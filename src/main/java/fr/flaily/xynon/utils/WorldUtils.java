package fr.flaily.xynon.utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fr.flaily.xynon.module.settings.impl.MultiSelectSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class WorldUtils implements Utils {

    public static List<Entity> getEntities(Predicate<? super Entity> condition) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }


    public static boolean isEntityValid(EntityLivingBase entityLivingBase, MultiSelectSetting validEntities) {
        if(entityLivingBase == mc.thePlayer) return false;

        if(entityLivingBase instanceof EntityPlayer && validEntities.isSelected("Players")) return true;
        if(entityLivingBase instanceof EntityMob && validEntities.isSelected("Mobs")) return true;
        if((entityLivingBase instanceof EntityAnimal || entityLivingBase instanceof EntityVillager)
                && validEntities.isSelected("Passive")) return true;
        return false;
    }

}
