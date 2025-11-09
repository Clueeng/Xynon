package fr.flaily.xynon.screen.alts;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.screen.main.XynonTextField;
import fr.flaily.xynon.utils.alts.Alt;
import fr.flaily.xynon.utils.alts.impl.ComboAlt;
import fr.flaily.xynon.utils.alts.impl.CookieAlt;
import fr.flaily.xynon.utils.alts.impl.SessionAlt;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.impl.AltShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiAltManager extends GuiScreen {

    
    // Custom Button class
    class AltButton {
        private String label;
        private int x, y, width, height;
        public int id;

        public AltButton(int x, int y, int width, int height, String label, int id) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.label = label;
            this.id = id;
        }

        public void render(int mouseX, int mouseY, float partialTicks) {
            // Render button background
            // Render button label
            RenderUtil.drawRoundedRect(x, y, width, height, 24f, new java.awt.Color(23, 23, 23).getRGB());
            Xynon.INSTANCE.getFontManager().getFunnel().size(24).drawCenteredString(label, x + width / 2f, y + height / 2f, -1);

        }

        public boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    class RenderedAlt {
        // Each alt in the list
        private Alt alt;
        private int x, y, width, height;

        public RenderedAlt(Alt alt, int x, int y, int width, int height) {
            this.alt = alt;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void render(int mouseX, int mouseY, float partialTicks) {
            // Render alt background
            // Render alt label`
            RenderUtil.drawRoundedRect(x,  y, width, height, 12f, new java.awt.Color(23, 23, 23).getRGB());
            CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(24);
            String displayName = alt.getUsername();
            if(alt instanceof CookieAlt) {
                CookieAlt cookieAlt = (CookieAlt) alt;
                if(!cookieAlt.getUsername().equals("Unknown")) {
                    displayName = cookieAlt.getUsername();
                } else {
                    displayName = "Cookie Alt";
                }
            }
            if(alt instanceof ComboAlt) {
                ComboAlt comboAlt = (ComboAlt) alt;
                if(comboAlt.isCracked()) {
                    displayName = comboAlt.getEmail() + " (Cracked)";
                } else {
                    displayName = comboAlt.getEmail();
                }
            }
            if(alt instanceof SessionAlt sessionAlt) {
                displayName = sessionAlt.getUsername() + " (Session)";
                long lastLogin = sessionAlt.lastLogin;
                long timeSince = System.currentTimeMillis() - lastLogin;
                // Valid for 24h, calculate how much time left in hh:mm:ss
                long timeLeft = (24 * 60 * 60 * 1000) - timeSince;
                if(timeLeft > 0) {
                    long hours = timeLeft / (1000 * 60 * 60);
                    long minutes = (timeLeft / (1000 * 60)) % 60;
                    long seconds = (timeLeft / 1000) % 60;
                    String timeLeftStr = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    Xynon.INSTANCE.getFontManager().getFunnel().size(16).drawCenteredString("Session valid for: " + timeLeftStr,
                     x + (width / 2f), (y + height / 2f) + 9, -1);

                }
            }
            font.drawCenteredString(displayName, x + width / 2f, y + height / 2f - 3, -1);
        }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if(isHovered(mouseX, mouseY) && mouseButton == 0) {
                // Select this alt
                System.out.println("Selected alt: " + alt);
                alt.login();
            }
        }

        public boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }




    /*
     * Alt manager =
     * List of alts
     * Add alt button (for all types of alts)
     * Remove alt button
     * Login button
     * Reload alts button
     * Status (logged in as xxx / not logged in)
     * Status of currently selected alt (working / not working, ban list, etc)
     * Bonus for later: import cookie alts by dragging a file into the window
    */

    public ArrayList<Alt> alts = new ArrayList<>();
    public ArrayList<RenderedAlt> renderedAlts = new ArrayList<>();
    public ArrayList<AltButton> buttons = new ArrayList<>();
    public Minecraft mc = Minecraft.getMinecraft();
    public XynonTextField usernameField;

    private void reloadAlts() {
        ScaledResolution sr = new ScaledResolution(mc);
        // System.out.println("reloadAlts called");

        Xynon.INSTANCE.getAltManager().loadAltsFromFile();

        this.alts.clear();
        this.alts = new ArrayList<>(Xynon.INSTANCE.getAltManager().getAlts());

        this.renderedAlts.clear();

        for(Alt alt : this.alts) {
            this.renderedAlts.add(new RenderedAlt(alt, 
            (int) ((sr.getScaledWidth() / 2f) - 150), 100 + this.renderedAlts.size() * 60, 300, 50));
        }
    }

    private String getAltStatus(Alt alt) {
        switch (alt.status) {
            case WAITING:
                return "Waiting for authentication...";
            case LOGGING_IN:
                return "Logging in...";
            case SUCCESS:
                return "Login successful!";
            case FAILED:
                return "Login failed.";
            default:
                return "Idle";
        }
    }

    private int getAltStatusColor(Alt alt) {
        switch (alt.status) {
            case WAITING:
                return new java.awt.Color(255, 255, 0).getRGB(); // Yellow
            case LOGGING_IN:
                return new Color(0, 0, 255).getRGB(); // Blue
            case SUCCESS:
                return new Color(0, 255, 0).getRGB(); // Green
            case FAILED:
                return new Color(255, 0, 0).getRGB(); // Red
            default:
                return new Color(255, 255, 255).getRGB(); // White
        }
    }
    private RenderedAlt selectedAlt = null;

    public GuiAltManager() {
        // Load alts from file or other source
        reloadAlts();
        ScaledResolution sr = new ScaledResolution(mc);


        int posX = 94;
        int posY = sr.getScaledHeight() / 2 + 32;
        System.out.println(posY);
        int buttonWidth = 72;
        this.usernameField = new XynonTextField(posX, posY - 105, buttonWidth * 2 + 10, 24);
        String[] buttons = {"MSA", "Cookie", "Cracked"};
        int id = 0;
        for (String buttonLabel : buttons) {
            if(id == 2) {
                posX = 94;
                posY += 34;
                buttonWidth *= 2;
                buttonWidth += 10;
            }
            this.buttons.add(new AltButton(posX, posY, buttonWidth, 24, buttonLabel, id));
            posX += buttonWidth + 10;
            id++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CustomFontRenderer big = Xynon.INSTANCE.getFontManager().getFunnel().size(48);
        CustomFontRenderer medium = Xynon.INSTANCE.getFontManager().getFunnel().size(24);
        CustomFontRenderer small = Xynon.INSTANCE.getFontManager().getFunnel().size(18);
        
        ScaledResolution sr = new ScaledResolution(mc);
        super.drawScreen(mouseX, mouseY, partialTicks);
        // Shader Background
        AltShader.render(1.0f);

        // Title
        big.drawCenteredString("Alt Manager", sr.getScaledWidth() / 4f, 40, -1);

        // Left side panel (Buttons)
        int posX = 94;
        int posY = sr.getScaledHeight() / 2 - 100;
        RenderUtil.drawRoundedRect(posX - 8, posY - 24, (72 * 2) + 14 + 12, 26, 9f, new java.awt.Color(23, 23, 23).getRGB());
        RenderUtil.drawRoundedRect(posX - 8, posY - 3, (72 * 2) + 14 + 12, 200, 9f, new java.awt.Color(42, 42, 42).getRGB());
        for (AltButton button : buttons) {
            button.render(mouseX, mouseY, partialTicks);
        }
        medium.drawCenteredString("Add Alt", posX + 78, posY - 14, -1);
        small.drawStringWithShadow("Crack Name", posX + 4, posY + 14, -1);
        usernameField.render();


        // Middle panel (Alt list)
        RenderUtil.drawRoundedRect((sr.getScaledWidth_double() / 2f) - 150, 76, 300, 26, 9f, new java.awt.Color(42, 42, 42).getRGB());
        for (RenderedAlt renderedAlt : renderedAlts) {
            renderedAlt.render(mouseX, mouseY, partialTicks);
        }
        if(alts.isEmpty()) {
            medium.drawCenteredString("No alts", (sr.getScaledWidth() / 2f), 90, -1);
        }else{
            medium.drawCenteredString("Alts (" + this.alts.size() + ")", (sr.getScaledWidth() / 2f), 90, -1);
        }

        // Status bar
        if(this.selectedAlt != null) {
            // System.out.println(selectedAlt.alt.username + " : " + selectedAlt.alt.status);
            Alt alt = this.selectedAlt.alt;
            String status = getAltStatus(alt);
            int statusColor = getAltStatusColor(alt);
            small.drawCenteredString(alt.getUsername() + " - " + status,
             sr.getScaledWidth() / 2f, sr.getScaledHeight() - 30, statusColor);
        }

        // Right side panel (Current alt info, later)
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // TODO Auto-generated method stub
        super.keyTyped(typedChar, keyCode);
        usernameField.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // TODO Auto-generated method stub
        super.mouseClicked(mouseX, mouseY, mouseButton);
        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        for (AltButton button : buttons) {
            if (button.isHovered(mouseX, mouseY)) {
                String username = usernameField.text;
                switch (button.id) {
                    case 0:
                        // MSA Alt
                        SessionAlt msaAlt = new SessionAlt();
                        msaAlt.login();
                        // Xynon.INSTANCE.getAltManager().addAlt(msaAlt);
                        reloadAlts();
                        break;
                    case 1:
                        // Cookie Alt
                        CookieAlt cookieAlt = new CookieAlt();
                        cookieAlt.login();
                        // Xynon.INSTANCE.getAltManager().addAlt(cookieAlt);
                        reloadAlts();
                        break;
                    case 2:
                        // Cracked Alt
                        ComboAlt crackedAlt = new ComboAlt(username, "");
                        crackedAlt.login();
                        // Xynon.INSTANCE.getAltManager().addAlt(crackedAlt);
                        reloadAlts();
                        break;
                }
            }
        }
        for(RenderedAlt renderedAlt : renderedAlts) {
            if(renderedAlt.isHovered(mouseX, mouseY)) {
                selectedAlt = renderedAlt;
            }
            renderedAlt.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

}
