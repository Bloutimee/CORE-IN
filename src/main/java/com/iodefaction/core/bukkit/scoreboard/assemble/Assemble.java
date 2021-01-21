package com.iodefaction.core.bukkit.scoreboard.assemble;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter @Setter
public class Assemble {

	private JavaPlugin plugin;
	private AssembleAdapter adapter;
	private Map<UUID, AssembleBoard> boards;
	private AssembleThread thread;
	private AssembleListener listeners;
	private long ticks = 2;
	private boolean hook = false;
	private AssembleStyle assembleStyle = AssembleStyle.MODERN;
	private boolean debugMode = true;

	public Assemble(JavaPlugin plugin, AssembleAdapter adapter) {
		if (plugin == null) {
			throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
		}

		this.plugin = plugin;
		this.adapter = adapter;
		this.boards = new ConcurrentHashMap<>();
	}

	public void setup() {
		//Register Events
		this.listeners = new AssembleListener(this);
		this.plugin.getServer().getPluginManager().registerEvents(this.listeners, this.plugin);

		//Ensure that the thread has stopped running
		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}

		//Register new boards for existing online players
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.getBoards().putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
		}

		//Start Thread
		this.thread = new AssembleThread(this);
	}

	public void cleanup() {
		if (this.thread != null) {
			this.thread.stop();
			this.thread = null;
		}

		if (this.listeners != null) {
			HandlerList.unregisterAll(this.listeners);
			this.listeners = null;
		}

		for (UUID uuid : this.getBoards().keySet()) {
			Player player = Bukkit.getPlayer(uuid);

			if (player == null || !player.isOnline()) {
				continue;
			}

			this.getBoards().remove(uuid);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}

}