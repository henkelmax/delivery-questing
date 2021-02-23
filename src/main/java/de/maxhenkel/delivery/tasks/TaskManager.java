package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import de.maxhenkel.delivery.Main;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.util.INBTSerializable;
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

public class TaskManager implements INBTSerializable<CompoundNBT> {

    private List<Task> tasks;
    private List<EndGameTask> endgameTasks;
    private final Random random;

    public TaskManager(List<Task> tasks, List<EndGameTask> endgameTasks) {
        this();
        this.tasks = tasks;
        this.endgameTasks = endgameTasks;
    }

    public TaskManager() {
        this.random = new Random();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<EndGameTask> getEndgameTasks() {
        return endgameTasks;
    }

    public boolean isEndgameTask(UUID taskID) {
        return endgameTasks.stream().anyMatch(endGameTask -> endGameTask.getId().equals(taskID));
    }

    @Nullable
    public Task getTask(UUID uuid, Group group) {
        return getTask(uuid, group.getEndgameTaskLevel(uuid));
    }

    @Nullable
    public Task getTask(UUID uuid, int level) {
        return tasks
                .stream()
                .filter(task -> task.getId().equals(uuid))
                .findAny()
                .orElse(endgameTasks
                        .stream()
                        .filter(task -> task.getId().equals(uuid))
                        .map(endGameTask -> endGameTask.toTask(level))
                        .findAny()
                        .orElse(null)
                );
    }

    public static TaskManager load() throws IOException {
        Path tasks = FMLPaths.CONFIGDIR.get().resolve(Main.MODID).resolve("tasks.json");

        if (!tasks.toFile().exists()) {
            Main.LOGGER.warn("Could not find tasks.json");
            Main.LOGGER.warn("Continuing with empty tasks");
            return new TaskManager(new ArrayList<>(), new ArrayList<>());
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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();

        ListNBT taskList = new ListNBT();
        for (Task task : tasks) {
            taskList.add(task.serializeNBT());
        }
        compound.put("Tasks", taskList);

        ListNBT endgameTaskList = new ListNBT();
        for (EndGameTask task : endgameTasks) {
            endgameTaskList.add(task.serializeNBT());
        }
        compound.put("EndgameTasks", endgameTaskList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        ListNBT taskList = compound.getList("Tasks", 10);
        this.tasks = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            Task task = new Task();
            task.deserializeNBT(taskList.getCompound(i));
            this.tasks.add(task);
        }

        ListNBT endgameTaskList = compound.getList("EndgameTasks", 10);
        this.endgameTasks = new ArrayList<>();
        for (int i = 0; i < endgameTaskList.size(); i++) {
            EndGameTask task = new EndGameTask();
            task.deserializeNBT(endgameTaskList.getCompound(i));
            this.endgameTasks.add(task);
        }
    }

}
