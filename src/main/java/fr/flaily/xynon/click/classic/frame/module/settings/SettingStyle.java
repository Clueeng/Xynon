package fr.flaily.xynon.click.classic.frame.module.settings;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.font.CustomFontRenderer;

import java.awt.*;

public interface SettingStyle {
    float height();
    // Same as category frame's width
    int width();

    int backgroundColor = new Color(31, 31, 31).getRGB(),
            toggledColor    = new Color(114, 114, 114).getRGB(),
            accentColor    = new Color(38, 163, 107).getRGB();

    CustomFontRenderer font = Xynon.INSTANCE.getFontManager().getFunnel().size(16);
}
