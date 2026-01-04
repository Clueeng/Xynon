package fr.flaily.xynon.module.impl.pvp;

import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.impl.render.Render;
import lombok.Getter;
import org.lwjgl.util.vector.Vector2f;

public class ModulePvP extends Module implements Render {
    public enum Category {
        // TODO : add icons
        Visuals('q'),
        QoL('d');
        final char icon;
        Category(char icon) {
            this.icon = icon;
        }
    }
    @Getter
    public Category legitCategory;

    @Getter
    public static class ScreenData {
        public Vector2f screenPos;
        public Vector2f screenSize;

        public ScreenData(Vector2f screenPos, Vector2f screenSize) {
            this.screenPos = screenPos;
            this.screenSize = screenSize;
        }
    }

    @Getter
    public ScreenData screenData;

    public ModulePvP(boolean screenModule) {
        if(this.getClass().isAnnotationPresent(LegitFeatureInfo.class)) {
            LegitFeatureInfo info = getClass().getAnnotation(LegitFeatureInfo.class);
            this.name = info.name();
            this.keyCode = info.key();
            this.legitCategory = info.category();
        }else{
            throw new RuntimeException("Feature does not contain any LegitFeatureInfo");
        }

        if(screenModule)
            this.screenData = new ScreenData(new Vector2f(0, 0), new  Vector2f(100, 24));

    }
}
