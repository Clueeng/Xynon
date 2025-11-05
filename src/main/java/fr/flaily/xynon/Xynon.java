package fr.flaily.xynon;

import best.azura.eventbus.core.EventBus;
import fr.flaily.xynon.module.ModuleManager;
import fr.flaily.xynon.utils.font.FontManager;
import fr.flaily.xynon.utils.logger.LoggerManager;
import fr.flaily.xynon.utils.logger.impl.DebugLogger;
import fr.flaily.xynon.utils.logger.impl.GameLogger;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Getter
public enum Xynon {
    INSTANCE;

    public final Log log = LogFactory.getLog(Xynon.class);
    public final LoggerManager loggerManager = new LoggerManager();
    public final EventBus eventBus = new EventBus();
    public final FontManager fontManager = new FontManager();
    public ModuleManager moduleManager;

    public void start() {
        getLoggerManager().init();
        debugLogger().sendLog("Initializing Xynon");
        moduleManager = new ModuleManager();
    }

    public void stop() {

    }

    public DebugLogger debugLogger() {
        return getLoggerManager().debugLogger;
    }
    public GameLogger gameLogger() {
        return getLoggerManager().gameLogger;
    }

}
