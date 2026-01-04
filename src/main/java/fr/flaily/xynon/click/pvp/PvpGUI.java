package fr.flaily.xynon.click.pvp;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.click.pvp.frame.CategoryFrame;
import fr.flaily.xynon.module.impl.pvp.ModulePvP;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.impl.Bloom;
import fr.flaily.xynon.utils.render.shader.impl.GaussianBlur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;

public class PvpGUI extends GuiScreen {
    public static final float BOTTOM_BAR_HEIGHT = 24;
    public static float TITLE_BAR_HEIGHT = 50;
    public float x, y, guiWidth, guiHeight;
    // box gui
    public ModulePvP.Category selectedCategory = ModulePvP.Category.Visuals;
    public HashMap<ModulePvP.Category, CategoryFrame> availableFrames = new HashMap<>();
    public CustomFontRenderer titleFont, catFont;
    public Minecraft mc;

    public PvpGUI() {
        mc = Minecraft.getMinecraft();
        titleFont = Xynon.INSTANCE.getFontManager().getInter().size(convertToScreen(48));
        catFont = Xynon.INSTANCE.getFontManager().getInter().size(convertToScreen(32));
        for(ModulePvP.Category category : ModulePvP.Category.values()) {
            this.availableFrames.put(category, new CategoryFrame(this, category));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        // BACKGROUND
        Runnable guiShape = () -> {
            RenderUtil.drawRoundedRect3(x, y, guiWidth, guiHeight, 12f, -1);
        };
        Bloom.renderBloom(4f, new Color(0, 0, 0, 90).getRGB(), guiShape);
        GaussianBlur.renderBlur(32f, guiShape);
        RenderUtil.drawRoundedRect3(x, y, guiWidth, guiHeight, 12f, new  Color(25, 25, 25, 90).getRGB());

        // TITLE BAR
        TITLE_BAR_HEIGHT = convertToScreen(50);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.prepareScissorBox(x - 1, y - 1, x + guiWidth + 1, y + TITLE_BAR_HEIGHT);
        RenderUtil.drawRoundedRect3(x, y, guiWidth, TITLE_BAR_HEIGHT + 20, 12f, new  Color(0, 0, 0, 50).getRGB());
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        titleFont.drawStringWithShadow("o  Xynon", x + convertToScreen(12), y + convertToScreen(12), -1);


        // BELOW FRAME
        availableFrames.get(selectedCategory).drawScreen(x, y + TITLE_BAR_HEIGHT + 4, mouseX, mouseY, partialTicks);

        // Bottom bar
        int bottomY = (int) (y + (guiHeight - BOTTOM_BAR_HEIGHT));
        RenderUtil.drawRoundedRect3(x, bottomY, guiWidth, BOTTOM_BAR_HEIGHT, 12f, new  Color(25, 25, 25, 90).getRGB());
        int padd = 4;
        float catX = x + padd;
        float catWidth = 120;
        float catSpacing = 4;
        int catHeight = 20;
        for(ModulePvP.Category category : ModulePvP.Category.values()) {
            RenderUtil.drawRoundedRect3(catX, bottomY + (padd / 2f), catWidth, catHeight, 13f, new  Color(0, 0, 0, 50).getRGB());

            float textLength = catFont.getWidth(category.name());
            float textX = catX + (catWidth / 2f) - (textLength / 2f);
            catFont.drawStringWithShadow(category.name(), textX, bottomY + (padd / 2f) + 6, -1);
            catX += catWidth + catSpacing;
        }
    }

    private int convertToScreen(double scaled) {
        return (int) (scaled / mc.gameSettings.guiScale);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        availableFrames
                .get(selectedCategory)
                .mouseClicked(mouseX, mouseY, mouseButton);

        // Bottom bar
        int bottomY = (int) (y + (guiHeight - BOTTOM_BAR_HEIGHT));
        int padd = 4;
        float catX = x + padd;
        float catWidth = 120;
        float catSpacing = 4;
        int catHeight = 20;
        float catY = bottomY + (padd / 2f);

        for(ModulePvP.Category category : ModulePvP.Category.values()) {
            boolean hovered =
                    mouseX >= catX &&
                            mouseX <= catX + catWidth &&
                            mouseY >= catY &&
                            mouseY <= catY + catHeight;

            if (hovered) {
                selectedCategory = category;
                break;
            }

            catX += catWidth + catSpacing;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        ScaledResolution sr = new ScaledResolution(mc);

        this.guiWidth = sr.getScaledWidth() / 1.5f;
        this.guiHeight = sr.getScaledHeight() / 1.5f;

        this.x = (sr.getScaledWidth() / 2f) - (guiWidth / 2f);
        this.y = (sr.getScaledHeight() - guiHeight) / 2f;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }
}
