package com.iodefaction.core.bukkit.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {
    private final ScoreboardManager scoreboardManager;
    private final ScoreboardModule scoreboardModule;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (this.scoreboardModule.isEnabled()) {
            Player player = event.getPlayer();
            if (player != null) {
                if (this.scoreboardManager != null) {
                    this.scoreboardManager.addBoard(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (this.scoreboardModule.isEnabled()) {
            Player player = event.getPlayer();
            if (player != null) {
                if (this.scoreboardManager != null) {
                    this.scoreboardManager.removeBoard(player);
                }
            }
        }
    }

    public ScoreboardListener(ScoreboardManager scoreboardManager, ScoreboardModule scoreboardModule) {
        this.scoreboardManager = scoreboardManager;
        this.scoreboardModule = scoreboardModule;
    }
}