package com.iodefaction.core.bukkit.scoreboard;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class ScoreboardManager {
    private final Map<String, FastBoard> boards;

    @Setter
    private ScoreboardModule scoreboardModule;

    public ScoreboardManager() {
        this.boards = new ConcurrentHashMap<>();
    }

    public void addBoard(Player player) {
        if(!hasBoard(player)) {
            FastBoard fastBoard = new FastBoard(player);

            boards.put(player.getName(), fastBoard);
        }

        updateBoard(getBoard(player));
    }

    public FastBoard getBoard(Player player) {
        return boards.get(player.getName());
    }

    public boolean hasBoard(Player player) {
        return boards.containsKey(player.getName());
    }

    public void removeBoard(Player player) {
        if(!hasBoard(player)) return;

        getBoard(player).delete();
        boards.remove(player.getName());
    }

    public abstract void updateBoard(FastBoard fastBoard);
}
