package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.ActiveTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TaskWidget extends Widget {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/task.png");

    private static final int COUNT = 4;

    private List<Element<?>> elements;
    private ActiveTask task;
    private int page;
    private boolean showProgress;
    private ResourceLocation background;
    private Consumer<ActiveTask> onInfoClick;

    public TaskWidget(int x, int y, ActiveTask task, boolean showProgress, @Nullable Consumer<ActiveTask> onInfoClick, ResourceLocation background) {
        super(x, y, 106, 104, new StringTextComponent(""));
        if (background != null) {
            this.background = background;
        } else {
            this.background = BACKGROUND;
        }
        this.task = task;
        this.showProgress = showProgress;
        this.onInfoClick = onInfoClick;
        elements = new ArrayList<>();

        for (de.maxhenkel.delivery.tasks.Item item : task.getTask().getItems()) {
            elements.add(new Element<>(item.getItem(), 0, item.getAmount()));
        }

        for (de.maxhenkel.delivery.tasks.Fluid fluid : task.getTask().getFluids()) {
            elements.add(new Element<>(fluid.getItem(), 0, fluid.getAmount()));
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

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.fontRenderer;
        mc.getTextureManager().bindTexture(background);
        RenderSystem.color4f(1F, 1F, 1F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        blit(matrixStack, x, y, 0, 0, width, height);

        if (onInfoClick != null) {
            if (isInfoHovered(mouseX, mouseY)) {
                blit(matrixStack, x + width - 13, y + 2, 142, 0, 11, 11);
            } else {
                blit(matrixStack, x + width - 13, y + 2, 153, 0, 11, 11);
            }
        }

        if (hasPrev()) {
            if (isLeftButtonHovered(mouseX, mouseY)) {
                blit(matrixStack, x + 10, y + height - 15, 124, 11, 18, 11);
            } else {
                blit(matrixStack, x + 10, y + height - 15, 106, 11, 18, 11);
            }
        }
        if (hasNext()) {
            if (isRightButtonHovered(mouseX, mouseY)) {
                blit(matrixStack, x + width - 35, y + height - 15, 124, 0, 18, 11);
            } else {
                blit(matrixStack, x + width - 35, y + height - 15, 106, 0, 18, 11);
            }
        }

        drawCentered(matrixStack, font, new TranslationTextComponent("message.delivery.task_items").mergeStyle(TextFormatting.DARK_GRAY), y + 4);

        int xPos = 8;
        int yPos = 15;
        int w = width - 10;
        int h = 16;
        for (int i = page * COUNT; i < Math.min(page * COUNT + COUNT, elements.size()); i++) {
            Element<?> element = elements.get(i);
            Object o = get(element.item);
            ItemStack itemStack = null;
            FluidStack fluidStack = null;
            IFormattableTextComponent str = null;
            if (o instanceof Item) {
                itemStack = new ItemStack((Item) o);
                mc.getItemRenderer().renderItemAndEffectIntoGUI(mc.player, itemStack, x + xPos, y + yPos);
                if (showProgress) {
                    str = new TranslationTextComponent("tooltip.delivery.progress", getNumberItems(element.current), getNumberItems(element.max));
                } else {
                    str = new StringTextComponent(getNumberItems(element.max));
                }
            } else if (o instanceof Fluid) {
                fluidStack = new FluidStack((Fluid) o, 1000);
                mc.getItemRenderer().renderItemAndEffectIntoGUI(mc.player, new ItemStack(fluidStack.getFluid().getFilledBucket()), x + xPos, y + yPos);//TODO
                if (showProgress) {
                    str = new TranslationTextComponent("tooltip.delivery.progress", getNumberBuckets(element.current), getNumberBuckets(element.max));
                } else {
                    str = new StringTextComponent(getNumberBuckets(element.max));
                }
            }

            if (!showProgress) {
                str = str.mergeStyle(TextFormatting.DARK_GRAY);
            } else if (element.current <= 0) {
                str = str.mergeStyle(TextFormatting.DARK_GRAY);
            } else if (element.current >= element.max) {
                str = str.mergeStyle(TextFormatting.DARK_GREEN);
            } else {
                str = str.mergeStyle(TextFormatting.DARK_RED);
            }
            font.func_243248_b(matrixStack, str, x + xPos + 20, y + yPos + 5, 0);

            if (mouseX >= xPos + x && mouseX < xPos + x + 16) {
                if (mouseY >= yPos + y && mouseY < yPos + y + h) {
                    List<ITextComponent> tooltip = null;
                    if (itemStack != null) {
                        tooltip = mc.currentScreen.getTooltipFromItem(itemStack);
                    } else if (fluidStack != null) {
                        tooltip = new ArrayList<>();
                        tooltip.add(new StringTextComponent("").append(fluidStack.getDisplayName()).mergeStyle(TextFormatting.WHITE));
                        if (mc.gameSettings.advancedItemTooltips) {
                            tooltip.add((new StringTextComponent(fluidStack.getFluid().getRegistryName().toString())).mergeStyle(TextFormatting.DARK_GRAY));
                        }
                    }

                    if (element.item.getAllElements().size() > 1) {
                        tooltip.add(new TranslationTextComponent("tooltip.delivery.tag", element.item.getName().toString()));
                    }
                    mc.currentScreen.renderWrappedToolTip(matrixStack, tooltip, mouseX, mouseY, font);
                }
            }
            if (mouseX >= xPos + x + 16 && mouseX < xPos + x + w - 16) {
                if (mouseY >= yPos + y && mouseY < yPos + y + h) {
                    List<ITextComponent> tooltip = new ArrayList<>();
                    if (itemStack != null) {
                        if (showProgress) {
                            tooltip.add(new TranslationTextComponent("tooltip.delivery.item_progress", element.current, element.max));
                        } else {
                            tooltip.add(new TranslationTextComponent("tooltip.delivery.item_amount", element.max));
                        }
                    } else if (fluidStack != null) {
                        if (showProgress) {
                            tooltip.add(new TranslationTextComponent("tooltip.delivery.fluid_progress", element.current, element.max));
                        } else {
                            tooltip.add(new TranslationTextComponent("tooltip.delivery.fluid_amount", element.max));
                        }
                    }
                    mc.currentScreen.renderWrappedToolTip(matrixStack, tooltip, mouseX, mouseY, font);
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


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasPrev() && isLeftButtonHovered((int) mouseX, (int) mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundHandler());
            prevPage();
            return true;
        }
        if (hasNext() && isRightButtonHovered((int) mouseX, (int) mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundHandler());
            nextPage();
            return true;
        }
        if (onInfoClick != null && isInfoHovered((int) mouseX, (int) mouseY)) {
            playDownSound(Minecraft.getInstance().getSoundHandler());
            onInfoClick.accept(task);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    private static <T> T get(ITag.INamedTag<T> tag) {
        long time = Minecraft.getInstance().world.getGameTime();
        List<T> allElements = tag.getAllElements();
        return allElements.get((int) (time / 20L % allElements.size()));
    }

    protected void drawCentered(MatrixStack matrixStack, FontRenderer font, IFormattableTextComponent text, int y) {
        int w = font.getStringPropertyWidth(text);
        font.func_243248_b(matrixStack, text, x + width / 2F - w / 2F, y, 0);
    }

    private static String getNumberItems(long num) {
        if (num < 1000) {
            return "" + num;
        }
        float n = ((float) num) / 1000F;
        if (n < 1000) {
            return String.format("%.1f", n) + "k";
        }
        n = n / 1000;
        if (n < 1000) {
            return String.format("%.1f", n) + "M";
        }
        n = n / 1000;
        return String.format("%.1f", n) + "T";
    }

    private static String getNumberBuckets(long num) {
        if (num < 1000) {
            return "" + num + "mB";
        }
        float n = ((float) num) / 1000F;
        return String.format("%.1f", n) + "B";
    }

    private static class Element<T> {
        private ITag.INamedTag<T> item;
        private long current, max;

        public Element(ITag.INamedTag<T> item, long current, long max) {
            this.item = item;
            this.current = current;
            this.max = max;
        }
    }

}
