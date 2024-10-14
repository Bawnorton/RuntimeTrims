@file:Suppress("UnstableApiUsage")

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    id("dev.kikugie.j52j") version "1.0.2"
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = MinecraftVersionData(stonecutter)

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://maven.shedaniel.me")
    maven("https://jitpack.io")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl")}+$minecraftVersion-$loader")
    annotationProcessor(modImplementation("com.bawnorton.configurable:configurable-$loader-yarn:${property("configurable")}") { isTransitive = false })

    modImplementation("maven.modrinth:iris:${property("iris")}")
    modImplementation("maven.modrinth:sodium:${property("sodium")}")
    modRuntimeOnly("org.antlr:antlr4-runtime:4.13.1")
    modRuntimeOnly("io.github.douira:glsl-transformer:2.0.1")
    modRuntimeOnly("org.anarres:jcpp:1.4.14")
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }

    runConfigs["client"].apply {
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
    }

    runs {
        afterEvaluate {
            val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
                componentFilter {
                    it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
                }
            }.files.first()

            configureEach {
                vmArg("-javaagent:$mixinJarFile")

                property("mixin.hotSwap", "true")
                property("mixin.debug.export", "true")
            }
        }
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 21
    }

    processResources {
        val modMetadata = mapOf(
            "description" to mod.description,
            "version" to mod.version,
            "minecraft_dependency" to mod.minecraftDependency,
            "minecraft_version" to minecraftVersion.toString(),
            "loader_version" to loader.getVersion()
        )

        inputs.properties(modMetadata)
        filesMatching("fabric.mod.json") { expand(modMetadata) }
        filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
    }

    processResources {
        val refmap = "refmap" to "${mod.name}-$minecraftVersion-$loader-refmap.json"
        inputs.properties(refmap)

        filesMatching("runtimetrims-compat.mixins.json5") { expand(refmap) }
    }

    withType<AbstractCopyTask> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    clean {
        delete(file(rootProject.file("build")))
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

loader.fabric {
    dependencies {
        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")

        include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-fabric:${property("mixin_squared")}")!!)!!)

        modImplementation("com.terraformersmc:modmenu:${property("mod_menu")}")

        modCompileOnly("maven.modrinth:show-me-your-skin:${property("show_me_your_skin")}")
        modCompileOnly("maven.modrinth:female-gender:${property("wildfire_gender")}")
        modCompileOnly("maven.modrinth:bclib:${property("bclib")}")
    }
}

loader.neoforge {
    dependencies {
        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
        })
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")

        compileOnly(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-common:${property("mixin_squared")}")!!)
        implementation(include("com.github.bawnorton.mixinsquared:mixinsquared-forge:${property("mixin_squared")}")!!)
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    val tag = "$loader-${mod.version}+$minecraftVersion"
    val branch = "main"
    changelog = "[Changelog](https://github.com/Bawnorton/${mod.name}/blob/$branch/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/${mod.name}"
        commitish = branch
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = mod.modrinthProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = mod.curseforgeProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }
}