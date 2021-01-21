package com.iodefaction.core.bukkit.scoreboard.assemble;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Getter
public class AssembleListener implements Listener {

	private Assemble assemble;

	public AssembleListener(Assemble assemble) {
		this.assemble = assemble;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
        this.getAssemble().getBoards().put(event.getPlayer().getUniqueId(), new AssembleBoard(event.getPlayer(), this.getAssemble()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
        this.getAssemble().getBoards().remove(event.getPlayer().getUniqueId());
		event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

}
