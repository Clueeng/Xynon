package fr.flaily.xynon.utils.alts;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

public class Alt {
    @Getter @Setter
    public String username;

    public JsonObject toJson() {
        return new JsonObject();
    }

    public void login() {
        throw new UnsupportedOperationException("This alt type does not support login.");
    }
}
