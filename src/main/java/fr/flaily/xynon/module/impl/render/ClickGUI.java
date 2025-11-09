package fr.flaily.xynon.module.impl.render;

import fr.flaily.xynon.click.classic.ClassicGUI;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import org.lwjgl.input.Keyboard;

@FeatureInfo(name = "ClickGUI", key = Keyboard.KEY_RSHIFT, category = Module.Category.Render)
public class ClickGUI extends Module {
    private ClassicGUI classicGUI;

    @Override
    public void onEnable() {
        super.onEnable();
//        mc.displayGuiScreen(new ClassicGUI());
        mc.displayGuiScreen(classicGUI == null ? classicGUI = new ClassicGUI() : classicGUI);
        this.toggle();
    }
}
