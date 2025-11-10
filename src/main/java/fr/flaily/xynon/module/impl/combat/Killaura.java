package fr.flaily.xynon.module.impl.combat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.EventTime;
import fr.flaily.xynon.events.game.EventUseItem;
import fr.flaily.xynon.events.player.MotionEvent;
import fr.flaily.xynon.events.player.RotationEvent;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.BooleanSetting;
import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.MathHelper;
import fr.flaily.xynon.utils.Timer;
import fr.flaily.xynon.utils.WorldUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.potion.Potion;

@FeatureInfo(name = "Killaura", category = Module.Category.Combat, key = Keyboard.KEY_R)
public class Killaura extends Module {
    public ModeSetting rotation = mode("Rotation Type", "Instant", "Instant", "Smooth");
    public NumberSetting range = num("Range", 3.0, 6.0, 3.00, 0.05, () -> true);
    public BooleanSetting raycastCheck = bool("Raycast", true, () -> true);
    public MultiSelectSetting allowedEntities = multi("Targets", Arrays.asList("Players", "Mobs"), Arrays.asList("Player", "Mobs"), () -> true);
    public ModeSetting blockMode = mode("Block", "None", "None", "Vanilla", "Legit");


    private Entity target;
    private float yaw, pitch;

    public Timer timer = new Timer();

    long nextDelay = 0L;
    private long lastAttack = 0L;

    @EventHandler
    public void onRotation(RotationEvent event) {
        if(target == null) return;

        event.setYaw(yaw);
        event.setPitch(pitch);
    }

    @EventHandler
    public void calculateRotations(MotionEvent event) {
        if(target == null) return;

        if(event.getTime() == EventTime.POST) {
            float[] toEntity = MathHelper.getRotations(target);
            this.yaw = toEntity[0];
            this.pitch = toEntity[1];
        }
    }

    @EventHandler
    public void applyTarget(UpdateEvent event) {
        java.util.List<Entity> test = WorldUtils.getEntities(e -> {
            return !(e instanceof EntityPlayerSP) && e.isEntityAlive() &&
            ((e instanceof EntityLivingBase) && ((EntityLivingBase)e).getHealth() >= 0.0f) &&
            ((e instanceof EntityPlayer && allowedEntities.isSelected("Players")) ||
            ((e instanceof EntityMob || e instanceof EntityVillager || e instanceof EntityAnimal) && allowedEntities.isSelected("Mobs")))
            && e.getDistanceToEntity(mc.thePlayer) < range.maximum;
        });
        test.sort(Comparator.comparingDouble(e -> e.getDistanceToEntity(mc.thePlayer)));

        if(!test.isEmpty()) {
            this.target = test.getFirst();

        }else{
            this.target = null;
        }
    }

    @EventHandler
    public void performAttack(UpdateEvent event) {
        timer.execute(() -> {
            lastAttack = System.currentTimeMillis();
            // Xynon.INSTANCE.gameLogger().sendLog("Timer reached " + (System.currentTimeMillis()));
            boolean raycast = !raycastCheck.getValue() || MathHelper.isLookingAtEntity(mc.thePlayer, target, range.getValue());
            boolean shouldCrit = mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null && target instanceof EntityLivingBase;
            

            if(raycast) {
                mc.thePlayer.swingItem();
                boolean keepSprint = false;
                if(keepSprint) {
                    if(shouldCrit) {
                        mc.thePlayer.onCriticalHit(target);
                    }
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, Action.ATTACK));
                }else{
                    mc.playerController.attackEntity(mc.thePlayer, target);
                }
            }

            nextDelay = getDelay();
        }, nextDelay, true);
    }

    @EventHandler
    public void onUpdateUse(UpdateEvent event) {
        if(target == null) return;
        int ticksAdvance = 1;

        if(blockMode.is("Legit")) {
            mc.gameSettings.keyBindUseItem.pressed = mc.thePlayer.ticksExisted % 2 == 0;
        }
        if(blockMode.is("Vanilla")) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 9999);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.gameSettings.keyBindUseItem.pressed = false;
    }

    public boolean isExhausted;
    public long getDelay() {
        Random random = new Random();
        if(random.nextFloat() > 0.95) {
            isExhausted = true;
        }
        float a = mc.thePlayer.serverYaw;
        float b = mc.thePlayer.serverPitch;
        float c = mc.thePlayer.ticksExisted;

        float f = 1.0f + (float) (Math.sin(a * b * c) * 0.2f);
        float cps = Math.abs(12 - (6 * f));
        if(isExhausted) {
            cps = Math.max(2, cps * 0.8f - random.nextFloat());
            isExhausted = Math.random() > 0.3f;
        }
        System.out.println("CPS: " + cps);
        return (long) (1000 / cps);
    }

}
