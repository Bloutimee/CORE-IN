package com.iodefaction.core.bukkit.scoreboard;

import com.iodefaction.api.bukkit.common.modules.Module;
import com.iodefaction.api.bukkit.common.plugins.Plugin;
import com.iodefaction.core.bukkit.scoreboard.assemble.Assemble;
import com.iodefaction.core.bukkit.scoreboard.assemble.AssembleAdapter;
import lombok.Getter;

public class ScoreboardModule extends Module {

    @Getter
    private ScoreboardManager scoreboardManager;

    @Getter
    private Assemble assemble;

    public ScoreboardModule(ScoreboardManager scoreboardManager, Plugin plugin) {
        super("Scoreboard", plugin);

        this.scoreboardManager = scoreboardManager;
        this.scoreboardManager.setScoreboardModule(this);
    }

    public ScoreboardModule(AssembleAdapter assembleAdapter, Plugin plugin) {
        super("Scoreboard", plugin);

        this.assemble = new Assemble(plugin, assembleAdapter);
    }

    @Override
    public void onModuleEnable() {
        if(this.scoreboardManager != null) {
            this.registerListener(new ScoreboardListener(this.getScoreboardManager(), this));
            this.getPlugin().getServer().getOnlinePlayers().forEach(this.getScoreboardManager()::addBoard);
        } else if(this.assemble != null) {
            this.assemble.setup();
        }
    }

    @Override
    public void onModuleDisable() {
        if(this.scoreboardManager != null) {
            this.getPlugin().getServer().getOnlinePlayers().forEach(this.getScoreboardManager()::removeBoard);
        } else if(this.assemble != null) {
            this.assemble.cleanup();
        }
    }
}
