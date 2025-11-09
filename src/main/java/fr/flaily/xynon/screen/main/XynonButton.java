package fr.flaily.xynon.screen.main;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import net.minecraft.client.gui.Gui;

public class XynonButton {
    CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(24);

    public int x, y, width, height, id;
    public String text;
    public AnimFloat hoverAnim = new AnimFloat(0, 0.035f, AnimFloat.Easing.EASE_IN_OUT);

    public XynonButton(int x, int y, int width, int height, String text, int id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.id = id;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        hoverAnim.update(partialTicks);
        if (isHovered(mouseX, mouseY)) {
            hoverAnim.setTarget(1f);
        } else {
            hoverAnim.setTarget(0f);
        }
        // Interpolate the background alpha/color
        int startOpacity = 110;
        int backgroundAlpha = (int) (startOpacity + hoverAnim.getValue() * (255 - startOpacity)); // Goes from 200 to 255
        int backgroundColor = new Color(12, 12, 12, backgroundAlpha).getRGB();

        GL11.glPushMatrix();
        GL11.glTranslatef(0, -8 * hoverAnim.getValue(), 0);
        float scaleFactor = 1f + 0.02f * hoverAnim.getValue();

        GL11.glTranslatef(x + width / 2f, y + height / 3f, 0);
        GL11.glScalef(scaleFactor, scaleFactor, 1);
        GL11.glTranslatef(-(x + width / 2f), -(y + height / 3f), 0);

        Gui.drawRect(x, y, x + width, y + height, backgroundColor);
        font.drawCenteredString(text, x + width / 2f, 5 +y + (height - font.getHeight("A")) / 2f, -1);

        GL11.glPopMatrix();
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
