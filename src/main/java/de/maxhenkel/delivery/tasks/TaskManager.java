package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import de.maxhenkel.delivery.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class TaskManager {

    private List<Task> tasks;
    private final Random random;

    public TaskManager(List<Task> tasks) {
        this();
        this.tasks = tasks;
    }

    public TaskManager() {
        this.random = new Random();
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

        Gson customGson = Deserializers.getGson();
        BufferedReader bufferedReader = Files.newBufferedReader(tasks);
        return customGson.fromJson(bufferedReader, TaskManager.class);
    }

    public void onServerTick(MinecraftServer server) {
        Progression progression = Main.getProgression(server);
        for (Group group : progression.getGroups()) {
            group.tick(server);
        }
    }

    public Random getRandom() {
        return random;
    }

}
