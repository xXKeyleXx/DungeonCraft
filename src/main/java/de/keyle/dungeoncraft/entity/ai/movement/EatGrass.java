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

package de.keyle.dungeoncraft.entity.ai.movement;

import de.keyle.dungeoncraft.api.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.types.sheep.EntityDungeonCraftSheep;
import net.minecraft.server.v1_7_R2.Block;
import net.minecraft.server.v1_7_R2.Blocks;
import net.minecraft.server.v1_7_R2.MathHelper;
import net.minecraft.server.v1_7_R2.World;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R2.event.CraftEventFactory;

public class EatGrass extends AIGoal {
    private EntityDungeonCraftSheep entityMySheep;
    private World world;
    private double chanceToEat;
    int eatTicks = 0;

    public EatGrass(EntityDungeonCraftSheep entityMySheep, double chanceToEat) {
        this.entityMySheep = entityMySheep;
        this.chanceToEat = chanceToEat;
        this.world = entityMySheep.world;
    }

    @Override
    public boolean shouldStart() {
        if (!this.entityMySheep.isSheared) {
            return false;
        } else if (entityMySheep.getRandom().nextDouble() > chanceToEat / 100.) {
            return false;
        } else if (this.entityMySheep.getGoalTarget() != null && this.entityMySheep.getGoalTarget().isAlive()) {
            return false;
        }
        int blockLocX = MathHelper.floor(this.entityMySheep.locX);
        int blockLocY = MathHelper.floor(this.entityMySheep.locY);
        int blockLocZ = MathHelper.floor(this.entityMySheep.locZ);

        return this.world.getType(blockLocX, blockLocY, blockLocZ) == Blocks.LONG_GRASS || this.world.getType(blockLocX, blockLocY - 1, blockLocZ) == Blocks.GRASS;
    }

    @Override
    public boolean shouldFinish() {
        return this.eatTicks <= 0;
    }

    @Override
    public void start() {
        this.eatTicks = 40;
        this.world.broadcastEntityEffect(this.entityMySheep, (byte) 10);
        this.entityMySheep.petNavigation.stop();
    }

    @Override
    public void finish() {
        this.eatTicks = 0;
    }

    @Override
    public void tick() {
        this.eatTicks--;
        if (this.eatTicks == 4) {
            int blockLocX = MathHelper.floor(this.entityMySheep.locX);
            int blockLocY = MathHelper.floor(this.entityMySheep.locY);
            int blockLocZ = MathHelper.floor(this.entityMySheep.locZ);

            if (this.world.getType(blockLocX, blockLocY, blockLocZ) == Blocks.LONG_GRASS) {
                if (!CraftEventFactory.callEntityChangeBlockEvent(this.entityMySheep.getBukkitEntity(), this.entityMySheep.world.getWorld().getBlockAt(blockLocX, blockLocY, blockLocZ), Material.AIR).isCancelled()) {
                    this.world.triggerEffect(2001, blockLocX, blockLocY, blockLocZ, Block.b(Blocks.LONG_GRASS) + 4096);
                    this.world.setAir(blockLocX, blockLocY, blockLocZ);
                    this.entityMySheep.setSheared(false);
                }
            } else if (this.world.getType(blockLocX, blockLocY - 1, blockLocZ) == Blocks.GRASS) {
                if (!CraftEventFactory.callEntityChangeBlockEvent(this.entityMySheep.getBukkitEntity(), this.entityMySheep.world.getWorld().getBlockAt(blockLocX, blockLocY - 1, blockLocZ), Material.DIRT).isCancelled()) {
                    this.world.triggerEffect(2001, blockLocX, blockLocY - 1, blockLocZ, Block.b(Blocks.GRASS));
                    this.world.setTypeAndData(blockLocX, blockLocY - 1, blockLocZ, Blocks.DIRT, 0, 2);
                    this.entityMySheep.setSheared(false);
                }
            }
        }
    }
}