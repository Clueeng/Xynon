package fr.flaily.xynon.module.impl.combat;

import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.NumberSetting;

@FeatureInfo(name = "Reach", category = Module.Category.Combat)
public class Reach extends Module {
    public NumberSetting range = num("Range", 3.0f, 6.0f, 3.0f, 0.05f, () -> true);

}
