package fr.flaily.xynon.module.impl.pvp.impl;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.module.impl.pvp.LegitFeatureInfo;
import fr.flaily.xynon.module.impl.pvp.ModulePvP;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

@LegitFeatureInfo(name = "FPS", category = ModulePvP.Category.Visuals, key = Keyboard.KEY_F)
public class ModuleFPS extends ModulePvP {
    public ModuleFPS() {
        super(true);
    }

    @EventHandler
    public void onRender(ScreenEvent event) {
        big.drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), getScreenData().getScreenPos().x, getScreenData().getScreenSize().y, -1);
    }
}
