package fr.flaily.xynon;

import best.azura.eventbus.core.EventBus;
import fr.flaily.xynon.command.CommandManager;
import fr.flaily.xynon.module.ModuleManager;
import fr.flaily.xynon.module.impl.render.Render;
import fr.flaily.xynon.notification.NotificationManager;
import fr.flaily.xynon.utils.alts.AltManager;
import fr.flaily.xynon.utils.font.FontManager;
import fr.flaily.xynon.utils.irc.XynonClient.LoginResult;
import fr.flaily.xynon.utils.logger.LoggerManager;
import fr.flaily.xynon.utils.logger.impl.DebugLogger;
import fr.flaily.xynon.utils.logger.impl.GameLogger;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Getter
public enum Xynon {
    INSTANCE;
    public final File clientFolder = new File(Minecraft.getMinecraft().mcDataDir, "Xynon");

    public final Log log = LogFactory.getLog(Xynon.class);
    public final LoggerManager loggerManager = new LoggerManager();
    public final EventBus eventBus = new EventBus();
    public final FontManager fontManager = new FontManager();
    public ModuleManager moduleManager;
    public AltManager altManager;
    public CommandManager commandManager;
    public LoginResult user;
    public NotificationManager notificationManager;

    public void start() {
        getLoggerManager().init();
        debugLogger().sendLog("Initializing Xynon");
        if(!clientFolder.exists()) {
            clientFolder.mkdir();
            debugLogger().sendLog("Created client folder at " + clientFolder.getAbsolutePath());
        }

        moduleManager = new ModuleManager();
        altManager = new AltManager();
        commandManager = new CommandManager();
        notificationManager = new NotificationManager();
        fontManager.preload();
    }

    public void stop() {

    }

    public DebugLogger debugLogger() {
        return getLoggerManager().debugLogger;
    }
    public GameLogger gameLogger() {
        return getLoggerManager().gameLogger;
    }

    /*
    Things to add to Xynon
AutoPotion (would prob require some utility event like InputOverrideEvent to prevent the player from clicking keys when modules takeover)
AutoRefill (Can we simulate mouse movements and clicks in java?)
BedInfo
Bedwars Overlay (Minimal)

ViaVersion impl maybe

     */
}
