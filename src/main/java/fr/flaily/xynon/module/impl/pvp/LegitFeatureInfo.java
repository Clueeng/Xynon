package fr.flaily.xynon.module.impl.pvp;

import fr.flaily.xynon.module.Module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface LegitFeatureInfo {
    String name();
    int key() default -1;
    ModulePvP.Category category();
}
