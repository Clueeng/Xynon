package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.network.PacketEvent;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.impl.combat.Killaura;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.WorldUtils;
import fr.flaily.xynon.utils.render.ColorUtils;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.shader.impl.RoundedShader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@FeatureInfo(name = "Target HUD", category = Module.Category.Render)
public class TargetHUD extends Module implements Render {
    private final AnimFloat healthAnim = new AnimFloat(0.0f, 0.05f, AnimFloat.Easing.EASE_OUT_BACK);

    public ModeSetting design = mode("Mode", "Simple", () -> true, "Simple");
    private Entity focusedEntity;

    @EventHandler
    public void onRender(ScreenEvent event) {
        healthAnim.update(event.getPartialTicks());

        Killaura killaura = Xynon.INSTANCE.getModuleManager().getModule(Killaura.class);
        if(killaura.getTarget() != null) {
            focusedEntity = killaura.getTarget();
        }

        boolean shouldHide = focusedEntity == null ? true : mc.thePlayer.getDistanceToEntity(focusedEntity) > 8.0;
        if(shouldHide) {
            healthAnim.setSpeed(0.015f);
            healthAnim.setTarget(1.0f);
        }else{
            healthAnim.setTarget(0.0f);
            healthAnim.setSpeed(0.05f);
        }

        float middleX = event.getSr().getScaledWidth() / 2f;
        float middleY = event.getSr().getScaledHeight() / 2f;

        float targetX = middleX + 40;

        float rest = event.getSr().getScaledWidth() - targetX;

        if(focusedEntity != null) {
            switch (design.getValue()) {
                case "Simple": {
                    simpleHud((EntityLivingBase) focusedEntity, targetX + (rest * healthAnim.getValue()), middleY + 40);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if(event.getPacket() instanceof C02PacketUseEntity use) {
            Xynon.INSTANCE.gameLogger().sendLog("test");
            if(use.getAction() == C02PacketUseEntity.Action.ATTACK) {
                focusedEntity = use.getEntityFromWorld(mc.theWorld);
            }
        }
    }

    private void simpleHud(EntityLivingBase target, float x, float y) {
        float hurtPercent = (float) target.hurtTime / (float) target.maxHurtTime;
        float width = 180;
        float height = 48;

        float ratio = Math.min(target.getHealth() / target.getMaxHealth(), 1.0f);

        if(hurtPercent >= 0.0f) {
            GlStateManager.pushMatrix();
            // Scale it down a bit as an animation on hit
            float centerX = x + width / 2;
            float centerY = y + height / 2;

            // Scale factor: 1.0 when not hit, 0.95 when just hit (tweak 0.05 for more/less intensity)
            float scale = 1.0f - (hurtPercent * 0.05f);

            GlStateManager.translate(centerX, centerY, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-centerX, -centerY, 0);
        }

        RenderUtil.drawRoundedRect3(x, y, width, height, 11f, new Color(0, 0, 0, 90).getRGB());

        int healthBarHeight = 4;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.prepareScissorBox(x, y + height - healthBarHeight, x + width, y + height + 100);
        RenderUtil.drawRoundedRect3(x, y + healthBarHeight, width, height - healthBarHeight, 11f, new Color(0, 0, 0, 150).getRGB());
        RenderUtil.drawRoundedRect3(x, y + healthBarHeight, width * ratio, height - healthBarHeight, 11f, ColorUtils.getHealthColor(target));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

//        RenderUtil.renderEntityHead(target, (int) x + 4, (int) y + 4, 32);
        float size = 32;
        float radius = 5;
        float headX = x + 4;
        float headY = y + 4;
        RoundedShader.drawRounded(headX, headY, size, size, radius, () -> {
            RenderUtil.renderEntityHead(target, (int)headX, (int)headY, (int)size);
        });
        if (target.hurtTime > 0) {
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();

            RenderUtil.drawRoundedRect3(headX - 1f, headY - .5f, size, size, radius, RenderUtil.withAlpha(Color.red.getRGB(), hurtPercent));
        }

        big.drawStringWithShadow(target.getName(), x + 6 + size, y + 6, -1);
        small.drawStringWithShadow(String.format("%.2f", target.getHealth()) + " / " + target.getMaxHealth(), x + 6 + size, (y + 12) + big.getHeight("A") + 2, -1);


        if(hurtPercent >= 0.0f) {
            // Scale it down a bit as an animation on hit
            GlStateManager.popMatrix();
        }
    }

}
