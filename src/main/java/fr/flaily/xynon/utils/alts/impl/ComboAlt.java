package fr.flaily.xynon.utils.alts.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.utils.alts.Alt;
import lombok.Getter;
import lombok.Setter;

public class ComboAlt extends Alt {
    @Setter @Getter
    private String email, password;

    public ComboAlt(String email, String password) {
        this.email = email;
        this.password = password;
        this.username = email;
    }
    public ComboAlt() {
        this.email = "";
        this.password = "";
        this.username = "Unknown";
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "combo");
        object.add("email", new JsonPrimitive(this.email));
        object.add("username", new JsonPrimitive(this.username));
        if(this.password == null) this.password = "";
        if(!this.password.isEmpty()) {
            object.add("password", new JsonPrimitive(this.password));
        }
        return object;
    }

    public boolean isCracked() {
        return this.password.isEmpty();
    }

    @Override
    public void login() {
        if(!Xynon.INSTANCE.getAltManager().alreadyIn(this)) {
            Xynon.INSTANCE.getAltManager().addAlt(this);
        }
    }
}
