/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2013-2014 Keyle & xXLupoXx
 * DungeonCraft is licensed under the GNU Lesser General Public License.
 *
 * DungeonCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.keyle.dungeoncraft.dungeon.generator;

import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import de.keyle.dungeoncraft.util.vector.BlockVector;
import de.keyle.knbt.TagCompound;
import net.minecraft.server.v1_7_R3.*;

import java.util.*;

public class DungeonChunkGenerator extends Thread {
    private final World world;
    private final int chunkX;
    private final int chunkZ;
    private final DungeonCraftChunkProvider provider;

    private List<BlockVector> tileEntityPositions = new ArrayList<BlockVector>();

    private static Map<String, Class<? extends TileEntity>> tileEntityClasses = new HashMap<String, Class<? extends TileEntity>>();

    static {
        tileEntityClasses.put("Furnace", TileEntityFurnace.class);
        tileEntityClasses.put("Chest", TileEntityChest.class);
        tileEntityClasses.put("EnderChest", TileEntityEnderChest.class);
        tileEntityClasses.put("RecordPlayer", TileEntityRecordPlayer.class);
        tileEntityClasses.put("Trap", TileEntityDispenser.class);
        tileEntityClasses.put("Dropper", TileEntityDropper.class);
        tileEntityClasses.put("Sign", TileEntitySign.class);
        tileEntityClasses.put("MobSpawner", TileEntityMobSpawner.class);
        tileEntityClasses.put("Music", TileEntityNote.class);
        tileEntityClasses.put("Piston", TileEntityPiston.class);
        tileEntityClasses.put("Cauldron", TileEntityBrewingStand.class);
        tileEntityClasses.put("EnchantTable", TileEntityEnchantTable.class);
        tileEntityClasses.put("Airportal", TileEntityEnderPortal.class);
        tileEntityClasses.put("Control", TileEntityCommand.class);
        tileEntityClasses.put("Beacon", TileEntityBeacon.class);
        tileEntityClasses.put("Skull", TileEntitySkull.class);
        tileEntityClasses.put("DLDetector", TileEntityLightDetector.class);
        tileEntityClasses.put("Hopper", TileEntityHopper.class);
        tileEntityClasses.put("Comparator", TileEntityComparator.class);
        tileEntityClasses.put("FlowerPot", TileEntityFlowerPot.class);
        tileEntityClasses = Collections.unmodifiableMap(tileEntityClasses);
    }

