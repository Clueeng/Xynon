package fr.flaily.xynon.module.impl.combat;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.game.EventOverrideInput;
import fr.flaily.xynon.events.game.EventRunTick;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.RangeSetting;
import fr.flaily.xynon.utils.Timer;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Random;

@FeatureInfo(name = "Auto Clicker", key = -1, category = Module.Category.Combat)
public class AutoClicker extends Module {
    // Settings
    private Timer leftTimer = new Timer();
    private Timer rightTimer = new Timer();

    public MultiSelectSetting buttons = multi("Button", List.of("Left", "Right"), List.of(""), ()->true);
    public RangeSetting leftCPS = range("Left CPS", 1f, 20f, 12.0f, 14.0f, 0.25f, () -> buttons.isSelected("Left"));
    public RangeSetting rightCPS = range("Right CPS", 1f, 20f, 12.0f, 14.0f, 0.25f, () -> buttons.isSelected("Right"));
    public MultiSelectSetting leftConditions = multi("Left Conditions", List.of("No Inventory", "Weapon Only", "Break Blocks")
            , List.of(), ()->true);
    public MultiSelectSetting rightConditions = multi("Right Conditions", List.of("No Inventory", "Blocks Only")
            , List.of(), ()->true);

    private int[] getMouseCoords(GuiScreen gui) {
        int i = Mouse.getEventX() * gui.width / mc.displayWidth;
        int j = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;
        return new int[]{i, j};
    }

    @SneakyThrows
    private void leftClick() {
        if(!isConditionMet(leftConditions)){
            return;
        }
        if(mc.thePlayer.isUsingItem()) return;
        if(mc.currentScreen instanceof GuiContainer gui) {
            int[] mouse = getMouseCoords(gui);
            gui.mouseClicked(mouse[0], mouse[1], 0);
            return;
        }

        mc.clickMouse();

    }
    @SneakyThrows
    private void rightClick() {
        if(!isConditionMet(rightConditions)) return;
        if(mc.thePlayer.isUsingItem()) return;
        if(mc.currentScreen instanceof GuiContainer gui) {
            int[] mouse = getMouseCoords(gui);
            gui.mouseClicked(mouse[0], mouse[1], 1);
            return;
        }
        mc.rightClickMouse();
    }

    @EventHandler
    public void overrideClicks(EventOverrideInput event) {
        if(Mouse.isButtonDown(0) && isConditionMet(leftConditions)) {
            event.choke(mc.gameSettings.keyBindAttack);
        }
        if(Mouse.isButtonDown(1) && isConditionMet(rightConditions)) {
            event.choke(mc.gameSettings.keyBindUseItem);
        }
    }

    @EventHandler
    public void onTick(EventRunTick eventRunTick) {
        leftTimer.execute(() -> {
            if(Mouse.isButtonDown(0)) {
                leftClick();
            }
        }, getLeftDelay(), true);
        if(Mouse.isButtonDown(1)) {
            rightTimer.execute(this::rightClick, getRightDelay(), true);
        }
    }

    private long getLeftDelay() {
        return randomizeDelay(leftCPS);
    }
    private long getRightDelay() {
        return randomizeDelay(rightCPS);
    }

    private long randomizeDelay(RangeSetting cps) {
        Random r = new Random();
        float highEnd = (float) cps.getValueHigh();
        float lowEnd = cps.getValue().floatValue();
        float delta = highEnd - lowEnd;

        float cpsRandom = highEnd - (delta * r.nextFloat());
        return (long) (1000L / cpsRandom);
    }


    private boolean isConditionMet(MultiSelectSetting condition) {
        if(condition.isSelected("No Inventory") && mc.currentScreen instanceof GuiContainer) return false;
        if(condition.isSelected("Weapon Only") && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword)) return false;
        if(condition.isSelected("Blocks Only") && !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBlock)) return false;
        if(condition.isSelected("Break Blocks") && (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)) return false;
        return true;
    }

}
