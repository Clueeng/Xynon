package net.minecraft.util;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.game.EventOverrideInput;

public class MouseHelper
{
    /** Mouse delta X this frame */
    public int deltaX;

    /** Mouse delta Y this frame */
    public int deltaY;

    /**
     * Grabs the mouse cursor it doesn't move and isn't seen.
     */
    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    /**
     * Ungrabs the mouse cursor so it can be moved and set it to the center of the screen
     */
    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        EventOverrideInput input = new EventOverrideInput();
        Xynon.INSTANCE.getEventBus().post(input);

        int mdx = Mouse.getDX();
        int mdy = Mouse.getDY();
        this.deltaX = mdx + input.getDX();
        this.deltaY = mdy + input.getDY();
        
    }
}
