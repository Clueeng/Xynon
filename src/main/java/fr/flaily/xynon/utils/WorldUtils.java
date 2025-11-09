package fr.flaily.xynon.utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class WorldUtils {

    public static List<Entity> getEntities(Predicate<? super Entity> condition) {
        return Minecraft.getMinecraft().theWorld.loadedEntityList.stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

}
