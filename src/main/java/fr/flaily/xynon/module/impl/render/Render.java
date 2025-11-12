package fr.flaily.xynon.module.impl.render;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.font.CustomFontRenderer;
public interface Render {

    // Inter fonts
    CustomFontRenderer xsmall = Xynon.INSTANCE.getFontManager().getInter().size(12),
            small   = Xynon.INSTANCE.getFontManager().getInter().size(15),
            medium  = Xynon.INSTANCE.getFontManager().getInter().size(18),
            big     = Xynon.INSTANCE.getFontManager().getInter().size(20),
            bigger  = Xynon.INSTANCE.getFontManager().getInter().size(24),
            huge    = Xynon.INSTANCE.getFontManager().getInter().size(32),

            // TikTok normal
            tiktokXSmall  = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(12),
            tiktokSmall   = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(15),
            tiktokMedium  = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(18),
            tiktokBig     = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(20),
            tiktokBigger  = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(24),
            tiktokHuge    = Xynon.INSTANCE.getFontManager().getTikTokNormal().size(32),

            // TikTok bold
            tiktokBoldXSmall  = Xynon.INSTANCE.getFontManager().getTikTokBold().size(12),
            tiktokBoldSmall   = Xynon.INSTANCE.getFontManager().getTikTokBold().size(15),
            tiktokBoldMedium  = Xynon.INSTANCE.getFontManager().getTikTokBold().size(18),
            tiktokBoldBig     = Xynon.INSTANCE.getFontManager().getTikTokBold().size(20),
            tiktokBoldBigger  = Xynon.INSTANCE.getFontManager().getTikTokBold().size(24),
            tiktokBoldHuge    = Xynon.INSTANCE.getFontManager().getTikTokBold().size(32);
}