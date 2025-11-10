package fr.flaily.xynon.click.classic;

import fr.flaily.xynon.click.classic.frame.CategoryFrame;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.AnimFloat.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

public class ClassicGUI extends GuiScreen {
    public static ClassicGUI INSTANCE;

    public ArrayList<CategoryFrame> frames = new ArrayList<>();
    public AnimFloat openAnim = new AnimFloat(0.0F, 0.1F, Easing.EASE_IN_OUT);

    public ScaledResolution sr;
    public ClassicGUI() {
        sr = new ScaledResolution(Minecraft.getMinecraft());

        INSTANCE = this;

        // Leave room for title
        int x = 35;
        int y = 100;

        // Done once on constructor to save positions and states of frames
        // even after closing the gui
        for(Module.Category category : Module.Category.values()) {
            CategoryFrame frame = new CategoryFrame(category, x, y);
            frames.add(frame);
            x += frame.width() + 12;
            // if the frame would be drawn too on the right
            if(x >= sr.getScaledWidth() - frame.width() - 12 - 10) {
                x = 35;
                // Reset the position, and jump below
                y += frame.maxHeight() + 24;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        frames.forEach(f -> f.handleMouseInput(dwheel));
    }

    @Override
    public void initGui() {
        super.initGui();
        openAnim.setTarget(1.0F);

        frames.forEach(CategoryFrame::init);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        frames.forEach(f -> f.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        frames.forEach(f -> f.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        super.keyTyped(typedChar, keyCode);
        // remove super to not have escape close the gui, but start an animation and when this ends, close the gui

        if (keyCode == 1)
        {
            this.openAnim.setTarget(0.0f);
            frames.forEach(CategoryFrame::startClosing);

            return;
            // Escape key
        }

        frames.forEach(f -> f.keyTyped(keyCode));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.openAnim.update(partialTicks);
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0, (int) (150 * openAnim.getValue())).getRGB());

        for (CategoryFrame f : frames) {
            f.render(mouseX, mouseY, partialTicks);
        }

        if (frames.stream().allMatch(f -> f.getAnim().getValue() <= 0.01f)) {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
