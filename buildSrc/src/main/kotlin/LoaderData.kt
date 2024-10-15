import org.gradle.api.Project

class LoaderData(private val project: Project, private val name: String) {
    private val isFabric = name == "fabric"
    private val isNeoForge = name == "neoforge"

    fun getVersion() : String = if (isNeoForge) {
        project.property("neoforge_loader").toString()
    } else {
        project.property("fabric_loader").toString()
    }

    override fun toString(): String {
        return name
    }

    fun neoforge(container: () -> Unit) {
        if(isNeoForge) container.invoke()
    }

    fun fabric(container: () -> Unit) {
        if(isFabric) container.invoke()
    }

    fun isFabric(): Boolean = isFabric

    fun isNeoForge(): Boolean = isNeoForge
}