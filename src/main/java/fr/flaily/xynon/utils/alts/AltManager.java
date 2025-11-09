package fr.flaily.xynon.utils.alts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.FileUtils;
import lombok.Getter;

public class AltManager {
    // TODO : Replace the login methods with PvpCafe's library when it's ready
    
    @Getter
    public ArrayList<Alt> alts = new ArrayList<>();

    public AltManager() {
        loadAltsFromFile();
        System.out.println("Loaded " + this.alts.size() + " alts.");
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
        this.writeAltsToFile();
        System.out.println("Added alt: " + alt.username);
    }

    public File getAltFile() {
        File altFile = new File(Xynon.INSTANCE.getClientFolder(), "alts.json");
        if(!altFile.exists()) {
            try {
                altFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return altFile;
    }

    public boolean alreadyIn(Alt alt) {
        for(Alt a : getAlts()) {
            if(a.toJson().equals(alt.toJson())) {
                return true;
            }
        }
        return false;
    }

    public void writeAltsToFile() {
        File altFile = getAltFile();

        JsonArray array = new JsonArray();
        for (Alt alt : this.alts) {
            array.add(alt.toJson());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(altFile)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Finished writing alts to file.");
    }

    public void loadAltsFromFile() {
        File altFile = getAltFile();
        String content = FileUtils.readFile(altFile);
        ArrayList<Alt> loadedAlts = parseAltsFromJson(content);
        this.alts.clear();
        this.alts.addAll(loadedAlts);
    }
    private ArrayList<Alt> parseAltsFromJson(String content) {
        ArrayList<Alt> list = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) return list;

        try {
            com.google.gson.JsonElement element = new com.google.gson.JsonParser().parse(content);
            if (!element.isJsonArray()) return list;

            for (com.google.gson.JsonElement altElement : element.getAsJsonArray()) {
                if (!altElement.isJsonObject()) continue;
                com.google.gson.JsonObject obj = altElement.getAsJsonObject();

                if (obj.has("email") && obj.has("password")) {
                    // ComboAlt
                    fr.flaily.xynon.utils.alts.impl.ComboAlt combo = new fr.flaily.xynon.utils.alts.impl.ComboAlt();
                    combo.setEmail(obj.get("email").getAsString());
                    combo.setPassword(obj.get("password").getAsString());
                    list.add(combo);

                } else if (obj.has("path")) {
                    // CookieAlt
                    fr.flaily.xynon.utils.alts.impl.CookieAlt cookie = new fr.flaily.xynon.utils.alts.impl.CookieAlt();
                    cookie.setPath(obj.get("path").getAsString());

                    if (obj.has("username")) {
                        cookie.setUsername(obj.get("username").getAsString());
                    }

                    list.add(cookie);
                }else if(obj.has("refreshToken")) {
                    // SessionAlt
                    fr.flaily.xynon.utils.alts.impl.SessionAlt session = new fr.flaily.xynon.utils.alts.impl.SessionAlt();
                    session.setRefreshToken(obj.get("refreshToken").getAsString());
                    if(obj.has("lastLogin")) {
                        // session.setLastLogin(obj.get("lastLogin").getAsLong());
                        session.lastLogin = obj.get("lastLogin").getAsLong();
                    }
                    if(obj.has("username")) {
                        session.setUsername(obj.get("username").getAsString());
                    }
                    if(obj.has("uuid")) {
                        session.setUuid(obj.get("uuid").getAsString());
                    }
                    list.add(session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }



}
