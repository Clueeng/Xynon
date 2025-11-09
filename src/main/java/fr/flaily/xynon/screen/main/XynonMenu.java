package fr.flaily.xynon.screen.main;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.render.shader.impl.MenuShader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;

public class XynonMenu extends GuiScreen {

    public ArrayList<XynonButton> buttons = new ArrayList<>();

    public XynonMenu() {
        int startY = height / 2 - 28;
        String[] buttonLabels = {"Singleplayer", "Multiplayer", "Settings", "Alt Manager", "Quit"};
        
        for (int i = 0; i < buttonLabels.length; i++) {
            buttons.add(new XynonButton(width / 2 - 100, startY + (i * 24), 200, 24, buttonLabels[i], i + 1));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        MenuShader.render(1.0f);
        Gui.drawRect(0, 0, width, height, new Color(0, 0, 0, 110).getRGB());
        Xynon.INSTANCE.getFontManager().getFunnel().size(96).drawCenteredString("Xynon",
         width / 2f, 100f, -1);

        // buttons background
        int buttonHeight = buttons.size() * 32;
        Gui.drawRect(width / 2 - 120, height / 2 - 60, width / 2 + 120, height / 2 + buttonHeight - 50, new Color(0, 0, 0, 50).getRGB());

        for (XynonButton button : buttons) {
            button.x = width / 2 - 100;
            button.y = height / 2 - 50 + buttons.indexOf(button) * 32;

            button.render(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // TODO Auto-generated method stub
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // TODO Auto-generated method stub
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (XynonButton button : buttons) {
            if (button.isHovered(mouseX, mouseY)) {
                switch (button.id) {
                    case 1:
                        // Handle Singleplayer button click
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case 2:
                        // Handle Multiplayer button click
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case 3:
                        // Handle Settings button click
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case 4:
                        // Handle Alt Manager button click
                        mc.displayGuiScreen(new fr.flaily.xynon.screen.alts.GuiAltManager());
                        break;
                    case 5:
                        // Handle Quit button click
                        mc.shutdown();
                        break;
                    default:
                        break;
                }
            }
        }

    }

}
