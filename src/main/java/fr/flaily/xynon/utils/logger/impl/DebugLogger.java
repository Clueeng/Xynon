package fr.flaily.xynon.utils.logger.impl;

import fr.flaily.xynon.utils.logger.Logger;
import net.minecraft.client.Minecraft;

public class DebugLogger extends Logger {
    public DebugLogger() {
        super("xynon debug");
    }

    @Override
    public void sendLog(String s) {
        if(Minecraft.logger == null) {
            defaultLog(s);
            return;
        }
        Minecraft.logger.info(s);
    }
}
