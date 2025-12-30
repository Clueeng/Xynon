package fr.flaily.xynon.module.impl.render;

import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.ColorSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;

import java.util.List;

@FeatureInfo(name = "Chams", category = Module.Category.Render)
public class Chams extends Module {

    public ColorSetting shown = color("Shown Color", 0, 255, 0, 255);
    public ColorSetting hidden = color("Hidden Color", 255, 0, 0, 255);

    public MultiSelectSetting validEntities = targetChoice(() -> true);

}
