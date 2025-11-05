package fr.flaily.xynon.click.classic.frame.module;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.font.CustomFontRenderer;

import java.awt.*;

public interface ModuleStyle {
    int width();
    int height();
    int baseHeight = 18;

    int backgroundColor = new Color(23, 23, 23).getRGB(),
        toggledColor    = new Color(114, 114, 114).getRGB(),
        accentColor    = new Color(38, 163, 107).getRGB();

    default int getColor(boolean state) {
        return state ? toggledColor
        : backgroundColor;
    }

    CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(18);
}
