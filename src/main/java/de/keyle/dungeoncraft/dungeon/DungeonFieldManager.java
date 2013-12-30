/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2011-2013 Keyle & xXLupoXx
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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.util.schematic.Schematic;

import java.util.HashMap;
import java.util.Map;

public class DungeonFieldManager {
    private static Map<DungeonField, Schematic> fieldSchematicMap = new HashMap<DungeonField, Schematic>();

    public static DungeonField getNewDungeonField() {
        return new DungeonField(0, 0);
    }

    public static void assignSchematicToDungeonField(DungeonField field, Schematic schematic) {
        fieldSchematicMap.put(field, schematic);
    }

    public static void dissociateSchematicFromDungeonField(DungeonField field) {
        fieldSchematicMap.remove(field);
    }

    public static Schematic getSchematicForChunk(int chunkX, int chunkZ) {
        DungeonField field = getDungeonFieldForChunk(chunkX, chunkZ);

        return fieldSchematicMap.get(field);
    }

    public static Schematic getSchematicForDungeonField(DungeonField field) {
        return fieldSchematicMap.get(field);
    }

    public static DungeonField getDungeonFieldForChunk(int chunkX, int chunkZ) {
        return new DungeonField((int) Math.floor(chunkX / 100.), (int) Math.floor(chunkZ / 100.));
    }
}