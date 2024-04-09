package me.orange.respawnplus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Respawncd extends Command {
    private int cooldown = 100;

    Respawncd() {
        super("respawncd",
                "Change respawn timer in ticks",
                "/respawncd <ticks>",
                new ArrayList<>());
        setPermission("respawnplus.respawncd");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        try {
            cooldown = parseInt(args[0]);
            sender.sendMessage("Set the respawn cooldown to " + cooldown + " ticks");
            return true;
        } catch (Exception e) {
            sender.sendMessage(Component.text(getUsage()).color(NamedTextColor.RED));
            return false;
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return List.of("40", "60", "80", "100");
    }

    public int getCooldown() {
        return cooldown;
    }
}