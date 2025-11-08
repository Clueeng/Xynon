package fr.flaily.xynon.click.classic.frame;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.click.classic.frame.module.ModuleFrame;
import fr.flaily.xynon.click.classic.frame.module.ModuleStyle;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import fr.flaily.xynon.utils.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class CategoryFrame implements CategoryStyle {
    public Module.Category category;
    public int x, y;
    public ArrayList<ModuleFrame> moduleFrames = new ArrayList<>();
    public AnimFloat scroll = new AnimFloat(0.0f, 0.12f, AnimFloat.Easing.LERP);

    private boolean dragging;
    private int dragX, dragY;

    @Getter
    private AnimFloat anim = new AnimFloat(0f, 0.04f, AnimFloat.Easing.EASE_OUT_BACK);

    @Getter
    public boolean showModules = true; // default expanded
    private AnimFloat moduleAnim = new AnimFloat(1f, 0.15f, AnimFloat.Easing.LERP);

    private boolean opening = true;
    @Getter
    private boolean closing = false;
    @Getter
    private boolean hovering;


    public CategoryFrame(Module.Category category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;

        float relY = 24;
        for (Module m : Xynon.INSTANCE.getModuleManager().getModules(category)) {
            ModuleFrame frame = new ModuleFrame(this, m, (int) relY);
            this.moduleFrames.add(frame);
            relY += frame.height();
        }
        init();
    }

    public void init() {
        opening = true;
        closing = false;
        anim.setValue(0.04f);
        anim.setTarget(1f);
        anim.setSpeed(0.04f);
    }

    public void startClosing() {
        opening = false;
        closing = true;
        anim.setTarget(0f);
        anim.setSpeed(0.08f);
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        anim.update(partialTicks);
        scroll.update(partialTicks, false);

        float scale = anim.getValue();
        if (scale <= 0f) return;

        float cx = x + width() / 2f;
        float cy = y + 12;


        GL11.glPushMatrix();
        GL11.glTranslatef(cx, cy, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-cx, -cy, 0);

        moduleAnim.update(partialTicks);
        float moduleScale = moduleAnim.getValue();

        float relY = 24; // starting below category head
        for (ModuleFrame m : moduleFrames) {
            GL11.glPushMatrix();

            // Translate visually based on module animation
            GL11.glTranslatef(0, -relY * (1f - moduleScale), 0);
            GL11.glTranslatef(0, scroll.getValue(), 0);
            // Scissor box
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.prepareScissorBox(x, y + 20, x + width(), y + maxHeight());

            if (moduleAnim.getValue() > 0.01f /*&& m.getY() > y + 8*/) {
                m.guiShown = true;
                m.render(mouseX, mouseY, partialTicks, scroll.getValue());
            } else {
                m.guiShown = false;
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();

            // Update logical relY for module
            m.relY = relY + scroll.getValue();

            // Use animated height for spacing so lower modules follow animation
            float animatedHeight = ModuleStyle.baseHeight + (m.height() - ModuleStyle.baseHeight) * m.settingAnim.getValue();
            relY += animatedHeight;
        }



        // Draw panel and modules
        CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(20);
        Gui.drawRect(x, y + 24 - 5, x + width(), y + 24, new Color(12, 12, 11, 255).getRGB());
        RenderUtil.drawRoundedRect(x, y, width(), 24, 13f, new Color(12, 12, 11, 255).getRGB());
        font.drawCenteredString(this.category.name(), x + (width() / 2f), y + 12, -1);

        mouseDragged(mouseX, mouseY);
        hovering = RenderUtil.hover(x, y, mouseX, mouseY, width(), maxHeight());

        GL11.glPopMatrix();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.hover(x, y, mouseX, mouseY, width(), 24)) {
            if (mouseButton == 0) {
                dragging = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
            }else if (mouseButton == 1) {
                showModules = !showModules;
                moduleAnim.setTarget(showModules ? 1f : 0f);
            }
        }

//        Xynon.INSTANCE.debugLogger().sendLog(String.valueOf(scroll.getValue()));
//        Xynon.INSTANCE.debugLogger().sendLog("scrol: " + scroll.getValue());
        if(mouseY >= y + 24)
            moduleFrames.forEach(m ->
                    m.mouseClicked(mouseX, mouseY, mouseButton, scroll.getTarget()));

    }

    public void keyTyped(int keyCode) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        moduleFrames.forEach(m -> m.mouseReleased(mouseX, mouseY, state, scroll.getValue()));
    }

    public void mouseDragged(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    @Override
    public int width() {
        return 120;
    }

    @Override
    public int maxHeight() {
        return 340;
    }

    public float height() {
        float h = (float) moduleFrames.stream().mapToDouble(ModuleFrame::height).sum();
        if(h > maxHeight()) return maxHeight();
        return h;
    }

    public void handleMouseInput(int dwheel) {
        final int force = 12;
        final int dir = dwheel == 0 ? 0 : Math.abs(dwheel) / dwheel;
        if(!hovering) return;

        if(dir < 0) {
            // up
            scroll.setTarget(scroll.getTarget() - force);
        }else if(dir > 0) {
            // down
            scroll.setTarget(scroll.getTarget() + force);
        }

        // Clamp max
        int lastMax = 24;
        double max = (moduleFrames.stream().mapToDouble(ModuleFrame::height).sum());
        int min = 0;
        scroll.setTarget(
                MathHelper.clamp_float(scroll.getTarget(), (float) -max, min)
        );
    }
}
