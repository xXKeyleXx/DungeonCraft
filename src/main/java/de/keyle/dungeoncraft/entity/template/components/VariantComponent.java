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

package de.keyle.dungeoncraft.entity.template.components;

import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.api.entity.components.Parameter;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.horse.EntityDungeonCraftHorse;

public class VariantComponent extends EntityTemplateComponent {
    int variant = 0;

    public VariantComponent(@Parameter(type = Parameter.Type.Number, name = "variant") int variant) {
        this.variant = variant;
    }

    @Override
    public void applyComponent(EntityDungeonCraft entity) {
        if (entity instanceof EntityDungeonCraftHorse) {
            ((EntityDungeonCraftHorse) entity).setVariant(variant);
        }
    }
}