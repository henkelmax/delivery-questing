package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.helpers.Triple;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.corelib.item.NonNullListCollector;
import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.advancements.ModTriggers;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.items.ContractItem;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.items.SealedEnvelopeItem;
import de.maxhenkel.delivery.net.MessageChallengeToast;
import de.maxhenkel.delivery.net.MessageEMailToast;
import de.maxhenkel.delivery.net.MessageTaskCompletedToast;
import de.maxhenkel.delivery.tasks.email.ContractEMail;
import de.maxhenkel.delivery.tasks.email.EMail;
import de.maxhenkel.delivery.tasks.email.OfferEMail;
import de.maxhenkel.delivery.tasks.email.QuestsFinishedEMail;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private String name;
    private String password;
    private List<UUID> members;
    private List<TaskProgress> tasks;
    private List<UUID> completedTasks;
    private long experience;
    private long balance;
    private NonNullList<ItemStack> mailboxInbox;
    private NonNullList<ItemStack> pendingInbox;
    private NonNullList<ItemStack> pendingDeliveries;
    private List<EMail> eMails;
    private boolean isInEndgame;

    public Group(String name, String password) {
        this();
        this.name = name;
        this.password = password;
        initInitialInbox();
    }

    public Group() {
        this.id = UUID.randomUUID();
        this.members = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        this.experience = 0L;
        this.balance = 0L;
        this.mailboxInbox = NonNullList.withSize(4, ItemStack.EMPTY);
        this.pendingInbox = NonNullList.create();
        this.pendingDeliveries = NonNullList.create();
        this.eMails = new ArrayList<>();
        this.isInEndgame = false;
    }

    public UUID getId() {
        return id;
    }

    public List<TaskProgress> getTasks() {
        return tasks;
    }

    public void addTask(UUID taskID) {
        if (canAcceptTask(taskID)) {
            tasks.add(new TaskProgress(taskID, experience));
            if (Main.TASK_MANAGER.isEndgameTask(taskID)) {
                eMails.removeIf(eMail -> {
                    if (!(eMail instanceof ContractEMail)) {
                        return false;
                    }
                    ContractEMail contractEMail = (ContractEMail) eMail;
                    return contractEMail.getTaskID().equals(taskID);
                });
            }
        }
    }

    public List<UUID> getCompletedTasks() {
        return completedTasks;
    }

    public void addMember(UUID member) throws CommandException {
        if (members.stream().anyMatch(uuid -> uuid.equals(member))) {
            throw new CommandException(new TranslationTextComponent("command.delivery.already_member"));
        }
        members.add(member);
    }

    public boolean removeMember(UUID member) {
        return members.removeIf(uuid -> uuid.equals(member));
    }

    public boolean isMember(UUID player) {
        return members.stream().anyMatch(uuid -> uuid.equals(player));
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public long getExperience() {
        return experience;
    }

    public void addExperience(int experience) {
        setExperience(this.experience + experience);
    }

    public void setExperience(long experience) {
        int levelBefore = (int) getLevel();
        this.experience = experience;
        int levelAfter = (int) getLevel();

        forEachOnlineMember(playerEntity -> {
            ModTriggers.LEVEL_TRIGGER.trigger(playerEntity, (int) getLevel());
        });

        List<Task> forcedTasks = Main.TASK_MANAGER.getTasks().stream()
                .filter(Task::isForced)
                .filter(task -> task.getMinLevel() > levelBefore && task.getMinLevel() <= levelAfter)
                .filter(task -> getCompletedTasks().stream().noneMatch(uuid -> uuid.equals(task.getId()))) // Filter for completed tasks
                .filter(task -> getActiveTasks().getTasks().stream().noneMatch(activeTask -> activeTask.getTask().getId().equals(task.getId()))) // Filter for accepted tasks
                .filter(task -> getUnacceptedEmailTasks().noneMatch(uuid -> uuid.equals(task.getId()))) // Filter for unaccepted tasks in emails
                .collect(Collectors.toList());

        for (Task task : forcedTasks) {
            MessageChallengeToast msg = new MessageChallengeToast(task);

            IFormattableTextComponent txt = new TranslationTextComponent("message.delivery.challenge_contract")
                    .appendString(" ")
                    .append(TextComponentUtils.wrapWithSquareBrackets(
                            new TranslationTextComponent("message.delivery.view_contract").modifyStyle(style -> {
                                return style
                                        .applyFormatting(TextFormatting.GREEN)
                                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/group showtask " + task.getId().toString()))
                                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("message.delivery.click_to_view_contract")));
                            })
                    ).mergeStyle(TextFormatting.GREEN));

            forEachOnlineMember(player -> {
                NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, msg);
                player.sendMessage(txt, Util.DUMMY_UUID);
            });

            addTask(task.getId());
        }

        if (levelAfter > levelBefore) {
            if (levelBefore + 1 >= Main.SERVER_CONFIG.minComputerLevel.get() && levelAfter <= Main.SERVER_CONFIG.minComputerLevel.get()) {
                onReachComputerAge();
            }

            List<Offer> newOffers = Main.OFFER_MANAGER.getNewOffers(levelBefore + 1, levelAfter);
            newOffers.forEach(offer -> addEMail(new OfferEMail(this, offer)));
        }
    }

    public void onReachComputerAge() {
        ItemStack parcel = new ItemStack(ModItems.SEALED_PARCEL);
        ModItems.SEALED_PARCEL.setContents(parcel, NonNullList.from(ItemStack.EMPTY, new ItemStack(ModBlocks.COMPUTER)));
        ModItems.SEALED_PARCEL.setSender(parcel, new TranslationTextComponent("message.delivery.unknown"));
        addItemToInbox(parcel);
    }

    public void initInitialInbox() {
        ItemStack parcel = new ItemStack(ModItems.SEALED_PARCEL);
        ModItems.SEALED_PARCEL.setContents(parcel, NonNullList.from(ItemStack.EMPTY, new ItemStack(ModItems.ENVELOPE, 8), new ItemStack(ModItems.PARCEL, 4)));
        ModItems.SEALED_PARCEL.setSender(parcel, new TranslationTextComponent("message.delivery.unknown"));
        addItemToInbox(parcel);
    }

    public NonNullList<ItemStack> getMailboxInbox() {
        return mailboxInbox;
    }

    public NonNullList<ItemStack> getPendingInbox() {
        return pendingInbox;
    }

    public NonNullList<ItemStack> getPendingDeliveries() {
        return pendingDeliveries;
    }

    public List<EMail> getEMails() {
        return eMails;
    }

    public int getUnreadEMailCount() {
        return (int) eMails.stream().filter(eMail -> !eMail.isRead()).count();
    }

    public void addEMail(EMail eMail) {
        eMails.add(eMail);
        MessageEMailToast msg = new MessageEMailToast(eMail);
        forEachOnlineMember(player -> {
            NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, msg);
        });
    }

    @Nullable
    public EMail getEMail(UUID mailID) {
        return eMails.stream().filter(eMail -> eMail.getId().equals(mailID)).findAny().orElse(null);
    }

    public void validateEMails() {
        eMails.removeIf(eMail -> !eMail.isValid());
    }

    public void markEMailRead(UUID mailID) {
        EMail mail = getEMail(mailID);
        if (mail != null) {
            mail.setRead(true);
        }
    }

    public void addPendingDelivery(ItemStack stack) {
        pendingDeliveries.add(stack);
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void addBalance(long balance) {
        this.balance += balance;
    }

    public void addItemToInbox(ItemStack stack) {
        pendingInbox.add(stack);
    }

    public boolean canAcceptTask(UUID taskID) {
        if (completedTasks.stream().anyMatch(uuid -> uuid.equals(taskID))) {
            return false;
        }
        if (tasks.stream().anyMatch(taskProgress -> taskProgress.getTaskID().equals(taskID))) {
            return false;
        }
        return true;
    }

    public ActiveTasks getActiveTasks() {
        List<ActiveTask> t = new ArrayList<>();
        for (TaskProgress taskProgress : tasks) {
            Task task = Main.TASK_MANAGER.getTask(taskProgress.getTaskID(), (int) getLevel(taskProgress.getExperienceStarted()));
            if (task != null) {
                t.add(new ActiveTask(task, taskProgress));
            }
        }
        return new ActiveTasks(t);
    }

    public void tick(MinecraftServer server) {
        if (!isInEndgame) {
            if (server.getTickCounter() % 1200 == 0 && getLevel() < Main.SERVER_CONFIG.minComputerLevel.get()) {
                generateMailboxTask();
            }
            if (server.getTickCounter() % 3600 == 0 && getLevel() >= Main.SERVER_CONFIG.minComputerLevel.get()) {
                generateEMailTask();
            }
        } else {
            if (server.getTickCounter() % 3600 == 0) {
                generateEndGameTask();
            }
        }

        if (server.getWorld(World.OVERWORLD).getDayTime() % 24000 == 20) {
            pendingDeliveries.forEach(this::addItemToInbox);
            pendingDeliveries.clear();
        }

        inboxLoop:
        while (!pendingInbox.isEmpty()) {
            for (int i = 0; i < mailboxInbox.size(); i++) {
                if (mailboxInbox.get(i).isEmpty()) {
                    mailboxInbox.set(i, pendingInbox.remove(0));
                    continue inboxLoop;
                }
            }
            break;
        }
    }

    public void onBuyOffer(UUID offerID) {
        Offer offer = Main.OFFER_MANAGER.getOffer(offerID);
        if (offer == null) {
            return;
        }
        if (offer.getPrice() > balance) {
            return;
        }
        if (offer.getLevelRequirement() > (int) getLevel()) {
            return;
        }
        ItemStack stack = offer.getStack();
        if (stack.isEmpty()) {
            return;
        }

        balance -= offer.getPrice();
        ItemStack parcel = new ItemStack(ModItems.SEALED_PARCEL);
        NonNullList<ItemStack> items = NonNullList.create();

        int count = offer.isForEveryMember() ? members.size() : 1;

        for (int i = 0; i < count; i++) {
            ItemStack s = stack.copy();
            if (s.getCount() > s.getMaxStackSize()) {
                while (!s.isEmpty()) {
                    items.add(s.split(s.getMaxStackSize()));
                }
            } else {
                items.add(s);
            }
        }

        ModItems.SEALED_PARCEL.setContents(parcel, items);
        ModItems.SEALED_PARCEL.setSender(parcel, new TranslationTextComponent("tooltip.delivery.minazon"));
        addPendingDelivery(parcel);
    }

    public void generateMailboxTask() {
        if (!hasTaskInMailbox() && getActiveTasks().getTasks().size() < 3) {
            Task task = generateNewTask();
            if (task != null) {
                addItemToInbox(SealedEnvelopeItem.createTask(task));
            }
        }
    }

    public void generateEMailTask() {
        if (getUnacceptedTasksInEmails() < 3 && getActiveTasks().getTasks().size() < 5) {
            Task task = generateNewTask();
            if (task != null) {
                addEMail(new ContractEMail(this, task));
            }
        }
    }

    public void generateEndGameTask() {
        if (getUnacceptedTasksInEmails() < 1 && getActiveTasks().getTasks().size() < 3) {
            Task task = generateNewEndgameTask();
            if (task != null) {
                addEMail(new ContractEMail(this, task));
            }
        }
    }

    @Nullable
    public Task generateNewEndgameTask() {
        List<EndGameTask> possibleTasks = Main.TASK_MANAGER.getEndgameTasks().stream()
                .filter(task -> getActiveTasks().getTasks().stream().noneMatch(activeTask -> activeTask.getTask().getId().equals(task.getId()))) // Filter for accepted tasks
                .filter(task -> getUnacceptedEmailTasks().noneMatch(uuid -> uuid.equals(task.getId()))) // Filter for unaccepted tasks in emails
                .collect(Collectors.toList());

        if (possibleTasks.isEmpty()) {
            Main.LOGGER.warn("Could not find a new end game task for group '{}'", getName());
            return null;
        }

        return possibleTasks.get(Main.TASK_MANAGER.getRandom().nextInt(possibleTasks.size())).toTask((int) getLevel());
    }

    @Nullable
    public Task generateNewTask() {
        List<Task> possibleTasks = Main.TASK_MANAGER.getTasks().stream()
                .filter(task -> task.getMinLevel() <= getLevel())
                .filter(task -> task.getDependencies().stream().allMatch(this::hasCompletedTask))
                .filter(task -> getCompletedTasks().stream().noneMatch(uuid -> uuid.equals(task.getId()))) // Filter for completed tasks
                .filter(task -> getActiveTasks().getTasks().stream().noneMatch(activeTask -> activeTask.getTask().getId().equals(task.getId()))) // Filter for accepted tasks
                .filter(task -> getUnacceptedEmailTasks().noneMatch(uuid -> uuid.equals(task.getId()))) // Filter for unaccepted tasks in emails
                .collect(Collectors.toList());

        if (possibleTasks.isEmpty()) {
            Main.LOGGER.warn("Could not find a new task for group '{}'", getName());
            return null;
        }

        return possibleTasks.get(Main.TASK_MANAGER.getRandom().nextInt(possibleTasks.size()));
    }

    public boolean hasCompletedTask(UUID taskID) {
        return completedTasks.stream().anyMatch(uuid -> uuid.equals(taskID));
    }

    public boolean hasTaskInMailbox() {
        return getMailboxInbox().stream().anyMatch(stack -> getTaskID(stack) != null);
    }

    public long getUnacceptedTasksInEmails() {
        return getUnacceptedEmailTasks().count();
    }

    public Stream<UUID> getUnacceptedEmailTasks() {
        return getEMails()
                .stream()
                .filter(eMail -> eMail instanceof ContractEMail)
                .map(ContractEMail.class::cast)
                .map(ContractEMail::getTaskID)
                .filter(this::canAcceptTask);
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

    public void handInTaskItems(NonNullList<ItemStack> items) {
        NonNullList<ItemStack> taskItems = NonNullList.create();
        NonNullList<FluidStack> taskFluids = NonNullList.create();

        for (ItemStack stack : items) {
            ITaskContainer taskContainer = getTaskContainer(stack);
            if (taskContainer != null) {
                taskItems.addAll(taskContainer.getItems(stack));
                taskFluids.addAll(taskContainer.getFluids(stack));
            } else {
                taskItems.add(stack);
            }
        }

        for (ItemStack stack : taskItems) {
            for (int i = 0; i < tasks.size(); i++) {
                if (!putItemStack(stack)) {
                    break;
                }
            }
        }

        for (FluidStack stack : taskFluids) {
            for (int i = 0; i < tasks.size(); i++) {
                if (!putFluidStack(stack)) {
                    break;
                }
            }
        }

        List<UUID> toRemove = new ArrayList<>();
        for (TaskProgress taskProgress : new ArrayList<>(tasks)) {
            if (isFinished(taskProgress)) {
                onTaskCompleted(taskProgress);
                toRemove.add(taskProgress.getTaskID());
            }
        }
        tasks.removeIf(taskProgress -> toRemove.contains(taskProgress.getTaskID()));
    }

    @Nullable
    public static ITaskContainer getTaskContainer(ItemStack stack) {
        if (stack.getItem() instanceof ITaskContainer) {
            return (ITaskContainer) stack.getItem();
        } else if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ITaskContainer) {
            return (ITaskContainer) ((BlockItem) stack.getItem()).getBlock();
        } else {
            return null;
        }
    }

    public void onTaskCompleted(TaskProgress taskProgress) {
        if (!Main.TASK_MANAGER.isEndgameTask(taskProgress.getTaskID())) {
            completedTasks.add(taskProgress.getTaskID());
        }

        Task task = taskProgress.findTask();

        if (task == null) {
            return;
        }

        addExperience(task.getExperience());
        addBalance(task.getMoney());

        NonNullList<ItemStack> rewards = task.getRewards().stream().filter(Objects::nonNull).filter(stack -> !stack.isEmpty()).collect(NonNullListCollector.toNonNullList());

        if (!rewards.isEmpty()) {
            ItemStack stack = ModItems.SEALED_PARCEL.setSender(ModItems.SEALED_PARCEL.setContents(new ItemStack(ModItems.SEALED_PARCEL), rewards), new StringTextComponent(task.getContractorName()));
            addItemToInbox(stack);
        }

        broadcastPacket(new MessageTaskCompletedToast(task));

        if (!isInEndgame && areAllTasksCompleted()) {
            addEMail(new QuestsFinishedEMail(this));
            isInEndgame = true;
        }
    }

    public boolean areAllTasksCompleted() {
        return Main.TASK_MANAGER.getTasks().stream().allMatch(task -> hasCompletedTask(task.getId()));
    }

    public void broadcastPacket(Message<?> message) {
        forEachOnlineMember(player -> NetUtils.sendTo(Main.SIMPLE_CHANNEL, player, message));
    }

    public void forEachOnlineMember(Consumer<ServerPlayerEntity> playerEntityConsumer) {
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        for (UUID member : members) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(member);
            if (player != null) {
                playerEntityConsumer.accept(player);
            }
        }
    }

    private boolean isFinished(TaskProgress taskProgress) {
        Task task = taskProgress.findTask();

        if (task == null) {
            return true; //TODO check
        }

        boolean itemsFinished = task.getItems().stream().allMatch(item -> getCurrentAmount(taskProgress, item) >= item.amount);
        boolean fluidsFinished = task.getFluids().stream().allMatch(fluid -> getCurrentAmount(taskProgress, fluid) >= fluid.amount);

        return itemsFinished && fluidsFinished;
    }

    private boolean putItemStack(ItemStack stack) {
        Triple<Task, TaskProgress, ItemElement> triple = findTask(stack);
        if (triple == null) {
            return false;
        }
        long itemsNeeded = triple.getValue3().amount - getCurrentAmount(triple.getValue2(), triple.getValue3());

        int insertAmount = (int) Math.min(itemsNeeded, stack.getCount());

        ItemStack insertStack = stack.split(insertAmount);

        triple.getValue2().add(insertStack);

        return !stack.isEmpty();
    }

    private boolean putFluidStack(FluidStack stack) {
        Triple<Task, TaskProgress, FluidElement> triple = findTask(stack);
        if (triple == null) {
            return false;
        }
        long itemsNeeded = triple.getValue3().amount - getCurrentAmount(triple.getValue2(), triple.getValue3());

        int insertAmount = (int) Math.min(itemsNeeded, stack.getAmount());

        FluidStack insertStack = stack.copy();
        insertStack.setAmount(insertAmount);
        stack.setAmount(stack.getAmount() - insertAmount);

        triple.getValue2().add(insertStack);

        return !stack.isEmpty();
    }

    public static long getCurrentAmount(TaskProgress progress, ItemElement element) {
        return progress.getTaskItems().stream().filter(stack -> stack.getItem().isIn(element.item)).filter(stack -> NBTUtil.areNBTEquals(element.getNbt(), stack.getTag(), true)).map(stack -> (long) stack.getCount()).reduce(0L, Long::sum);
    }

    public static long getCurrentAmount(TaskProgress progress, FluidElement element) {
        return progress.getTaskFluids().stream().filter(stack -> stack.getFluid().isIn(element.item)).filter(stack -> NBTUtil.areNBTEquals(element.getNbt(), stack.getTag(), true)).map(stack -> (long) stack.getAmount()).reduce(0L, Long::sum);
    }

    @Nullable
    private Triple<Task, TaskProgress, ItemElement> findTask(ItemStack stack) {
        for (TaskProgress taskProgress : tasks) {
            Task task = taskProgress.findTask();
            if (task == null) {
                continue;
            }
            Optional<ItemElement> item = task.getItems().stream().filter(i -> stack.getItem().isIn(i.getItem())).filter(e -> NBTUtil.areNBTEquals(e.getNbt(), stack.getTag(), true)).findAny();
            if (item.isPresent()) {
                return new Triple<>(task, taskProgress, item.get());
            }
        }

        return null;
    }

    @Nullable
    private Triple<Task, TaskProgress, FluidElement> findTask(FluidStack stack) {
        for (TaskProgress taskProgress : tasks) {
            Task task = taskProgress.findTask();
            if (task == null) {
                continue;
            }
            Optional<FluidElement> item = task.getFluids().stream().filter(i -> stack.getFluid().isIn(i.getItem())).filter(e -> NBTUtil.areNBTEquals(e.getNbt(), stack.getTag(), true)).findAny();
            if (item.isPresent()) {
                return new Triple<>(task, taskProgress, item.get());
            }
        }

        return null;
    }

    public float getLevel() {
        return getLevel(experience);
    }

    public static float getLevel(long experience) {
        return (float) (Math.sqrt(0.2F * experience + 0.25F) - 0.5F);
    }

    public int getEndgameTaskLevel(UUID taskID) {
        return getActiveTasks().getTasks().stream().filter(activeTask -> activeTask.getTask().getId().equals(taskID)).map(activeTask -> (int) Group.getLevel(activeTask.getTaskProgress().getExperienceStarted())).findAny().orElse((int) getLevel());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId("ID", id);
        compound.putString("Name", name);
        compound.putString("Password", password);

        ListNBT memberList = new ListNBT();
        for (UUID member : members) {
            CompoundNBT memberTag = new CompoundNBT();
            memberTag.putUniqueId("Member", member);
            memberList.add(memberTag);
        }
        compound.put("Members", memberList);

        ListNBT taskList = new ListNBT();
        for (TaskProgress task : tasks) {
            taskList.add(task.serializeNBT());
        }
        compound.put("Tasks", taskList);

        ListNBT completedTasksList = new ListNBT();
        for (UUID t : completedTasks) {
            CompoundNBT taskTag = new CompoundNBT();
            taskTag.putUniqueId("ID", t);
            completedTasksList.add(taskTag);
        }
        compound.put("CompletedTasks", completedTasksList);

        compound.putLong("Experience", experience);
        compound.putLong("Balance", balance);

        ItemUtils.saveInventory(compound, "MailboxInbox", mailboxInbox);
        ItemUtils.saveItemList(compound, "PendingMailboxInbox", pendingInbox, false);
        ItemUtils.saveItemList(compound, "PendingDeliveries", pendingDeliveries, false);

        ListNBT eMailList = new ListNBT();
        for (EMail email : eMails) {
            eMailList.add(email.serializeNBT());
        }
        compound.put("EMails", eMailList);

        compound.putBoolean("Endgame", isInEndgame);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        this.id = compound.getUniqueId("ID");
        this.name = compound.getString("Name");
        this.password = compound.getString("Name");

        ListNBT memberList = compound.getList("Members", 10);
        this.members = new ArrayList<>();
        for (int i = 0; i < memberList.size(); i++) {
            this.members.add(memberList.getCompound(i).getUniqueId("Member"));
        }

        ListNBT taskList = compound.getList("Tasks", 10);
        this.tasks = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            TaskProgress task = new TaskProgress();
            task.deserializeNBT(taskList.getCompound(i));
            this.tasks.add(task);
        }

        ListNBT completedTasksList = compound.getList("CompletedTasks", 10);
        this.completedTasks = new ArrayList<>();
        for (int i = 0; i < completedTasksList.size(); i++) {
            this.completedTasks.add(completedTasksList.getCompound(i).getUniqueId("ID"));
        }

        this.experience = compound.getLong("Experience");
        this.balance = compound.getLong("Balance");

        this.mailboxInbox = NonNullList.withSize(4, ItemStack.EMPTY);
        ItemUtils.readInventory(compound, "MailboxInbox", mailboxInbox);
        this.pendingInbox = ItemUtils.readItemList(compound, "PendingMailboxInbox", false);
        this.pendingDeliveries = ItemUtils.readItemList(compound, "PendingDeliveries", false);

        ListNBT emailList = compound.getList("EMails", 10);
        this.eMails = new ArrayList<>();
        for (int i = 0; i < emailList.size(); i++) {
            EMail mail = EMail.deserialize(emailList.getCompound(i), this);
            if (mail != null) {
                this.eMails.add(mail);
            }
        }

        this.isInEndgame = compound.getBoolean("Endgame");
    }
}
