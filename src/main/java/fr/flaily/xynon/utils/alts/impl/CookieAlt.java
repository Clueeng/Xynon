package fr.flaily.xynon.utils.alts.impl;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.FileUtils;
import fr.flaily.xynon.utils.alts.Alt;
import fr.flaily.xynon.utils.alts.CookieAltsUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

@Getter @Setter
public class CookieAlt extends Alt {
    // When user adds a cookie alt file, we copy it to the client directory, so we store the path here
    private String path;
    // We'll cache the username after reading the cookie file
    private Minecraft mc = Minecraft.getMinecraft();

    public CookieAlt(String path) {
        this.path = path;
        this.username = "Unknown";
    }

    public CookieAlt() {
        this.username = "Unknown";
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
        // Run the whole cookie authentication asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                File source;
                if(this.path == null || this.path.isEmpty()) {
                    source = CookieAltsUtil.getCookieFile();
                    this.path = source.getAbsolutePath();
                }else{
                    source = new File(this.path);
                }

                // Run cookie login (blocking network code)
                CookieAltsUtil.loginWithCookie(source.getAbsolutePath(), account -> {
                    this.status = Status.LOGGING_IN;
                    if (account == null) {
                        Xynon.INSTANCE.gameLogger().sendLog("Failed to login with cookie alt.");
                        return;
                    }

                    // Handle login result safely
                    handleSuccessfulLogin(account);
                });

            } catch (Exception e) {
                this.status = Status.FAILED;
                e.printStackTrace();
                Xynon.INSTANCE.gameLogger().sendLog("Exception while logging in with cookie alt: " + e.getMessage());
            }
        });
    }

    private void handleSuccessfulLogin(Session account) {
        // Switch session on Minecraft’s thread

        this.username = account.getUsername();
        mc.session.switchSession(account);
        this.status = Status.SUCCESS;

        Xynon.INSTANCE.gameLogger().sendLog("Logged in as " + this.username + " using cookie alt.");

        // Avoid duplicates, then add to AltManager
        if(!Xynon.INSTANCE.getAltManager().alreadyIn(this)) {
            Xynon.INSTANCE.getAltManager().addAlt(this);
        }
    }
}
