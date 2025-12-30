package fr.flaily.xynon.command.impl;

import com.google.common.collect.Lists;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.command.Command;
import fr.flaily.xynon.notification.impl.list.AppleNotification;

import java.util.Arrays;

public class NotificationCommand extends Command {
    public NotificationCommand() {
        super("notif");
    }

    @Override
    public void execute(String[] args) {
        Xynon.INSTANCE.getNotificationManager().add(new AppleNotification("Name", "This is a test description lol", 2000L));
        Xynon.INSTANCE.gameLogger().sendLog("Sent debug notification");
    }

    @Override
    public String[] helpMessage() {
        return (String[]) Lists.newArrayList(".notif to send a test notification", "used for debugging").toArray();
    }
}
