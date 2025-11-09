package fr.flaily.xynon.module.impl.render;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.font.CustomFontRenderer;

public interface Render {
    public CustomFontRenderer xsmall = Xynon.INSTANCE.getFontManager().getInter().size(12),
                            small  = Xynon.INSTANCE.getFontManager().getInter().size(15),
                            medium  = Xynon.INSTANCE.getFontManager().getInter().size(18),
                            big  = Xynon.INSTANCE.getFontManager().getInter().size(20),
                            bigger  = Xynon.INSTANCE.getFontManager().getInter().size(24),
                            huge     = Xynon.INSTANCE.getFontManager().getInter().size(32);
}
