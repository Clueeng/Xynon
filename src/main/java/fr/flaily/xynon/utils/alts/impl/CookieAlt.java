package fr.flaily.xynon.utils.alts.impl;

import java.io.File;

import com.google.gson.JsonObject;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.FileUtils;
import fr.flaily.xynon.utils.alts.Alt;
import fr.flaily.xynon.utils.alts.CookieAltsUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Getter @Setter
public class CookieAlt extends Alt {
    // When user adds a cookie alt file, we copy it to the client directory, so we store the path here
    private String path;
    // We'll cache the username after reading the cookie file
    private String username = "Unknown";
    private Minecraft mc = Minecraft.getMinecraft();

    public CookieAlt(String path) {
        this.path = path;
    }

    public CookieAlt() {
    }
    
    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "cookie");
        object.addProperty("path", this.path);
        object.addProperty("username", this.username);
        return object;
    }

    @Override
    public void login() {
        try {
            File source = new File(this.path);
            CookieAltsUtil.loginWithCookie(source.getAbsolutePath(), account -> {
                if(account != null) {
                    this.username = account.getUsername();
                    mc.session.switchSession(account);
                } else {
                    throw new RuntimeException("Failed to login with cookie alt.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
