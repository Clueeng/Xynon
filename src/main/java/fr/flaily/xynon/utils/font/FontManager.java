package fr.flaily.xynon.utils.font;

import java.util.HashMap;

public class FontManager {

    public HashMap<String, CustomFont> fonts;

    public FontManager() {
        fonts = new HashMap<>();

        // Add fonts here
        fonts.put("funnel", new CustomFont("Funnel.ttf"));
        fonts.put("inter", new CustomFont("Inter.ttf"));
        fonts.put("tiktok_bold", new CustomFont("TikTok_Bold.ttf"));
        fonts.put("tiktok_norm", new CustomFont("TikTok_Regular.ttf"));
    }

    public CustomFont getFunnel() {
        return getFont("funnel");
    }
    public CustomFont getInter() {
        return getFont("inter");
    }
    public CustomFont getTikTokBold() {
        return getFont("tiktok_bold");
    }
    public CustomFont getTikTokNormal() {
        return getFont("tiktok_norm");
    }
    private CustomFont getFont(String name) {
        if(!fonts.containsKey(name)) {
            System.out.println("Font not found");
            return null;
        }
        return fonts.get(name);
    }

    public void preload() {
        int[] sizes = { 12, 15, 18, 20, 24, 32 };

        for (int size : sizes) {
            getInter().size(size);
            getTikTokNormal().size(size);
            getTikTokBold().size(size);
            getFunnel().size(size);
        }
    }
}
