package fr.flaily.xynon.module.impl;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.ScreenEvent;
import fr.flaily.xynon.events.player.MotionEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

@FeatureInfo(name = "Test", category = Module.Category.Combat, key = Keyboard.KEY_G)
public class TestModule extends Module {
    public ModeSetting testmode3 = mode("TestMode", "Value1", "Value1", "Value2", "Value3", "Value4", "Value5");
    //    public ModeSetting testmode = mode("TestMode", "Value1", "Value1", "Value2", "Value3");
    public BooleanSetting test = bool("Test", true, () -> true);
    public BooleanSetting testTwo = bool("Other Test", true, () -> true);
//    public ModeSetting testmode2 = mode("TestMode", "Value1", "Value1", "Value2", "Value3");
    public ColorSetting color = color("Test", 1f, 1f, 1f, 255);
    public NumberSetting numberSetting = num("NumTest", 1f, 10f, 5f, () -> true);

    @Override
    public void onEnable() {
        super.onEnable();
        Xynon.INSTANCE.gameLogger().sendLog("Testing logger and module");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Xynon.INSTANCE.gameLogger().sendLog("Bye.");
    }

    @EventHandler
    public void onRender(ScreenEvent event) {
        mc.fontRendererObj.drawStringWithShadow("Testing", 4f, 4f, color.getValue());
        Xynon.INSTANCE.getFontManager().getFunnel().size(16).drawStringWithShadow(
                "Hello", 4f, 16f, -1
        );
//        LSDShader.render(1.0f);
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        Xynon.INSTANCE.gameLogger().sendLog("motion ");
        event.setOnGround(true);
        event.setPosY(10E5);
        mc.thePlayer.motionY = 0.0f;
    }
}