    public DungeonChunkGenerator(World world, int chunkX, int chunkZ, DungeonCraftChunkProvider provider) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.provider = provider;
        this.setName("DungeonChunkGenerator(" + chunkX + ", " + chunkZ + ")");
    }

    @SuppressWarnings("unchecked")
    public void run() {
        DungeonField field = DungeonFieldManager.getDungeonFieldForChunk(chunkX, chunkZ);
        Schematic schematic = DungeonFieldManager.getSchematicForDungeonField(field);

        if (schematic != null) {

            int schematicHeight = schematic.getHeight();
            Chunk chunk = new DungeonCraftChunk(world, chunkX, chunkZ);

            int sectionCount = (int) Math.ceil(schematicHeight / 16.D);
            for (int i = 0; i < sectionCount; i++) {
                chunk.i()[i] = generateSectionBlocks(i, chunkX - field.getChunkX(), chunkZ - field.getChunkZ(), schematic);
            }
            setBiomes(chunk, chunkX - field.getChunkX(), chunkZ - field.getChunkZ(), schematic);

            Map<BlockVector, TagCompound> tileEntities = schematic.getTileEntities();
            for (BlockVector tileEntityPosition : tileEntityPositions) {
                if (tileEntities.containsKey(tileEntityPosition)) {
                    TagCompound tileEntityCompound = tileEntities.get(tileEntityPosition);
                    TileEntity tileEntity = createTileEntity((NBTTagCompound) BukkitUtil.compoundToVanillaCompound(tileEntityCompound));
                    tileEntity.a(this.world);
                    tileEntity.x = field.getBlockX() + tileEntityPosition.getBlockX();
                    tileEntity.y = tileEntityPosition.getBlockY();
                    tileEntity.z = field.getBlockZ() + tileEntityPosition.getBlockZ();
                    chunk.tileEntities.put(new ChunkPosition(tileEntityPosition.getBlockX() & 0xF, tileEntityPosition.getBlockY(), tileEntityPosition.getBlockZ() & 0xF), tileEntity);
                    //chunk.a(tileEntityPosition.getBlockX() & 0xF, tileEntityPosition.getBlockY(), tileEntityPosition.getBlockZ() & 0xF, tileEntity);
                }
            }
            tileEntityPositions.clear();

            //chunk.initLighting(); //causes ModificationException in:
            // Collections.sort(entityplayer.chunkCoordIntPairQueue, new ChunkCoordComparator(entityplayer));

            // make the chunk ready (faked)
            chunk.lit = true;
            chunk.m = true;
            chunk.done = true;
            // ----------------------

            provider.addChunk(chunk);
            //DungeonCraftLogger.write("Generated Chunk from schematic at X(" + chunkX + ") Z(" + chunkZ + ")");
        }
    }

    public static TileEntity createTileEntity(NBTTagCompound nbtTagCompound) {
        TileEntity tileEntity = null;
        try {
            Class<? extends TileEntity> tileEntityClass = tileEntityClasses.get(nbtTagCompound.getString("id"));

            if (tileEntityClass.equals(TileEntityCommand.class)) { // http://i.imgur.com/E3MKkqv.jpg
                synchronized (Blocks.COMMAND) {
                    tileEntity = ((BlockCommand) Blocks.COMMAND).a(null, 0);
                }
            } else {
                if (tileEntityClass != null) {
                    tileEntity = tileEntityClass.newInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tileEntity != null) {
            tileEntity.a(nbtTagCompound);
        } else {
            DungeonCraftLogger.write("Skipping BlockEntity with id " + nbtTagCompound.getString("id"));
        }
        return tileEntity;
    }

    public ChunkSection generateSectionBlocks(int section, int chunkX, int chunkZ, Schematic schematic) {
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] blocks = schematic.getBlocks();
        byte[] data = schematic.getData();
        byte[] skyLight = schematic.getSkyLight();
        byte[] blockLight = schematic.getBlockLight();

        ChunkSection newChunkSection = new ChunkSection(section, true); //2nd parameter -> !world.worldProvider.g -> hasWorldSkylight
        int yOffset = section * 16;
        int maxY = schematic.getHeight() > yOffset ? 16 : yOffset - schematic.getHeight();

        int maxIndex = schematicWidth * schematicLength * schematic.getHeight();

        Block block;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < maxY; ++y) {
                for (int z = 0; z < 16; ++z) {
                    int index = getSchematicIndex(chunkX, chunkZ, x, y + yOffset, z, schematicLength, schematicWidth);
                    if (index != -1) {
                        if (index >= maxIndex) {
                            continue;
                        }
                        block = Block.e(blocks[index] & 0xFF);
                        newChunkSection.setTypeId(x, y, z, block);
                        newChunkSection.setData(x, y, z, data[index]);
                        newChunkSection.setEmittedLight(x, y, z, blockLight[index]);
                        newChunkSection.setSkyLight(x, y, z, skyLight[index]);
                        if (isTileEntityBlock(block)) {
                            BlockVector pos = new BlockVector((chunkX * 16) + x, y + yOffset, (chunkZ * 16) + z);
                            tileEntityPositions.add(pos);
                        }
                    } else {
                        newChunkSection.setSkyLight(x, y, z, 15);
                    }
                }
            }
        }
        return newChunkSection;
    }

    public byte[] setBiomes(Chunk chunk, int chunkX, int chunkZ, Schematic schematic) {
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] schematicBiomes = schematic.getBiomes();
        byte[] biomes = chunk.m();

        int maxIndex = schematicWidth * schematicLength;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; ++z) {
                int index = getSchematicIndex(chunkX, chunkZ, x, z, schematicLength, schematicWidth);
                if (index != -1) {
                    if (index >= maxIndex) {
                        continue;
                    }
                    biomes[x + z * 16] = schematicBiomes[index];
                }
            }
        }

        return biomes;
    }

    public static int getSchematicIndex(int chunkX, int chunkZ, int x, int y, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - chunkX * 16) {
            return -1;
        }
        if (z >= schematicLength - chunkZ * 16) {
            return -1;
        }
        return (y * schematicWidth * schematicLength) + ((z + chunkZ * 16) * schematicWidth) + (x + chunkX * 16);
    }

    public static int getSchematicIndex(int chunkX, int chunkZ, int x, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - chunkX * 16) {
            return -1;
        }
        if (z >= schematicLength - chunkZ * 16) {
            return -1;
        }
        return ((z + chunkZ * 16) * schematicWidth) + (x + chunkX * 16);
    }

    public static boolean isTileEntityBlock(Block block) {
        return block instanceof BlockContainer;
    }
}