package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.maxhenkel.delivery.Main;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private final List<Task> tasks;

    public TaskManager(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public static TaskManager load() throws IOException {
        Path tasks = FMLPaths.CONFIGDIR.get().resolve(Main.MODID).resolve("tasks.json");

        if (!tasks.toFile().exists()) {
            Main.LOGGER.warn("Could not find tasks.json");
            Main.LOGGER.warn("Continuing with empty tasks");
            return new TaskManager(new ArrayList<>());
        }

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Item.class, Item.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(Fluid.class, Fluid.DESERIALIZER);

        Gson customGson = gsonBuilder.create();
        BufferedReader bufferedReader = Files.newBufferedReader(tasks);
        return customGson.fromJson(bufferedReader, TaskManager.class);
    }

}
