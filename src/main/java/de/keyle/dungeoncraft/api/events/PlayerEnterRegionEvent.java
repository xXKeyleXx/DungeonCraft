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

package de.keyle.dungeoncraft.api.events;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.region.DungeonRegion;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEnterRegionEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Dungeon dungeon;
    private final DungeonCraftPlayer player;
    private final DungeonRegion region;
    private boolean isCancelled = false;

    public PlayerEnterRegionEvent(Dungeon dungeon, DungeonCraftPlayer player, DungeonRegion region) {
        this.dungeon = dungeon;
        this.player = player;
        this.region = region;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public DungeonCraftPlayer getPlayer() {
        return player;
    }

    public DungeonRegion getRegion() {
        return region;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}