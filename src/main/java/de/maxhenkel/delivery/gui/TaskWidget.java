package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.helpers.AbstractStack;
import de.maxhenkel.corelib.helpers.Pair;
import de.maxhenkel.corelib.helpers.WrappedFluidStack;
import de.maxhenkel.corelib.helpers.WrappedItemStack;
import de.maxhenkel.corelib.tag.SingleElementTag;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskWidget {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/task.png");

    private static final int COUNT = 4;

    private int x, y, width, height;
    private Minecraft mc;
    private Font font;

    private List<Element<?>> elements;
    private ActiveTask task;
    private int page;
    private boolean showProgress;
    private ResourceLocation background;
    private Consumer<ActiveTask> onInfoClick;

    public TaskWidget(int x, int y, ActiveTask task, boolean showProgress, @Nullable Consumer<ActiveTask> onInfoClick, ResourceLocation background) {
        this.x = x;
        this.y = y;
        this.width = 106;
        this.height = 104;
        mc = Minecraft.getInstance();
        font = mc.font;
        if (background != null) {
            this.background = background;
        } else {
            this.background = BACKGROUND;
        }
        this.task = task;
        this.showProgress = showProgress;
        this.onInfoClick = onInfoClick;
        elements = new ArrayList<>();

        for (ItemElement item : task.getTask().getItems()) {
            elements.add(new Element<>(item, showProgress ? Group.getCurrentAmount(task.getTaskProgress(), item) : 0, item.getAmount()));
        }

        for (FluidElement fluid : task.getTask().getFluids()) {
            elements.add(new Element<>(fluid, showProgress ? Group.getCurrentAmount(task.getTaskProgress(), fluid) : 0, fluid.getAmount()));
        }

    }

    public TaskWidget(int x, int y, ActiveTask task, boolean showProgress, @Nullable Consumer<ActiveTask> onInfoClick) {
        this(x, y, task, showProgress, onInfoClick, null);
    }

    public void nextPage() {
        page = Math.floorMod(page + 1, getPages());
    }

    public void prevPage() {
        page = Math.floorMod(page - 1, getPages());
    }

    private int getPages() {
        int size = elements.size();
        return Math.max(1, (size / COUNT) + ((size % COUNT == 0) ? 0 : 1));
    }

    public boolean hasNext() {
        return page < getPages() - 1;
    }

    public boolean hasPrev() {
        return page > 0;
    }

    public void render(PoseStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, background);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiComponent.blit(matrixStack, x, y, 0, 0, width, height, 256, 256);

        if (onInfoClick != null) {
            if (isInfoHovered(mouseX, mouseY)) {
                GuiComponent.blit(matrixStack, x + width - 13, y + 2, 142, 0, 11, 11, 256, 256);
            } else {
                GuiComponent.blit(matrixStack, x + width - 13, y + 2, 153, 0, 11, 11, 256, 256);
            }
        }

        if (hasPrev()) {
            if (isLeftButtonHovered(mouseX, mouseY)) {
                GuiComponent.blit(matrixStack, x + 10, y + height - 15, 124, 11, 18, 11, 256, 256);
            } else {
                GuiComponent.blit(matrixStack, x + 10, y + height - 15, 106, 11, 18, 11, 256, 256);
            }
        }
        if (hasNext()) {
            if (isRightButtonHovered(mouseX, mouseY)) {
                GuiComponent.blit(matrixStack, x + width - 35, y + height - 15, 124, 0, 18, 11, 256, 256);
            } else {
                GuiComponent.blit(matrixStack, x + width - 35, y + height - 15, 106, 0, 18, 11, 256, 256);
            }
        }

        drawCentered(matrixStack, font, new TranslatableComponent("message.delivery.task_items").withStyle(ChatFormatting.DARK_GRAY), y + 4);

        int xPos = 8;
        int yPos = 15;
        int w = width - 10;
        int h = 16;
        for (int i = page * COUNT; i < Math.min(page * COUNT + COUNT, elements.size()); i++) {
            Element<?> element = elements.get(i);
            AbstractStack abstractStack = element.item.getAbstractStack();
            abstractStack.render(matrixStack, x + xPos, y + yPos);

            MutableComponent str;
            if (abstractStack instanceof WrappedFluidStack) {
                if (showProgress) {
                    str = new TranslatableComponent("tooltip.delivery.progress", getNumberBuckets(element.current), getNumberBuckets(element.max));
                } else {
                    str = new TextComponent(getNumberBuckets(element.max));
                }
            } else {
                if (showProgress) {
                    str = new TranslatableComponent("tooltip.delivery.progress", getNumberItems(element.current), getNumberItems(element.max));
                } else {
                    str = new TextComponent(getNumberItems(element.max));
                }
            }

            if (!showProgress) {
                str = str.withStyle(ChatFormatting.DARK_GRAY);
            } else if (element.current <= 0) {
                str = str.withStyle(ChatFormatting.DARK_GRAY);
            } else if (element.current >= element.max) {
                str = str.withStyle(ChatFormatting.DARK_GREEN);
            } else {
                str = str.withStyle(ChatFormatting.DARK_RED);
            }
            font.draw(matrixStack, str, x + xPos + 20, y + yPos + 5, 0);

            if (mouseX >= xPos + x && mouseX < xPos + x + 16) {
                if (mouseY >= yPos + y && mouseY < yPos + y + h) {
                    List<Component> tooltip = abstractStack.getTooltip(mc.screen);

                    if (element.item.getItem() != null && !(element.item.getItem() instanceof SingleElementTag)) {
                        tooltip.add(new TranslatableComponent("tooltip.delivery.tag", element.item.getItem().getName().toString()));
                    }
                    mc.screen.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY, font);
                }
            }
            if (mouseX >= xPos + x + 16 && mouseX < xPos + x + w - 16) {
                if (mouseY >= yPos + y && mouseY < yPos + y + h) {
                    List<Component> tooltip = new ArrayList<>();
                    if (abstractStack instanceof WrappedItemStack) {
                        if (showProgress) {
                            tooltip.add(new TranslatableComponent("tooltip.delivery.item_progress", element.current, element.max));
                        } else {
                            tooltip.add(new TranslatableComponent("tooltip.delivery.item_amount", element.max));
                        }
                    } else if (abstractStack instanceof WrappedFluidStack) {
                        if (showProgress) {
                            tooltip.add(new TranslatableComponent("tooltip.delivery.fluid_progress", element.current, element.max));
                        } else {
                            tooltip.add(new TranslatableComponent("tooltip.delivery.fluid_amount", element.max));
                        }
                    }
                    mc.screen.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY, font);
                }
            }

            yPos += 18;
        }
    }

    private boolean isLeftButtonHovered(int mouseX, int mouseY) {
        if (mouseX >= x + 10 && mouseX < x + 28) {
            if (mouseY >= y + height - 15 && mouseY < y + height - 4) {
                return true;
            }
        }
        return false;
    }

    private boolean isRightButtonHovered(int mouseX, int mouseY) {
        if (mouseX >= x + width - 35 && mouseX < x + width - 17) {
            if (mouseY >= y + height - 15 && mouseY < y + height - 4) {
                return true;
            }
        }
        return false;
    }

    private boolean isInfoHovered(int mouseX, int mouseY) {
        if (mouseX >= x + width - 13 && mouseX < x + width - 2) {
            if (mouseY >= y + 2 && mouseY < y + 13) {
                return true;
            }
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasPrev() && isLeftButtonHovered((int) mouseX, (int) mouseY)) {
            playClickSound();
            prevPage();
            return true;
        }
        if (hasNext() && isRightButtonHovered((int) mouseX, (int) mouseY)) {
            playClickSound();
            nextPage();
            return true;
        }
        if (onInfoClick != null && isInfoHovered((int) mouseX, (int) mouseY)) {
            playClickSound();
            onInfoClick.accept(task);
            return true;
        }
        return false;
    }

    private void playClickSound() {
        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public Object getIngredientUnderMouse(double mouseX, double mouseY) {
        int xPos = 8;
        int yPos = 15;
        int h = 16;

        for (int i = page * COUNT; i < Math.min(page * COUNT + COUNT, elements.size()); i++) {
            if (mouseX >= xPos + x && mouseX < xPos + x + 16) {
                if (mouseY >= yPos + y && mouseY < yPos + y + h) {
                    Element<?> element = elements.get(i);
                    if (element.item instanceof ItemElement) {
                        return new ItemStack((Item) element.item.getCurrentDisplayedElement());
                    } else if (element.item instanceof FluidElement) {
                        return new FluidStack((Fluid) element.item.getCurrentDisplayedElement(), 1000);
                    }
                }
            }
            yPos += 18;
        }

        return null;
    }

    public List<Pair<Rect2i, Object>> getIngredients() {
        List<Pair<Rect2i, Object>> ingredients = new ArrayList<>();
        int xPos = 8;
        int yPos = 15;

        for (int i = page * COUNT; i < Math.min(page * COUNT + COUNT, elements.size()); i++) {
            Element<?> element = elements.get(i);
            if (element.item instanceof ItemElement) {
                ingredients.add(new Pair<>(new Rect2i(xPos + x, yPos + y, 16, 16), new ItemStack((Item) element.item.getCurrentDisplayedElement())));
            } else if (element.item instanceof FluidElement) {
                ingredients.add(new Pair<>(new Rect2i(xPos + x, yPos + y, 16, 16), new FluidStack((Fluid) element.item.getCurrentDisplayedElement(), 1000)));
            }
            yPos += 18;
        }
        return ingredients;
    }

    protected void drawCentered(PoseStack matrixStack, Font font, MutableComponent text, int y) {
        int w = font.width(text);
        font.draw(matrixStack, text, x + width / 2F - w / 2F, y, 0);
    }

    // TODO localize
    private static String getNumberItems(long num) {
        if (num < 1000) {
            return "" + num;
        }
        float n = ((float) num) / 1000F;
        if (n < 1000F) {
            return String.format("%.1f", n) + " k";
        }
        n = n / 1000F;
        if (n < 1000F) {
            return String.format("%.1f", n) + " M";
        }
        n = n / 1000F;
        return String.format("%.1f", n) + " T";
    }

    private static String getNumberBuckets(long num) {
        if (num < 1000) {
            return num + " mB";
        }
        float n = ((float) num) / 1000F;
        if (n < 1000F) {
            return String.format("%.1f", n) + " B";
        }
        n = n / 1000F;
        if (n < 1000F) {
            return String.format("%.1f", n) + " kB";
        }
        n = n / 1000F;
        return String.format("%.1f", n) + " MB";
    }

    private static class Element<T> {
        private final TaskElement<T> item;
        private final long current, max;

        public Element(TaskElement<T> item, long current, long max) {
            this.item = item;
            this.current = current;
            this.max = max;
        }
    }

}
