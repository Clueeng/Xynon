package fr.flaily.xynon.notification.impl.list;

import fr.flaily.xynon.utils.MathHelper;
import fr.flaily.xynon.utils.render.shader.impl.GaussianBlur;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import fr.flaily.xynon.module.impl.render.Render;
import fr.flaily.xynon.notification.Notification;
import fr.flaily.xynon.notification.NotificationType;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.AnimFloat.Easing;

import java.awt.Color;
import fr.flaily.xynon.utils.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class AppleNotification extends Notification implements Render {

    private float x, y, width, height;
    private static final float PADDING = 10.0f;
    private static final float CORNER_RADIUS = 12.0f;
    private static final int STACK_SPACING = 8;

    private final ScaledResolution sr = new ScaledResolution(mc);
    private final AnimFloat alphaAnim;
    private final AnimFloat slideAnim;

    public AppleNotification(String title, String description, long delay) {
        super(title, description, delay, NotificationType.STACKABLE);

        this.width = Math.max(medium.getWidth(description), medium.getWidth(title)) + (PADDING * 2);
        this.height = medium.getHeight("A") * 2 + PADDING * 2; // Enough space for 2 lines + padding

        this.alphaAnim = new AnimFloat(0.0f, 0.08f, Easing.EASE_OUT_CUBIC);
        this.slideAnim = new AnimFloat(0.0f, 0.1f, Easing.EASE_OUT_CUBIC);
        
        this.alphaAnim.setTarget(1.0f);
        this.slideAnim.setTarget(1.0f);
        this.created = System.currentTimeMillis();

        this.y = sr.getScaledHeight();
    }

    @Override
    public void render(float partialTicks, int index) {
        alphaAnim.update(partialTicks);
        slideAnim.update(partialTicks);

        float animValue = slideAnim.getValue();
        float alpha = alphaAnim.getValue();

//        float targetY = 48 * index;
        float targetY = sr.getScaledHeight() - (10 + (height + STACK_SPACING) * index) - 48;
        float targetX = sr.getScaledWidth() - width - 10;

        this.x = targetX + (width * (1 - animValue));
        this.y = (float) MathHelper.lerp(0.03f, this.y, targetY);

        drawNotificationBox(alpha);
        drawText(alpha);

        if (alphaAnim.getTarget() >= 0.99f && hasElapsed()) {
            alphaAnim.setTarget(0.0f);
            slideAnim.setTarget(0.0f);
        }
    }

    private void drawNotificationBox(float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        int baseColor = new Color(240, 240, 255, 40).getRGB(); // dark gray with transparency
        int shadowColor = (int)(alpha * 80) << 24 | 0x000000; // soft shadow

        // Shadow
        GaussianBlur.renderBlur(13f, () -> {
            RenderUtil.drawRoundedRect3(x, y, width + 48, height, CORNER_RADIUS, -1);
        });
        RenderUtil.drawRoundedRect3(x, y, width + 48, height, CORNER_RADIUS, new Color(0, 0, 0, 30).getRGB());

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawText(float alpha) {
        int titleColor = ((int)(alpha * 255) << 24) | new Color(230, 230, 230).getRGB();
        int descColor = ((int)(alpha * 200) << 24) | new Color(171, 171, 171).getRGB();

        float textStartX = x + PADDING + 6; // a bit more left padding
        float textStartY = y + PADDING - 2;

        // Title (bold)
        tiktokBoldMedium.drawStringWithShadow(title, textStartX, textStartY, titleColor);

        // Description (lighter)
        tiktokMedium.drawStringWithShadow(description, textStartX, textStartY + tiktokMedium.getHeight("A") + 2, descColor);
    }


    @Override
    public boolean shouldDisappear() {
        return alphaAnim.getTarget() <= 0.01f && alphaAnim.getValue() <= 0.01f;
    }
}