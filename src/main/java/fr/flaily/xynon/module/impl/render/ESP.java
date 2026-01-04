package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.render.EventTeamColor;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.events.render.WorldRenderEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.WorldUtils;
import fr.flaily.xynon.utils.render.ColorUtils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.WorldToScreen;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.List;

import static fr.flaily.xynon.utils.render.WorldToScreen.worldToScreen;

@FeatureInfo(name = "ESP", category = Module.Category.Render)
public class ESP extends Module implements Render {

//    public ModeSetting espMode = mode("Mode", "2D", "2D", "Shader");
    public MultiSelectSetting espMode = multi("Mode", List.of("2D", "Shader", "3D"), List.of("Shader"), () -> true);
    public MultiSelectSetting targets = targetChoice(() -> true);
    public ColorSetting color = color("Color", 255, 0, 0, 255);
    public NumberSetting maxDistance = num("Max Distance", 16.0f, 512f, 256.0f, 16f, () -> espMode.isSelected("2D"));

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        WorldToScreen.updateMatrices();
        if(!espMode.isSelected("3D")) return;
        double x = mc.getRenderManager().viewerPosX;
        double y = mc.getRenderManager().viewerPosY;
        double z = mc.getRenderManager().viewerPosZ;

        for(Entity e : mc.theWorld.loadedEntityList) {
            if(!(e instanceof EntityLivingBase entity)) continue;
            if(!(WorldUtils.isEntityValid(entity, targets))) continue;
            if(entity == mc.thePlayer) continue;

            AxisAlignedBB bb = entity.getEntityBoundingBox();
            double ix = -(entity.posX - entity.lastTickPosX) * (1.0 - event.getPartialTicks());
            double iy = -(entity.posY - entity.lastTickPosY) * (1.0 - event.getPartialTicks());
            double iz = -(entity.posZ - entity.lastTickPosZ) * (1.0 - event.getPartialTicks());
            AxisAlignedBB renderBB = bb.offset(ix - x, iy - y, iz - z).expand(0.02, 0.02, 0.02);

            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            int a = color.getAlpha();


            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            RenderGlobal.drawFilledBoundingBox(renderBB, r, g, b, 100);

            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @EventHandler
    public void overrideEntityColor(EventTeamColor eventTeamColor) {
        if(espMode.isSelected("Shader")) {
            if(eventTeamColor.getEntityIn() instanceof EntityMob) {
                eventTeamColor.setoColor(new Color(0, 0, 255, 255));
                return;
            }
            if(eventTeamColor.getEntityIn() instanceof EntityAnimal) {
                eventTeamColor.setoColor(new Color(0, 255, 0, 255));
                return;
            }
            if(eventTeamColor.getEntityIn() instanceof EntityPlayer) {
                eventTeamColor.setoColor(new Color(255, 0, 0, 255));
                return;
            }
        }
    }

    @EventHandler
    public void onRender(ScreenEvent event) {
        float partialTicks = event.getPartialTicks();
        ScaledResolution sr = event.getSr();
        if(!espMode.isSelected("2D")) return;

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
            float dist = entity.getDistanceToEntity(mc.thePlayer);
            if(dist >= maxDistance.getValue()) continue;

            if (anyVisible) {
                String text = EnumChatFormatting.strip(entity.getName()) + "(" + (int)dist + "m)";
                float middle = minX + ((maxX - minX) / 2);
                float textPos = middle - (small.getWidth(text) / 2);
                float textLen = small.getWidth(text);
                float textY = minY - 14;
                float textH = small.getHeight(text);
                float healthX = minX - 4;
                float healthTop = minY;
                float healthBottom = maxY;
                float delta = Math.abs(healthTop - healthBottom);
                float healthRatio = Math.min(entity.getHealth() / entity.getMaxHealth(), 1.0f);
                float healthHeight = delta * healthRatio;

                // Nametags
                Gui.drawRect(textPos - 1, textY - 1, textPos + textLen + 1, textY + textH + 1, new Color(0, 0, 0, 150).getRGB());
                RenderUtil.drawUnfilledRectangle(textPos - 1, textY - 1, textPos + textLen + 1, textY + textH + 1, 0f, new Color(0, 0, 0, 255).getRGB());
                small.drawStringWithShadow(text, textPos, textY, -1);

                // Main ESP
                RenderUtil.drawUnfilledRectangle(minX - 0.5f, minY - 0.5f, maxX + 0.5f, maxY + 0.5f, 0f, new Color(0, 0, 0).getRGB(), 2f);
                RenderUtil.drawUnfilledRectangle(minX, minY, maxX, maxY, 0f, color.getColor().getRGB(), 1f);

                // Health bar
                RenderUtil.drawRect(healthX, healthBottom - healthHeight, healthX + 1, healthBottom, ColorUtils.getHealthColor(entity));
            }
        }
    }

}
