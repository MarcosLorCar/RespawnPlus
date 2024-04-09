package me.orange.respawnplus;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class CheckScoreboardCommand extends Command {
    private final JavaPlugin plugin;

    public CheckScoreboardCommand(JavaPlugin plugin) {
        super("checkscoreboard");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        FileConfiguration configuration = plugin.getConfig();
        Server server = plugin.getServer();
        Scoreboard mainScoreboard = server.getScoreboardManager().getMainScoreboard();
        Objective objective = mainScoreboard.getObjective(configuration.getString("deaths_scoreboard", "deaths"));
        if (objective == null) return false;
        if (mainScoreboard.getObjective(DisplaySlot.SIDEBAR) == objective) {
            mainScoreboard.clearSlot(DisplaySlot.SIDEBAR);
            return true;
        }
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        server.getScheduler().runTaskLater(plugin, () -> mainScoreboard.clearSlot(DisplaySlot.SIDEBAR), configuration.getLong("scoreboard_time"));
        return true;
    }
}
