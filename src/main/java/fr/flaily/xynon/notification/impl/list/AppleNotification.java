package fr.flaily.xynon.notification.impl.list;

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

// Assuming RenderUtil.drawRoundedRect3 draws a filled rounded rectangle
// and 'medium' is a FontRenderer instance.

public class AppleNotification extends Notification implements Render {

    private float x, y, width, height;
    private static final float PADDING = 10.0f; // Padding inside the box
    private static final float CORNER_RADIUS = 12.0f; // High radius for modern look
    private static final int STACK_SPACING = 8; // Spacing between stacked notifications

    private final ScaledResolution sr = new ScaledResolution(mc);
    private final AnimFloat alphaAnim;  // Alpha for fade in/out
    private final AnimFloat slideAnim;  // Position for slide in/out

    public AppleNotification(String title, String description, long delay) {
        super(title, description, delay, NotificationType.STACKABLE);

        // Calculate dynamic width based on text
        this.width = Math.max(medium.getWidth(description), medium.getWidth(title)) + (PADDING * 2);
        this.height = medium.getHeight("A") * 2 + PADDING * 2; // Enough space for 2 lines + padding

        // Animation setup: use EASE_OUT_CUBIC for a slick, decelerating entry
        this.alphaAnim = new AnimFloat(0.0f, 0.08f, Easing.EASE_OUT_CUBIC);
        this.slideAnim = new AnimFloat(0.0f, 0.1f, Easing.EASE_OUT_CUBIC);
        
        // Start animation (Slide in and fade in simultaneously)
        this.alphaAnim.setTarget(1.0f);
        this.slideAnim.setTarget(1.0f);
        this.created = System.currentTimeMillis(); // Start timer right away
    }

    @Override
    public void render(float partialTicks, int index) {
        alphaAnim.update(partialTicks);
        slideAnim.update(partialTicks);

        float animValue = slideAnim.getValue();
        float alpha = alphaAnim.getValue();

        // 1. Calculate Target Position
        float targetY = 10 + (height + STACK_SPACING) * index;
        float targetX = sr.getScaledWidth() - width - 10;

        // 2. Apply Slide Animation (Slide in from the right, starting at +width)
        // The notification slides in 100% (animValue=1) at targetX.
        // It starts at targetX + width (animValue=0).
        this.x = targetX + (width * (1 - animValue));
        this.y = targetY;

        drawNotificationBox(alpha);
        drawText(alpha);

        // 3. Disappearance Trigger
        if (alphaAnim.getTarget() >= 0.99f && hasElapsed()) {
            // Start the exit animation: slide out and fade out simultaneously
            alphaAnim.setTarget(0.0f);
            slideAnim.setTarget(0.0f);
        }
    }

    private void drawNotificationBox(float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        // Colors similar to macOS notification (light translucent gray)
        int baseColor = (int)(alpha * 120) << 24 | new Color(240, 240, 255).getRGB(); // dark gray with transparency
        int shadowColor = (int)(alpha * 80) << 24 | 0x000000; // soft shadow

        // Shadow
        RenderUtil.drawRoundedRect3(x + 2, y + 2, x + width + 2, y + height + 2, CORNER_RADIUS, shadowColor);

        // Background
        RenderUtil.drawRoundedRect3(x, y, x + width, y + height, CORNER_RADIUS, baseColor);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawText(float alpha) {
        int titleColor = ((int)(alpha * 255) << 24) | new Color(24, 24, 25).getRGB();
        int descColor = ((int)(alpha * 200) << 24) | new Color(53, 53, 53).getRGB();

        float textStartX = x + PADDING + 6; // a bit more left padding
        float textStartY = y + PADDING + 2;

        // Title (bold)
        tiktokBoldMedium.drawString(title, textStartX, textStartY, titleColor);

        // Description (lighter)
        tiktokMedium.drawString(description, textStartX, textStartY + tiktokMedium.getHeight("A") + 2, descColor);
    }


    @Override
    public boolean shouldDisappear() {
        // Disappear when the animation is complete and the target is 0.0f (faded out)
        return alphaAnim.getTarget() <= 0.01f && alphaAnim.getValue() <= 0.01f;
    }
}