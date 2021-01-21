package com.iodefaction.core.bukkit.scoreboard;

import com.iodefaction.api.bukkit.common.plugins.Plugin;
import org.bukkit.Bukkit;

public class RefreshedScoreboardModule extends ScoreboardModule {
    public RefreshedScoreboardModule(ScoreboardManager scoreboardManager, Plugin plugin) {
        super(scoreboardManager, plugin);
    }

    @Override
    public void onModuleEnable() {
        super.onModuleEnable();

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), () -> this.getScoreboardManager().getBoards().forEach((s, fastBoard) -> this.getScoreboardManager().updateBoard(fastBoard)), 2, 2);
    }
}
