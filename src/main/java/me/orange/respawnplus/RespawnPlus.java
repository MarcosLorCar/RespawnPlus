package me.orange.respawnplus;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static net.kyori.adventure.title.Title.Times;
import static net.kyori.adventure.title.Title.title;

public final class RespawnPlus extends JavaPlugin implements Listener {
    ArrayList<UUID> canRespawnSpectators = new ArrayList<>();
    private final HashMap<UUID, PlayerDeathData> deadSpectators = new HashMap<>();
    private BukkitScheduler scheduler;
    private Respawncd respawncd;
    private FileConfiguration configuration;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        scheduler = getServer().getScheduler();

        respawncd = new Respawncd();
        getServer().getCommandMap().register(getName(), respawncd);

        getServer().getCommandMap().register(getName(),
                new CheckScoreboardCommand(this));

        //config
        saveDefaultConfig();
        configuration = getConfig();
    }

    @EventHandler
    public void playerDied(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = player.getKiller();

        if (killer == null || killer.getName().equals(player.getName())) {
            showScoreboard();
            return;
        }

        Component deathMessage = event.deathMessage();
        for (Player onlinePlayer : getServer().getOnlinePlayers()) {
            if (deathMessage != null) onlinePlayer.sendMessage(deathMessage);
        }

        PlayerDeathData playerDeathData = new PlayerDeathData(player, killer, player.getLastDeathLocation());
        player.setGameMode(GameMode.SPECTATOR);
        player.setSpectatorTarget(killer);
        deadSpectators.put(player.getUniqueId(), playerDeathData);
        int cd = respawncd.getCooldown();
        scheduler.runTaskLater(this, () -> respawn(player.getUniqueId()), cd);
        int cdSecs = cd / 20;
        for (int i = 0; i < cdSecs; i++) {
            int currI = i;
            scheduler.runTaskLater(this,
                    () -> player.showTitle(title(Component.empty(),
                            Component.text(cdSecs - currI).color(NamedTextColor.GRAY))),
                    i * 20);
        }

        event.setCancelled(true);
    }

    private void showScoreboard() {
        Scoreboard mainScoreboard = getServer().getScoreboardManager().getMainScoreboard();
        Objective objective = mainScoreboard.getObjective(configuration.getString("deaths_scoreboard", "deaths"));
        if (objective == null) return;
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scheduler.runTaskLater(this, () -> mainScoreboard.clearSlot(DisplaySlot.SIDEBAR), configuration.getLong("scoreboard_time", 600L));
    }

    private void respawn(UUID uniqueId) {
        Server server = getServer();
        Player player = server.getPlayer(uniqueId);
        if (player != null) {
            player.showTitle(title(Component.text(""),
                    Component.text("Press ")
                            .color(NamedTextColor.GRAY)
                            .append(Component.keybind("key.sneak")
                                    .append(Component.text(" to respawn"))),
                    Times.times(Duration.ZERO,
                            Duration.ofMillis(500),
                            Duration.ofSeconds(1))));
            deadSpectators.remove(uniqueId);
            canRespawnSpectators.add(uniqueId);
            player.playSound(player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,
                    SoundCategory.RECORDS,
                    1,
                    2);
        }
    }

    @EventHandler
    public void stopSpectators(PlayerStopSpectatingEntityEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();
        if (deadSpectators.containsKey(id)) {
            event.setCancelled(true);
        } else if (canRespawnSpectators.contains(id)) {
            player.setGameMode(GameMode.SURVIVAL);
            canRespawnSpectators.remove(id);
        }
    }
}
