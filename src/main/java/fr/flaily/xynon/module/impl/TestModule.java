package fr.flaily.xynon.module.impl;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.events.player.MotionEvent;
import fr.flaily.xynon.events.player.RotationEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.*;
import fr.flaily.xynon.utils.alts.Alt;
import fr.flaily.xynon.utils.alts.impl.SessionAlt;

import org.lwjgl.input.Keyboard;

import java.util.Arrays;

@FeatureInfo(name = "Test", category = Module.Category.Combat, key = Keyboard.KEY_I)
public class TestModule extends Module {
    public ModeSetting testmode3 = mode("TestMode", "Value1", "Value1", "Value2", "Value3", "Value4", "Value5");
    //    public ModeSetting testmode = mode("TestMode", "Value1", "Value1", "Value2", "Value3");
    public BooleanSetting test = bool("Test", true, () -> true);
    public BooleanSetting testTwo = bool("Other Test", true, () -> true);
//    public ModeSetting testmode2 = mode("TestMode", "Value1", "Value1", "Value2", "Value3");
    public ColorSetting color = color("Test", 1f, 1f, 1f, 255);
    public NumberSetting numberSetting = num("NumTest", 1f, 10f, 5f, () -> true);
    public MultiSelectSetting selectSetting = multi("ChoiceTest", Arrays.asList(), Arrays.asList("A", "B", "C"), () -> true);
    public RangeSetting rangeSetting = range("RangeTest", 0.0, 100.0, 25.0, 75.0, 1.0, () -> true);

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    public void onRotate(RotationEvent event) {
        event.setYaw(event.getYaw() + 90.0f);
    }

    @EventHandler
    public void onRender(ScreenEvent event) {

    }

    @EventHandler
    public void onMotion(MotionEvent event) {
//        Xynon.INSTANCE.gameLogger().sendLog("motion ");
//        event.setOnGround(true);
//        mc.thePlayer.motionY = 0.0f;
    }
}
