package fr.flaily.xynon.utils.alts.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import fr.flaily.xynon.utils.alts.Alt;
import lombok.Getter;
import lombok.Setter;

public class ComboAlt extends Alt {
    @Setter @Getter
    private String email, password;

    public ComboAlt(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public ComboAlt() {
        this.email = "";
        this.password = "";
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "combo");
        object.add("email", new JsonPrimitive(this.email));
        object.add("password", new JsonPrimitive(this.password));
        return object;
    }

    @Override
    public void login() {
        
    }
}
