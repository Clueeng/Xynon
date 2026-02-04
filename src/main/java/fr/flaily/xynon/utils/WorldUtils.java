package fr.flaily.xynon.utils;

import java.awt.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;

import fr.flaily.xynon.module.settings.impl.ModeSetting;
import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

public class WorldUtils implements Utils {

    public static List<Entity> getEntities(Predicate<? super Entity> condition) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public static AxisAlignedBB createBoundingBox(BlockPos pos1, BlockPos pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX()) + 1.0;
        double maxY = Math.max(pos1.getY(), pos2.getY()) + 1.0;
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()) + 1.0;

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
    }
    public static AxisAlignedBB createBoundingBox(AxisAlignedBB boundingBox) {
        return boundingBox.offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
    }

    public static AxisAlignedBB createBoundingBox(BlockPos pos1) {
        double minX = pos1.getX();
        double minY = pos1.getY();
        double minZ = pos1.getZ();
        double maxX = pos1.getX() + 1.0;
        double maxY = pos1.getY() + 1.0;
        double maxZ = pos1.getZ() + 1.0;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
    }


    public static float[] getRotationDeltaToBlock(final Vec3 eyes, final Vec3 destination) {
        final double deltaX = destination.xCoord - eyes.xCoord,
                deltaY = destination.yCoord - eyes.yCoord,
                deltaZ = destination.zCoord - eyes.zCoord,
                deltaXZ = Math.hypot(deltaX, deltaZ);

        return new float[]{
                (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F,
                (float) -Math.toDegrees(Math.atan2(deltaY, deltaXZ))
        };
    }

    public static Vec3 getBlockVector(Tuple<BlockPos, EnumFacing> blockData, ModeSetting vectorSetting) {
        final var vec = blockData.getSecond().getDirectionVec();
        if (vectorSetting.is("Furthest")) {
            final var basic = blockData.getFirst().getCenter().addVector(vec.getX() * 0.5, -0.25 + vec.getY() * 0.5, vec.getZ() * 0.5);
            final BlockPos pos = blockData.getFirst().offset(blockData.getSecond());
            if (mc.theWorld.getBlockState(pos) == null) return basic;
            IBlockState blockState = mc.theWorld.getBlockState(pos);
            final AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState);
            if (blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState) == null) return basic;
            final double deltaX1 = bb.minX - mc.thePlayer.posX, deltaX2 = bb.maxX - mc.thePlayer.posX,
                    deltaZ1 = bb.minZ - mc.thePlayer.posZ, deltaZ2 = bb.maxZ - mc.thePlayer.posZ;
            final Vec3 center = new Vec3(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY, Math.floor(mc.thePlayer.posZ) + 0.5);
            return new Vec3(
                    center.xCoord + Math.max(deltaX1, deltaX2),
                    basic.yCoord,
                    center.zCoord + Math.max(deltaZ1, deltaZ2)
            );
        } else if (vectorSetting.is("Sideways")) {
            final var basic = blockData.getFirst().getCenter().addVector(vec.getX() * 0.5, -0.25 + vec.getY() * 0.5, vec.getZ() * 0.5);
            final BlockPos pos = blockData.getFirst().offset(blockData.getSecond());
            if (mc.theWorld.getBlockState(pos) == null) return basic;
            IBlockState blockState = mc.theWorld.getBlockState(pos);
            final AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState);
            if (blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState) == null) return basic;

            final double direction = Math.toRadians(MotionUtils.moveDirection()),
                    x = Math.round(-Math.sin(direction)), z = Math.round(Math.cos(direction));

            final double posX = blockData.getFirst().getX(),
                    posZ = blockData.getFirst().getZ();

            final double deltaX1 = bb.minX - posX, deltaX2 = bb.maxX - posX,
                    deltaZ1 = bb.minZ - posZ, deltaZ2 = bb.maxZ - posZ;
            return new Vec3(
                    posX + (vec.getX() * 0.5 + Math.max(deltaX1, deltaX2)) * 0.25 * x,
                    basic.yCoord,
                    posZ + (vec.getX() * 0.5 + Math.max(deltaZ1, deltaZ2)) * 0.25 * z
            );
        } else if (vectorSetting.is("Back")) {
            final var basic = blockData.getFirst().getCenter().addVector(vec.getX() * 0.5, -0.25 + vec.getY() * 0.5, vec.getZ() * 0.5);
            final BlockPos pos = blockData.getFirst();
            if (mc.theWorld.getBlockState(pos) == null) return basic;
            IBlockState blockState = mc.theWorld.getBlockState(pos);
            final AxisAlignedBB bb = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState);
            if (blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState) == null) return basic;

            final double direction = Math.toRadians(MotionUtils.moveDirection()),
                    x = -Math.sin(direction), z = Math.cos(direction),
                    rX = Math.round(x), rZ = Math.round(z);

            return new Vec3(
                    rX != 0 || !mc.thePlayer.onGround ? basic.xCoord : Math.clamp(mc.thePlayer.posX, bb.minX, bb.maxX),
                    basic.yCoord,
                    rZ != 0 || !mc.thePlayer.onGround ? basic.zCoord : Math.clamp(mc.thePlayer.posZ, bb.minZ, bb.maxZ)
            );
        } else if (vectorSetting.is("Eagle")) {
            final var basic = blockData.getFirst().getCenter().addVector(vec.getX() * 0.5, -0.25 + vec.getY() * 0.5, vec.getZ() * 0.5);
            final float moveYaw = Math.round(MotionUtils.moveDirection() / 45) * 45;
            final double pDirection = Math.toRadians(moveYaw + 45),
                    nDirection = Math.toRadians(moveYaw - 45),
                    pX = Math.sin(pDirection), pZ = Math.cos(pDirection),
                    nX = Math.sin(nDirection), nZ = Math.cos(nDirection);

            final Vec3 pVec = new Vec3(
                    mc.thePlayer.posX + pX * 0.5,
                    basic.yCoord + 0.17 - (mc.thePlayer.isSneaking() ? 0.08 : 0.0),
                    mc.thePlayer.posZ - pZ * 0.5
            ), nVec = new Vec3(
                    mc.thePlayer.posX + nX * 0.5,
                    basic.yCoord + 0.17 - (mc.thePlayer.isSneaking() ? 0.08 : 0.0),
                    mc.thePlayer.posZ - nZ * 0.5
            );

            final Vec3 center = blockData.getFirst().offset(blockData.getSecond()).getCenter();

            final double distToPos = basic.distanceTo(nVec), distToNeg = basic.distanceTo(pVec);
            if (moveYaw % 90 == 0 && (distToPos < 1 || distToNeg < 1) &&
                    mc.thePlayer.getPositionVector().distanceTo(center) < 1.5)
                return distToPos > distToNeg ? pVec : nVec;
            else
                return basic;
        }
        return blockData.getFirst().getCenter().addVector(vec.getX() * 0.5, -0.25 + vec.getY() * 0.5, vec.getZ() * 0.5);
    }



    public static boolean isEntityValid(EntityLivingBase entityLivingBase, MultiSelectSetting validEntities) {
        if(entityLivingBase == mc.thePlayer) return false;

        if(entityLivingBase instanceof EntityPlayer && validEntities.isSelected("Players")) return true;
        if(entityLivingBase instanceof EntityMob && validEntities.isSelected("Mobs")) return true;
        if((entityLivingBase instanceof EntityAnimal || entityLivingBase instanceof EntityVillager)
                && validEntities.isSelected("Passive")) return true;
        return false;
    }
    public static Color getEntityColor(EntityLivingBase entityLivingBase) {
        if(entityLivingBase == mc.thePlayer) return null;

        if(entityLivingBase instanceof EntityPlayer) return new Color(255, 0, 0);
        if(entityLivingBase instanceof EntityMob) return new Color(0, 0, 255);
        if((entityLivingBase instanceof EntityAnimal || entityLivingBase instanceof EntityVillager)) return new  Color(0, 255, 0);
        return new Color(0, 0, 0);
    }

    public static BlockPos getClosestBlock(Material material, int radius) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos pos = new BlockPos(mc.thePlayer.getPosition());

        BlockPos closest = null;
        double closestDistSq = Double.MAX_VALUE;

        for(int x = -radius; x <= radius; x++) {
                for(int z = -radius; z <= radius; z++) {
                        for(int y = -radius; y <= radius; y++) {
                                BlockPos blockPos = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                                if(mc.theWorld.getBlockState(blockPos).getBlock().getMaterial() == material) {
                                        double dist = blockPos.distanceSq(pos);
                                        if(dist <= closestDistSq) {
                                                closestDistSq = dist;
                                                closest = blockPos;
                                        }
                                }
                        }
                }
        }
        if(closest != null) {
                System.out.println(closest);
        }

        return closest;
    }

}
