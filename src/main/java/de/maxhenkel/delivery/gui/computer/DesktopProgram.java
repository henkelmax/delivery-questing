package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;

public class DesktopProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/desktop.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/computer/icons.png");
    private MutableComponent MINTERNET = new TranslatableComponent("tooltip.delivery.minternet");
    private MutableComponent MAIL = new TranslatableComponent("message.delivery.email");
    private MutableComponent NOTES = new TranslatableComponent("message.delivery.notes");

    private ScreenBase.HoverArea minternet;
    private ScreenBase.HoverArea mail;
    private ScreenBase.HoverArea notes;

    public DesktopProgram(ComputerScreen screen) {
        super(screen);
    }

    @Override
    protected void init() {
        super.init();

        minternet = new ScreenBase.HoverArea(16, 16, 32, 32, () -> Collections.singletonList(MINTERNET.withStyle(ChatFormatting.WHITE).getVisualOrderText()));
        addHoverArea(minternet);

        mail = new ScreenBase.HoverArea(64, 16, 32, 32, () -> Collections.singletonList(MAIL.withStyle(ChatFormatting.WHITE).getVisualOrderText()));
        addHoverArea(mail);

        notes = new ScreenBase.HoverArea(112, 16, 32, 32, () -> Collections.singletonList(NOTES.withStyle(ChatFormatting.WHITE).getVisualOrderText()));
        addHoverArea(notes);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        drawIcon(matrixStack, minternet, mouseX, mouseY, 0, 0, MINTERNET);
        drawIcon(matrixStack, mail, mouseX, mouseY, 0, 32, MAIL);
        drawIcon(matrixStack, notes, mouseX, mouseY, 0, 64, NOTES);

        drawCount(matrixStack, mail, getContainer().getGroup().getUnreadEMailCount());
        drawCount(matrixStack, notes, getContainer().getGroup().getActiveTasks().getTasks().size());

        screen.drawHoverAreas(matrixStack, mouseX, mouseY);
    }

    private void drawIcon(PoseStack matrixStack, ScreenBase.HoverArea hoverArea, int mouseX, int mouseY, int xOffset, int yOffset, MutableComponent name) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, ICONS);
        if (hoverArea.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset + hoverArea.getWidth(), yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        }
        screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset, yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        screen.drawCentered(matrixStack, name, hoverArea.getPosX() + hoverArea.getWidth() / 2 + 1, hoverArea.getPosY() + hoverArea.getHeight() + 2, ChatFormatting.DARK_GRAY.getColor());
        screen.drawCentered(matrixStack, name, hoverArea.getPosX() + hoverArea.getWidth() / 2, hoverArea.getPosY() + hoverArea.getHeight() + 1, ChatFormatting.WHITE.getColor());
    }

    private void drawCount(PoseStack matrixStack, ScreenBase.HoverArea hoverArea, int count) {
        if (count > 0) {
            MutableComponent num = new TextComponent(String.valueOf(count)).withStyle(ChatFormatting.DARK_RED);
            int w = mc.font.width(num);
            mc.font.draw(matrixStack, num, hoverArea.getPosX() + hoverArea.getWidth() - 1 - w, hoverArea.getPosY() + 4, 0);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minternet.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new MinazonProgram(screen, this));
            playClickSound();
            return true;
        }
        if (mail.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new MailProgram(screen, this));
            playClickSound();
            return true;
        }
        if (notes.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new NotesProgram(screen, this));
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
