package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.items.ContractItem;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.items.SealedEnvelopeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
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
import java.util.stream.Collectors;

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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Item.class, Item.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(Fluid.class, Fluid.DESERIALIZER);

        Gson customGson = gsonBuilder.create();
        BufferedReader bufferedReader = Files.newBufferedReader(tasks);
        return customGson.fromJson(bufferedReader, TaskManager.class);
    }

    public void onServerTick(MinecraftServer server) {
        Progression progression = Main.getProgression(server);
        for (Group group : progression.getGroups()) {
            if (server.getTickCounter() % 1200 == 0 && group.getLevel() < 10) {
                generateMailboxTask(group);
            }
        }
    }

    public void generateMailboxTask(Group group) {
        if (!hasTaskInMailbox(group) && group.getActiveTasks().getTasks().isEmpty()) {
            Task task = generateNewTask(group);
            if (task != null) {
                NonNullList<ItemStack> mailboxInbox = group.getMailboxInbox();
                for (int i = 0; i < mailboxInbox.size(); i++) {
                    if (mailboxInbox.get(i).isEmpty()) {
                        mailboxInbox.set(i, SealedEnvelopeItem.createTask(task.getId()));
                        break;
                    }
                }
            }
        }
    }

    @Nullable
    public Task generateNewTask(Group group) {
        List<Task> possibleTasks = tasks.stream()
                .filter(task -> task.getMinLevel() <= group.getLevel())
                .filter(task -> task.getMaxLevel() >= group.getLevel())
                .filter(task -> group.getCompletedTasks().stream().noneMatch(uuid -> uuid.equals(task.getId())))
                .filter(task -> group.getActiveTasks().getTasks().stream().noneMatch(activeTask -> activeTask.getTask().getId().equals(task.getId()))).collect(Collectors.toList());

        if (possibleTasks.isEmpty()) {
            Main.LOGGER.warn("Could not find a new task for group '{}'", group.getName());
            return null;
        }

        return possibleTasks.get(random.nextInt(possibleTasks.size()));
    }

    public boolean hasTaskInMailbox(Group group) {
        return group.getMailboxInbox().stream().anyMatch(stack -> getTaskID(stack) != null);
    }

    @Nullable
    public UUID getTaskID(ItemStack stack) {
        if (stack.getItem() instanceof SealedEnvelopeItem) {
            NonNullList<ItemStack> contents = ModItems.SEALED_ENVELOPE.getContents(stack);
            for (ItemStack s : contents) {
                if (s.getItem() instanceof ContractItem) {
                    UUID task = ModItems.CONTRACT.getTask(s);
                    if (task != null) {
                        return task;
                    }
                }
            }
        }
        return null;
    }


}
