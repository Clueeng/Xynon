package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.events.render.WorldRenderEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.utils.WorldUtils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.WorldToScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static fr.flaily.xynon.utils.render.WorldToScreen.worldToScreen;
import static net.minecraft.client.renderer.GlStateManager.glBegin;

@FeatureInfo(name = "Tracers", category = Module.Category.Render)
public class Tracers extends Module implements Render {

    public MultiSelectSetting targets = targetChoice(() -> true);
    public ColorSetting color = color("Color", 255, 0, 0, 255);
    public ModeSetting where = mode("Mode", "Middle", "Middle", "Bottom");

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        WorldToScreen.updateMatrices();
    }

    @EventHandler
    public void onRender(ScreenEvent event) {
        float partialTicks = event.getPartialTicks();
        ScaledResolution sr = event.getSr();

        for(Entity e : mc.theWorld.loadedEntityList) {
            if(!(e instanceof EntityLivingBase entity)) continue;
            if(!(WorldUtils.isEntityValid(entity, targets))) continue;

            if(entity == mc.thePlayer) continue;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

            float width = entity.width / 2.0f;
            float height = entity.height;

            double[][] corners = {
                    {x - width, y, z - width}, {x - width, y, z + width},
                    {x + width, y, z - width}, {x + width, y, z + width},
                    {x - width, y + height, z - width}, {x - width, y + height, z + width},
                    {x + width, y + height, z - width}, {x + width, y + height, z + width}
            };

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            boolean anyVisible = false;

            for (double[] corner : corners) {
                float[] screenPos = worldToScreen(corner[0], corner[1], corner[2], sr);

                if (screenPos != null && screenPos[2] >= 0.0f && screenPos[2] < 1.0f) {
                    minX = Math.min(minX, screenPos[0]);
                    minY = Math.min(minY, screenPos[1]);
                    maxX = Math.max(maxX, screenPos[0]);
                    maxY = Math.max(maxY, screenPos[1]);
                    anyVisible = true;
                }
            }

            if (anyVisible) {
                float middle = sr.getScaledWidth() / 2f;
                float bottom = sr.getScaledHeight();
                float middleY = sr.getScaledHeight() / 2f;

                float chosen = 0.0f;
                if(this.where.is("Middle")) {
                    chosen = middleY;
                }else{
                    chosen = bottom;
                }

                float toX = minX + ((maxX - minX) / 2f);
                float toY = maxY;

                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glLineWidth(2.0f);

                GL11.glLineWidth(2f);
                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.5f);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2f(toX, toY);
                GL11.glVertex2f(middle, chosen);
                GL11.glEnd();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
//                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                GlStateManager.resetColor();
            }
        }
    }

}
