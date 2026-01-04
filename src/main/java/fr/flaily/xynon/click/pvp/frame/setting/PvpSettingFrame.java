package fr.flaily.xynon.click.pvp.frame.setting;

import fr.flaily.xynon.Xynon;
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

public class PvpSettingFrame extends GuiScreen {

    public static final float BOTTOM_BAR_HEIGHT = 24;
    public static float TITLE_BAR_HEIGHT = 50;
    public float x, y, guiWidth, guiHeight;
    public ModulePvP parent;
    public CustomFontRenderer titleFont, catFont;

    public PvpSettingFrame(ModulePvP parent) {
        this.parent = parent;
        mc = Minecraft.getMinecraft();

        titleFont = Xynon.INSTANCE.getFontManager().getInter().size(convertToScreen(48));
        catFont = Xynon.INSTANCE.getFontManager().getInter().size(convertToScreen(32));
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

        titleFont.drawStringWithShadow(this.parent.getName(), x + convertToScreen(12), y + convertToScreen(12), -1);


        // BELOW FRAME
    }

    private int convertToScreen(double scaled) {
        return (int) (scaled / mc.gameSettings.guiScale);
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
}
