package fr.flaily.xynon.utils.logger.impl;

import fr.flaily.xynon.utils.logger.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import static net.minecraft.util.EnumChatFormatting.*;

public class GameLogger extends Logger {
    public GameLogger() {
        super("Xynon");
    }

    @Override
    public void sendLog(String s) {
        if(Minecraft.getMinecraft().thePlayer == null) {
            defaultLog(s);
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(
                DARK_RED + "[" + DARK_GRAY + "Xynon" + DARK_RED + "] " + WHITE + s
        ));
    }
}
