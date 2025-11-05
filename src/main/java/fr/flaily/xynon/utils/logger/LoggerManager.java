package fr.flaily.xynon.utils.logger;

import fr.flaily.xynon.utils.logger.impl.DebugLogger;
import fr.flaily.xynon.utils.logger.impl.GameLogger;

public class LoggerManager {
    public DebugLogger debugLogger;
    public GameLogger gameLogger;

    public void init() {
        this.debugLogger = new DebugLogger();
        this.gameLogger = new GameLogger();
    }
}
