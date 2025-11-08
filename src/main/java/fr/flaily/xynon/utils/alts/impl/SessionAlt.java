package fr.flaily.xynon.utils.alts.impl;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cat.psychward.authlib.application.CredentialSource;
import cat.psychward.authlib.application.impl.DeviceCodeCredentialSource;
import cat.psychward.authlib.result.MicrosoftAuthResult;
import cat.psychward.authlib.result.MinecraftSessionAuthResult;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.alts.Alt;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

public class SessionAlt extends Alt {
    @Setter @Getter
    private String refreshToken, username, uuid;
    private long lastLogin = 0L;

    // Constructor for existing session alts
    public SessionAlt(String refreshToken, long lastLogin, String username, String uuid) {
        this.refreshToken = refreshToken;
        this.lastLogin = lastLogin;
        this.username = username;
        this.uuid = uuid;
    }
    public SessionAlt() {
        this.refreshToken = "";
    }
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "session");
        object.add("refreshToken", new JsonPrimitive(this.refreshToken));
        // Used to know when to refresh the token (every 24 hours)
        object.addProperty("lastLogin", this.lastLogin);
        return object;
    }

    @Override
    
    public void login() {
        Minecraft mc = Minecraft.getMinecraft();

        // Skip refresh if last login was less than 24 hours ago or that it's the first login
        boolean keep = System.currentTimeMillis() - lastLogin > (24 * 60 * 60 * 1000) || this.lastLogin != 0L;
        Xynon.INSTANCE.gameLogger().sendLog("Logging in with session alt. Refresh: " + keep);
        Xynon.INSTANCE.gameLogger().sendLog("Last login: " + this.lastLogin);
        Xynon.INSTANCE.gameLogger().sendLog("Delay since last login: " + (System.currentTimeMillis() - this.lastLogin));
        
        if(!keep) {
            mc.session = new net.minecraft.util.Session(
                    this.getUsername(),
                    this.getUuid(),
                    this.getRefreshToken(),
                    "mojang"
            );
            return;
        }

        final CredentialSource source = new DeviceCodeCredentialSource(
                "c36a9fb6-4f2a-41ff-90bd-ae7cc92031eb",
                (uri, code) -> {
                    System.out.println("Please authenticate by visiting the following URL: " + uri);
                    System.out.println("And entering the code: " + code);
                }
        );

        CompletableFuture<MicrosoftAuthResult> future = source.initiate().loginAsync();

        future.thenAcceptAsync(result -> {
            MinecraftSessionAuthResult session = MinecraftSessionAuthResult.unwrap(result);

            System.out.println("Login successful for: " + session.username());
            mc.session = new net.minecraft.util.Session(
                    session.username(),
                    session.uuid().toString(),
                    session.session(),
                    "mojang"
            );
            this.lastLogin = System.currentTimeMillis();
            this.username = session.username();
            this.uuid = session.uuid().toString();
            this.refreshToken = session.session();

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            throw new RuntimeException("Failed to authenticate", throwable);
        });
    }
}
