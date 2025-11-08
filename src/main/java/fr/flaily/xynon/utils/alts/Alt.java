package fr.flaily.xynon.utils.alts;

import com.google.gson.JsonObject;

public class Alt {
    public JsonObject toJson() {
        return new JsonObject();
    }

    public void login() {
        throw new UnsupportedOperationException("This alt type does not support login.");
    }
}
