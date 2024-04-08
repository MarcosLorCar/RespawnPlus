package me.orange.respawnplus;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public record PlayerDeathData(Player player, Player killer, Location deathLocation) {
}
