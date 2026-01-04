package fr.flaily.xynon.module.impl.render.overlay;

import com.mojang.authlib.GameProfile;
import fr.flaily.xynon.Xynon;
import fr.flaily.xynon.events.network.PacketEvent;
import lombok.Setter;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.*;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

import static fr.flaily.xynon.utils.Utils.mc;

public class BedwarsPlayer {
        // Color of armor
        @Setter
        public Color color;
        public int entityId;
        public UUID uuid;
        // 0 = hand, 1 = boot, 2 = leg, 3 = chest, 4 = head
        public ItemStack[] entityStuff = new ItemStack[5];
        // idk how to check for that
        public boolean isInvisible;
        private NetworkPlayerInfo networkPlayerInfo;
        private GameProfile gameProfile;

        public BedwarsPlayer(S0CPacketSpawnPlayer p) {
//            this.color = new Color(255, 255, 255);
            this.uuid = p.getPlayer();
            this.entityId = p.getEntityID();
        }

        private GameProfile getGameProfile() {
            if(gameProfile == null) {
                this.networkPlayerInfo = mc.getNetHandler().getPlayerInfo(this.uuid);
                this.gameProfile = this.networkPlayerInfo.getGameProfile();
            }
            return this.gameProfile;
        }

        public String getName() {
            if(getGameProfile() == null) {
                gameProfile = getGameProfile();
            }
            return getGameProfile().getName();
        }

        public boolean isBedwars() {
            // Check if the player is wearing a leather chestplate
            ItemStack chest = this.entityStuff[3];
            if(chest == null) {
                return false;
            }
            // If it has a custom color, we probably are in bedwars

            if (chest.getItem() instanceof ItemArmor armor) {
                if (armor.getArmorMaterial() != ItemArmor.ArmorMaterial.LEATHER) {
                    return false;
                }

                return chest.hasTagCompound()
                        && chest.getTagCompound().hasKey("display", 10)
                        && chest.getTagCompound().getCompoundTag("display").hasKey("color", 3);
            }

            return false;
        }

        public Color getTeamColor() {
            if(color != null) {
                return color;
            }
            ItemStack chest = entityStuff[3];
            if (chest == null) return new Color(255, 255, 255);

            NBTTagCompound display = chest.getTagCompound()
                    .getCompoundTag("display");

            int rgb = display.getInteger("color");
            this.color = new Color(rgb);
            return color;
        }

        public boolean hasBeenInitialized() {
            return this.getName() != null;
        }

        public void updateState(PacketEvent packetEvent) {
            if(packetEvent.getPacket() instanceof S04PacketEntityEquipment equipment) {
                this.color = null;
                this.getTeamColor();
                if(equipment.getEntityID() == this.entityId) {
                    entityStuff[equipment.getEquipmentSlot()] = equipment.getItemStack();
                }
            }
            if (packetEvent.getPacket() instanceof S1DPacketEntityEffect effect) {
                if (effect.getEntityId() == this.entityId && effect.getEffectId() == 14) {
                    this.isInvisible = true;
                }
            }
            if (packetEvent.getPacket() instanceof S1EPacketRemoveEntityEffect remove) {
                if (remove.getEntityId() == this.entityId && remove.getEffectId() == 14) {
                    this.isInvisible = false;
                }
            }
        }
        // has bed

    }