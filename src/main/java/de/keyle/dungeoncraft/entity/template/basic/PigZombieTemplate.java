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

package de.keyle.dungeoncraft.entity.template.basic;

import de.keyle.dungeoncraft.entity.template.BasicTemplate;
import de.keyle.dungeoncraft.entity.template.components.ArmorComponent;
import de.keyle.dungeoncraft.entity.template.components.EquipmentWeaponComponent;
import de.keyle.dungeoncraft.entity.template.components.MeleeDamageComponent;
import de.keyle.dungeoncraft.entity.types.EntityType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PigZombieTemplate extends BasicTemplate {
    public PigZombieTemplate() {
        super("pigzombie", 20, EntityType.PigZombie);
        addBasicComponent(new MeleeDamageComponent(9));
        addBasicComponent(new ArmorComponent(2));
        addBasicComponent(new EquipmentWeaponComponent(new ItemStack(Material.GOLD_SWORD)));
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }
}