package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.maxhenkel.delivery.Main;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskManager {

    private final List<Task> tasks;

    public TaskManager(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    @Nullable
    public Task getTask(UUID uuid) {
        return tasks.stream().filter(task -> task.getId().equals(uuid)).findAny().orElse(null);
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
