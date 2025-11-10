package fr.flaily.xynon.module.impl.render;

import java.util.Arrays;

import org.lwjgl.Sys;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.player.ItemHitEntityEvent;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.events.render.RenderedItemEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.AnimFloat;
import fr.flaily.xynon.utils.AnimFloat.Easing;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

@FeatureInfo(name = "Animations", category = fr.flaily.xynon.module.Module.Category.Render)
public class Animations extends Module {
    public float counter = 0.0f;
    public AnimFloat slidingAnim = new AnimFloat(0.0F, 0.05f, Easing.LERP);

    public MultiSelectSetting features = multi("Features", Arrays.asList("Block Animation", "Item Size", "Slow Swing", "Enchant Glint")
    , Arrays.asList("Block Animation", "Item Size", "Enchant Glint"), () -> true);

    public ModeSetting animation = mode("Block Animation", "Legacy", () -> features.isSelected("Block Animation"), "Legacy", "Chop", "Luna", "Hpop", "Exhibition");
    public BooleanSetting animationFix = bool("Swing Fix", true, () -> features.isSelected("Block Animation"));
    public NumberSetting itemSize = num("Item Size", 0.0f, 2.0f, 1.0f, 0.05f, () -> features.isSelected("Item Size"));
    public NumberSetting itemSpeed = num("Swing Speed", 6.0f, 24.0f, 6.0f, 1f, () -> features.isSelected("Slow Swing"));
    public BooleanSetting glintAll = bool("All Enchanted", true, () -> features.isSelected("Enchant Glint"));
    public ColorSetting glintColor = color("Glint Color", 0.0F, 0.0f, 0.0F, 255);
    

    @EventHandler
    public void swingAnimation(RenderedItemEvent event) {
        if(slidingAnim.getValue() <= 0.01f) {
            slidingAnim.setTarget(1.0f);
            slidingAnim.setSpeed(0.12f);
        }
        if(slidingAnim.getValue() >= 0.99f) {
            slidingAnim.setTarget(0.0f);
            slidingAnim.setSpeed(0.07f);
        }
        slidingAnim.update(event.partialTicks);


        float smoothSwing = (float) Math.sin(event.swingProgress * Math.PI); 
        float smoothSin = (float) ((float) Math.sin(
            (counter++) * Math.PI
        )) * 10e10f;
        float smoothVal = (slidingAnim.getValue()) * (event.swingProgress > 0 ? 1.0f : 0.0f);

        switch (animation.getCurrent()) {
            case "Legacy":
                event.transformFirstPersonItem(event.equipProgress, event.swingProgress);
                event.doBlockTransformations();
                break;
            case "Chop":
                GlStateManager.rotate(-event.swingProgress * 36f, 0.0f, 0.0f, 0.1f);
                GlStateManager.translate(0.0f, smoothSwing * 0.5f, 0.0f);
                event.transformFirstPersonItem(event.equipProgress, event.swingProgress);
                event.doBlockTransformations();
                break;
            case "Luna": {
                GlStateManager.translate(0F, 0.11F, 0F);
                GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
                GlStateManager.translate(0.0F, event.equipProgress * -0.6F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(event.equipProgress * -20.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(smoothVal * -20.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(smoothVal * -80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-smoothVal * -40.0F, 1.0F, -1.0F, 1.0F);
                GlStateManager.scale(0.4F, 0.4F, 0.4F);
                event.doBlockTransformations();
                break;
            }
            case "Hpop": {
                GlStateManager.translate(.5f, 0.0f, 0.0f);
                GlStateManager.translate(-smoothVal * 0.5f, 0.0f, 0.0);
                GlStateManager.rotate(smoothVal * 45.0f, 0.0F, 1.0F, 1.0F);
                event.transformFirstPersonItem(event.equipProgress, 1.0F);
                event.doBlockTransformations();
                break;
            }
            case "Exhibition": {
                GlStateManager.translate(0.1F, 0.25F, 0F);
                // GlStateManager.rotate(-event.swingProgress * 0.6f, 1.0f, 0.0f, 0.1f);
                // GlStateManager.translate(0.0f, smoothSwing * 0.5f, 0.0f);
                // event.transformFirstPersonItem(event.equipProgress, event.swingProgress);

                GlStateManager.translate(0.56F, -0.52F, -0.72F);
                GlStateManager.translate(0.0F, event.equipProgress * -0.6F, 0.0F);
                GlStateManager.rotate(45.0F, 0.0F, 135.0F, 0.0F);
                float f = MathHelper.sin(event.swingProgress * event.swingProgress * (float)Math.PI);
                float f1 = MathHelper.sin(MathHelper.sqrt_float(event.swingProgress) * (float)Math.PI);

                GlStateManager.rotate(-smoothVal * 30.0f, 1.0F, 0.0F, 1.0f);
                GlStateManager.rotate(-smoothVal * 10.0f, 0.0F, 1.0F, 0.0f);
                GlStateManager.rotate(-smoothVal * 30.0f, 1.0F, 0.0F, 0.0f);

                GlStateManager.scale(0.4F, 0.4F, 0.4F);
                event.doBlockTransformations();
                break;
            }
        
            default:
                event.transformFirstPersonItem(event.equipProgress, 1.0F);
                event.doBlockTransformations();
                break;
        }
    }

    @EventHandler
    public void debug(UpdateEvent event) {
    }

    @EventHandler
    public void swingFix(ItemHitEntityEvent event) {
        if(this.animationFix.isToggled()) {
            event.setCancelled(true);
        }
    }

}
