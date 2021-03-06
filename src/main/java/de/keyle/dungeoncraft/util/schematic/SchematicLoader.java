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

package de.keyle.dungeoncraft.util.schematic;

import de.keyle.dungeoncraft.util.vector.BlockVector;
import de.keyle.dungeoncraft.util.vector.Vector;
import de.keyle.knbt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchematicLoader extends Thread {
    private ISchematicReveiver schematicReceiver;

    public SchematicLoader(ISchematicReveiver schematicReceiver) {
        this.schematicReceiver = schematicReceiver;
    }

    public void run() {
        Schematic schematic;
        try {
            schematic = loadSchematic(schematicReceiver.getSchematicFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        schematicReceiver.setSchematic(schematic);
    }

    public static Schematic loadSchematic(File file) throws IOException {

        if (!file.exists()) {
            throw new IllegalArgumentException("Schematic file not found");
        }

        FileInputStream stream = new FileInputStream(file);

        TagCompound schematicTag = TagStream.readTag(stream, true);
        if (schematicTag == null) {
            schematicTag = TagStream.readTag(stream, false);
            if (schematicTag == null) {
                throw new IllegalArgumentException("Can not read tags");
            }
        }

        if (!schematicTag.containsKeyAs("Blocks", TagByteArray.class)) {
            throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
        }

        short width = schematicTag.getAs("Width", TagShort.class).getShortData();
        short length = schematicTag.getAs("Length", TagShort.class).getShortData();
        short height = schematicTag.getAs("Height", TagShort.class).getShortData();

        String materials = schematicTag.getAs("Materials", TagString.class).getStringData();
        if (!materials.equals("Alpha")) {
            throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
        }

        byte[] blocks = schematicTag.getAs("Blocks", TagByteArray.class).getByteArrayData();
        byte[] blockData = schematicTag.getAs("Data", TagByteArray.class).getByteArrayData();
        byte[] biomeData;
        if (schematicTag.containsKeyAs("Biomes", TagByteArray.class)) {
            biomeData = schematicTag.getAs("Biomes", TagByteArray.class).getByteArrayData();
        } else {
            biomeData = new byte[length * width];
        }

        TagList tileEntitiesTag = schematicTag.getAs("TileEntities", TagList.class);
        TagList entitiesTag = schematicTag.getAs("Entities", TagList.class);

        List<TagBase> readOnlyList = tileEntitiesTag.getReadOnlyList();
        Map<BlockVector, TagCompound> tileEntities = new HashMap<BlockVector, TagCompound>();

        for (int i = 0; i < readOnlyList.size(); i++) {
            TagCompound tileEntity = tileEntitiesTag.getTagAs(i, TagCompound.class);

            if (!tileEntity.containsKeyAs("x", TagInt.class)) {
                continue;
            } else if (!tileEntity.containsKeyAs("y", TagInt.class)) {
                continue;
            } else if (!tileEntity.containsKeyAs("z", TagInt.class)) {
                continue;
            }
            int x = ((TagInt) tileEntity.get("x")).getIntData();
            int y = ((TagInt) tileEntity.get("y")).getIntData();
            int z = ((TagInt) tileEntity.get("z")).getIntData();

            BlockVector v = new BlockVector(x, y, z);
            tileEntities.put(v, tileEntity);
        }

        List<TagBase> readOnlyEntityList = entitiesTag.getReadOnlyList();
        Map<Vector, TagCompound> entities = new HashMap<Vector, TagCompound>();

        for (int i = 0; i < readOnlyEntityList.size(); i++) {
            TagCompound entity = entitiesTag.getTagAs(i, TagCompound.class);

            double x = 0;
            double y = 0;
            double z = 0;

            if (entity.containsKeyAs("TileX", TagInt.class)) {
                x = ((TagInt) entity.get("TileX")).getIntData();
            } else if (entity.containsKeyAs("TileY", TagInt.class)) {
                y = ((TagInt) entity.get("TileY")).getIntData();
            } else if (entity.containsKeyAs("TileZ", TagInt.class)) {
                z = ((TagInt) entity.get("TileZ")).getIntData();
            }
            if (entity.containsKeyAs("Pos", TagList.class)) {
                TagList posTag = entity.getAs("Pos", TagList.class);
                //List<TagBase> readOnlyPos = posTag.getReadOnlyList();
                x = posTag.getTagAs(0, TagDouble.class).getDoubleData();
                y = posTag.getTagAs(1, TagDouble.class).getDoubleData();
                z = posTag.getTagAs(2, TagDouble.class).getDoubleData();
            }

            Vector v = new Vector(x, y, z);

            entities.put(v, entity);
        }
        Schematic schematic = new Schematic(blocks, blockData, biomeData, width, length, height, tileEntities, entities);

        if (schematicTag.containsKeyAs("Light", TagCompound.class)) {
            TagCompound lightTag = schematicTag.getAs("Light", TagCompound.class);
            boolean lightVersionCorrect = false;
            if (lightTag.containsKeyAs("Version", TagInt.class)) {
                lightVersionCorrect = lightTag.getAs("Version", TagInt.class).getIntData() >= LightCalculator.VERSION;
            }
            if (lightVersionCorrect && lightTag.containsKeyAs("SkyLight", TagByteArray.class) && lightTag.containsKeyAs("BlockLight", TagByteArray.class)) {
                byte[] skyLight = lightTag.getAs("SkyLight", TagByteArray.class).getByteArrayData();
                byte[] blockLight = lightTag.getAs("BlockLight", TagByteArray.class).getByteArrayData();
                schematic.setLight(skyLight, blockLight);
            }
        }

        return schematic;
    }
}