package fr.flaily.xynon.module.impl.render;

import best.azura.eventbus.handler.EventHandler;
import fr.flaily.xynon.events.player.UpdateEvent;
import fr.flaily.xynon.events.render.ScreenEvent;
import fr.flaily.xynon.events.render.WorldRenderEvent;
import fr.flaily.xynon.module.FeatureInfo;
import fr.flaily.xynon.module.Module;
import fr.flaily.xynon.module.settings.impl.NumberSetting;
import fr.flaily.xynon.utils.render.RenderUtil;
import fr.flaily.xynon.utils.render.WorldToScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.awt.*;
import java.util.ArrayList;

import static fr.flaily.xynon.utils.render.WorldToScreen.worldToScreen;

@FeatureInfo(name = "Bed Info", key = -1, category = Module.Category.Render)
public class BedInfo extends Module implements Render {

    private final ArrayList<BlockNeighborResult> foundBed = new ArrayList<>();
    private final double MAX_DISTANCE = 128.0D;

    private final NumberSetting infoSize = num("Size", 8.0, 64.0, 32.0, 8.0, () -> true);

    @EventHandler
    public void onRender(ScreenEvent event) {
        ScaledResolution sr = event.getSr();
        for(BlockNeighborResult result : foundBed) {
            BlockPos pos = result.getPos();

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            float[] screenPos = worldToScreen(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, sr);
            boolean anyVisible = false;

            if (screenPos != null && screenPos[2] >= 0.0f && screenPos[2] < 1.0f) {
                minX = Math.min(minX, screenPos[0]);
                minY = Math.min(minY, screenPos[1]);
                maxX = Math.max(maxX, screenPos[0]);
                maxY = Math.max(maxY, screenPos[1]);
                anyVisible = true;
            }

            if(anyVisible) {
                StringBuilder test = new StringBuilder();
                ArrayList<IBlockState> neighbors = result.getNeighbors();
                int totalBlocks = neighbors.size();

                int baseSize = 16;
                int textureSize = infoSize.getValue().intValue();
                float scaleUp = textureSize / 16f;

                float middleX = minX + ((maxX - minX) / 2f);
                float y = maxY;

                float totalWidthAtScale = totalBlocks * textureSize;

                GlStateManager.pushMatrix();
                GlStateManager.translate(middleX, y, 0);
                GlStateManager.scale(scaleUp, scaleUp, 1.0f);

                float startX = -(totalBlocks * baseSize) / 2f;
                GlStateManager.translate(startX, 0, 0);

                RenderHelper.enableGUIStandardItemLighting();

                int index = 0;
                for(IBlockState b : neighbors) {
                    int meta = b.getBlock().getMetaFromState(b);
                    ItemStack stack = new ItemStack(b.getBlock(), 1, meta);
                    mc.getItemRenderer().itemRenderer.renderItemIntoGUI(stack, index * baseSize, 0);
                    index++;
                }
                if(totalBlocks > 0) {
                    RenderUtil.drawRoundedRect3(-2, -2, (totalWidthAtScale / scaleUp) + 4, (textureSize / scaleUp) + 4, 5f, new Color(0, 0, 0, 100).getRGB());
                }

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();

                GlStateManager.popMatrix();
                float testLength = big.getWidth(test.toString());
                big.drawStringWithShadow(test.toString(), minX - (testLength / 2f), y, -1);

            }
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent event) {
        WorldToScreen.updateMatrices();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.ticksExisted % 10 == 0) {
            foundBed.forEach(BlockNeighborResult::updateState);
        }

        if(mc.thePlayer.ticksExisted % 150 != 0)
            return;

        // Reset beds
        this.foundBed.clear();
        // Get the chunks around the player
        ArrayList<Chunk> chunks = getChunks(mc.gameSettings.renderDistanceChunks);
        for(Chunk chunk : chunks) {
            scanChunkForBeds(chunk);
        }
    }

    private ArrayList<Chunk> getChunks(int nAround) {
        ArrayList<Chunk> chunks = new ArrayList<>();

        BlockPos current = mc.thePlayer.getPosition();
        // remove le x % 16 de xpos
        int[] corner = new int[] {
                current.getX() >> 4,
                current.getY() >> 4,
                current.getZ() >> 4
        };
        for(int x = -nAround; x <= nAround; x++) {
            for(int z = -nAround; z <= nAround; z++) {
                int chunkX = corner[0] + x;
                int chunkZ = corner[2] + z;
                Chunk currentChunk = mc.theWorld.getChunkFromChunkCoords(chunkX, chunkZ);
                chunks.add(currentChunk);
            }
        }
        return chunks;
    }


    private void scanChunkForBeds(Chunk chunk) {
        if (chunk == null || !chunk.isLoaded())
            return;

        ExtendedBlockStorage[] sections = chunk.getBlockStorageArray();

        for (int sectionY = 0; sectionY < sections.length; sectionY++) {
            ExtendedBlockStorage section = sections[sectionY];
            if (section == null)
                continue;

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = section.getBlockByExtId(x, y, z);
                        if (block instanceof BlockBed bed) {
                            int worldX = (chunk.xPosition << 4) + x;
                            int worldY = (sectionY << 4) + y;
                            int worldZ = (chunk.zPosition << 4) + z;

                            BlockPos bedPos = new BlockPos(worldX, worldY, worldZ);
                            if(mc.theWorld.getBlockState(bedPos).getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD){
                                foundBed.add(new BlockNeighborResult(bedPos));
                            }
                        }
                    }
                }
            }
        }
    }

    static class BlockNeighborResult {
        public BlockPos getPos() {
            return pos;
        }

        public ArrayList<IBlockState> getNeighbors() {
            return neighbors;
        }

        public BlockPos pos;
        public ArrayList<IBlockState> neighbors;
        public BlockNeighborResult(BlockPos pos) {
            this.pos = pos;
            this.neighbors = getNeighboringMaterials(pos);
        }

        public void updateState() {
            this.neighbors = getNeighboringMaterials(this.pos);
        }

        private ArrayList<IBlockState> getNeighboringMaterials(BlockPos pos) {
            ArrayList<IBlockState> blockTypes = new ArrayList<>();
            for(int y = 0; y < 2; y++) {
                for(int i = 1; i < 3; i++) {
                    for(EnumFacing axis : EnumFacing.Plane.HORIZONTAL.facings()) {
                        BlockPos cPos = pos.offset(axis, i).up(y);
                        IBlockState current = mc.theWorld.getBlockState(cPos);
                        if(getBedwarsBlocks().contains(current.getBlock())
                                && !blockTypes.contains(current)) {
                            blockTypes.add(current);
                        }
                    }
                }
            }
            return blockTypes;
        }

        private final ArrayList<Block> bedwarsBlocks = new ArrayList<>();
        private ArrayList<Block> getBedwarsBlocks() {
            if(!bedwarsBlocks.isEmpty()) return bedwarsBlocks;
            bedwarsBlocks.add(Blocks.end_stone);
            bedwarsBlocks.add(Blocks.wool);
            bedwarsBlocks.add(Blocks.obsidian);
            bedwarsBlocks.add(Blocks.clay);
            bedwarsBlocks.add(Blocks.glass);
            bedwarsBlocks.add(Blocks.stained_glass);
            bedwarsBlocks.add(Blocks.hardened_clay);
            bedwarsBlocks.add(Blocks.stained_hardened_clay);
            bedwarsBlocks.add(Blocks.ladder);
            return bedwarsBlocks;
        }
    }

}
