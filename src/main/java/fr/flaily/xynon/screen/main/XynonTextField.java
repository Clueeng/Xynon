package fr.flaily.xynon.screen.main;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.render.RenderUtil;

public class XynonTextField {
    public String text;
    public int x, y, width, height;
    public boolean isFocused = false;

    public XynonTextField(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = "";
    }

    public void render() {
        // Render the text field background and text
        RenderUtil.drawRoundedRect(x, y, width, height, 12f, new java.awt.Color(23, 23, 23).getRGB());
        Xynon.INSTANCE.getFontManager().getFunnel().size(24).drawString(text + (isFocused ? "|" : ""), x + 8, y + (height - 24) / 2f + 5, -1);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            isFocused = true;
        } else {
            isFocused = false;
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (isFocused) {
            boolean validChar = (typedChar >= ' ' && typedChar <= '~');
            if (validChar) {
                addCharacter(typedChar);
            } else if (keyCode == 14) { // Backspace
                removeLastCharacter();
            }
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public void addCharacter(char c) {
        if (isFocused) {
            text += c;
        }
    }

    public void removeLastCharacter() {
        if (isFocused && text.length() > 0) {
            text = text.substring(0, text.length() - 1);
        }
    }


}
