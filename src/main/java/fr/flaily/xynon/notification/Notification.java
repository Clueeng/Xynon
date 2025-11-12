package fr.flaily.xynon.notification;

import fr.flaily.xynon.utils.Utils;

// Apple style notification
// Modern aero glass 
public class Notification implements Utils {
    public String title;
    public String description;
    public long delay;
    
    public Notification(String title, String description, long delay) {
        this.title = title;
        this.description = description;
        this.delay = delay;
    }

    public void render() {
        
    }
}
