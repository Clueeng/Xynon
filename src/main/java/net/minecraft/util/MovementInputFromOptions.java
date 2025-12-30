package net.minecraft.util;

import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.game.EventMoveInput;
import fr.flaily.xynon.events.game.EventOverrideInput;
import net.minecraft.client.settings.GameSettings;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        EventOverrideInput input = new EventOverrideInput();
        Xynon.INSTANCE.getEventBus().post(input);

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (this.gameSettings.keyBindForward.isKeyDown() && !input.isChoked(this.gameSettings.keyBindForward))
        {
            ++this.moveForward;
        }

        if (this.gameSettings.keyBindBack.isKeyDown() && !input.isChoked(this.gameSettings.keyBindBack))
        {
            --this.moveForward;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown() && !input.isChoked(this.gameSettings.keyBindLeft))
        {
            ++this.moveStrafe;
        }

        if (this.gameSettings.keyBindRight.isKeyDown() && !input.isChoked(this.gameSettings.keyBindRight))
        {
            --this.moveStrafe;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown() && !input.isChoked(this.gameSettings.keyBindJump);
        this.sneak = this.gameSettings.keyBindSneak.isKeyDown() && !input.isChoked(this.gameSettings.keyBindSneak);

        if (this.sneak)
        {
            this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
            this.moveForward = (float)((double)this.moveForward * 0.3D);
        }

        EventMoveInput eventMoveInput = new EventMoveInput(sneak, jump, moveForward, moveStrafe);
        Xynon.INSTANCE.getEventBus().post(eventMoveInput);

        this.sneak = eventMoveInput.sneak;
        this.jump = eventMoveInput.jump;
        this.moveForward = eventMoveInput.forward;
        this.moveStrafe = eventMoveInput.sideways;
    }
}
