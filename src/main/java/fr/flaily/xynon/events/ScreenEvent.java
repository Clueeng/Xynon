package fr.flaily.xynon.events;

import best.azura.eventbus.events.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@AllArgsConstructor
public class ScreenEvent extends CancellableEvent {
    private float partialTicks;

    public ScaledResolution getSr() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }
}
