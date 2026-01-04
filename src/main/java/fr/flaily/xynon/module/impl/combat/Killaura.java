package fr.flaily.xynon.module.impl.combat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import fr.flaily.xynon.module.settings.impl.*;
import fr.flaily.xynon.utils.PacketUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
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
    public RangeSetting pitchSmoothSpeed = range("Pitch Smooth", 0.0, 180.0, 20.0, 40.0, 0.5, () -> rotation.is("Smooth"));
    public RangeSetting yawSmoothSpeed = range("Yaw Smooth", 0.0, 180.0, 20.0, 40.0, 0.5, () -> rotation.is("Smooth"));
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

            switch (rotation.getValue()) {
                case "Instant" -> {
                    this.yaw = toEntity[0];
                    this.pitch = toEntity[1];
                }
                case "Smooth" -> {
                    final float pitchSmoothThreshold = MathHelper.randomNumber((float) pitchSmoothSpeed.getValueLow(), (float) pitchSmoothSpeed.getValueHigh());
                    final float yawSmoothThreshold = MathHelper.randomNumber((float) yawSmoothSpeed.getValueLow(), (float) yawSmoothSpeed.getValueHigh());
                    float[] rotations = MathHelper.getRotations(target);
                    float targetYaw = rotations[0];
                    float targetPitch = rotations[1];
                    float yawChange = net.minecraft.util.MathHelper.wrapAngleTo180_float(targetYaw - mc.thePlayer.serverYaw);
                    float pitchChange = net.minecraft.util.MathHelper.wrapAngleTo180_float(targetPitch - mc.thePlayer.serverPitch);
                    pitchChange = Math.max(-pitchSmoothThreshold, Math.min(pitchSmoothThreshold, pitchChange));
                    yawChange = Math.max(-yawSmoothThreshold, Math.min(yawSmoothThreshold, yawChange));

                    this.yaw = mc.thePlayer.serverYaw + yawChange;
                    this.pitch = mc.thePlayer.serverPitch + pitchChange;
                }
            }
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
                // test
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

            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
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
        float c = mc.thePlayer.ticksExisted * 0.1f;

        float f = 1.0f + (float) (Math.sin(c) * 0.5f);

        int maxCps = 20;
        int minCps = 10;
        float cps = Math.abs(maxCps - (minCps * f));
        if(isExhausted) {
            cps = Math.max(2, cps * 0.8f - random.nextFloat());
            isExhausted = Math.random() > 0.3f;
        }
        System.out.println("CPS: " + cps);
        return (long) (1000 / cps);
    }

    public Entity getTarget() {
        return target;
    }

}
