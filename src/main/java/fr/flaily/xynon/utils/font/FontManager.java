package fr.flaily.xynon.utils.font;

import java.util.HashMap;

public class FontManager {

    public HashMap<String, CustomFont> fonts;

    public FontManager() {
        fonts = new HashMap<>();

        // Add fonts here
        fonts.put("funnel", new CustomFont("Funnel.ttf"));
    }

    public CustomFont getFunnel() {
        if(!fonts.containsKey("funnel")) {
            System.out.println("Font not found");
            return null;
        }
        return fonts.get("funnel");
    }

}
