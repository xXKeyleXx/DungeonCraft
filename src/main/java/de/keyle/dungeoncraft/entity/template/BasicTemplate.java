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

package de.keyle.dungeoncraft.entity.template;

import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.entity.types.EntityType;

public abstract class BasicTemplate extends EntityTemplate {
    public BasicTemplate(String templateId, double maxHp, EntityType type) {
        super(templateId, type);
        super.setMaxHealth(maxHp);
    }

    public void setMaxHealth(double maxHp) {
    }

    public void setWalkSpeed(float walkSpeed) {
    }

    public void addComponent(EntityTemplateComponent comonent) {
    }

    protected void addBasicComponent(EntityTemplateComponent comonent) {
        super.addComponent(comonent);
    }
}