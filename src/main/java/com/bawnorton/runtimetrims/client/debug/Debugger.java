package com.bawnorton.runtimetrims.client.debug;

import com.bawnorton.runtimetrims.RuntimeTrims;
import com.bawnorton.runtimetrims.client.RuntimeTrimsClient;
import com.bawnorton.runtimetrims.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import javax.imageio.ImageIO;
import net.minecraft.resource.Resource;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class Debugger {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Path RT_DEBUG = Platform.getGameDir().resolve("rt-debug");

    public static void createImage(String path, BufferedImage image) {
        if(!RuntimeTrimsClient.debug) return;
        try {
            File file = RT_DEBUG.resolve(path).toFile();
            file.mkdirs();
            file.createNewFile();
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            RuntimeTrims.LOGGER.error("Could not create debug file", e);
        }
    }

    public static void createJson(String path, Resource resource) {
        if(!RuntimeTrimsClient.debug) return;
        try {
            path = path.replace(":", File.separator);
            JsonReader reader = new JsonReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            JsonElement jsonElement = JsonParser.parseReader(reader);

            int lastSlash = path.lastIndexOf('/');
            String dir = path.substring(0, lastSlash);
            Path dirPath = RT_DEBUG.resolve(dir);
            File dirFile = dirPath.toFile();
            dirFile.mkdirs();
            Path filePath = dirPath.resolve(path.substring(lastSlash + 1));
            File file = filePath.toFile();
            file.createNewFile();

            try (FileWriter fileWriter = new FileWriter(file)) {
                GSON.toJson(jsonElement, fileWriter);
            }
        } catch (IOException e) {
            RuntimeTrims.LOGGER.error("Could not create debug file", e);
        }
    }
}
