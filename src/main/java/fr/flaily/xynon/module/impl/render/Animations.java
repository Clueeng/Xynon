package fr.flaily.xynon.module.impl.render;

import java.util.Arrays;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.player.ItemHitEntityEvent;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.events.render.RenderedItemEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import net.minecraft.client.renderer.GlStateManager;

@FeatureInfo(name = "Animations", category = fr.flaily.xynon.module.Module.Category.Render)
public class Animations extends Module {

    public MultiSelectSetting features = multi("Features", Arrays.asList("Block Animation")
    , Arrays.asList("Block Animation", "Item Size", "Enchant Glint"), () -> true);

    public ModeSetting animation = mode("Block Animation", "Legacy", () -> features.isSelected("Block Animation"), "Legacy", "Chop", "Luna", "Hpop", "Exhibition");
    public BooleanSetting animationFix = bool("Swing Fix", true, () -> features.isSelected("Block Animation"));

    @EventHandler
    public void swingAnimation(RenderedItemEvent event) {
        float smoothSwing = (float) Math.sin(event.swingProgress * Math.PI); 

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
                GlStateManager.rotate(smoothSwing * -20.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(smoothSwing * -80.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(-smoothSwing * -40.0F, 1.0F, -1.0F, 1.0F);
                GlStateManager.scale(0.4F, 0.4F, 0.4F);
                event.doBlockTransformations();
                break;
            }
            case "Hpop": {
                GlStateManager.translate(-smoothSwing * 0.5f, 0.0f, 0.0);
                event.transformFirstPersonItem(event.equipProgress, 1.0F);
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
        if(mc.thePlayer.isSneaking()) {
            int ticks = 1;
            if(mc.thePlayer.ticksExisted % ticks == 0) {
                mc.thePlayer.swingItem();
            }
        }
    }

    @EventHandler
    public void swingFix(ItemHitEntityEvent event) {
        if(this.animationFix.isToggled()) {
            event.setCancelled(true);
        }
    }

}
