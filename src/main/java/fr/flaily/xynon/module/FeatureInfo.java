package fr.flaily.xynon.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureInfo {
    String name();
    int key();
    Module.Category category();
}
