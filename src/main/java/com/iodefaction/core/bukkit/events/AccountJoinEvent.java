package com.iodefaction.core.bukkit.events;

import com.iodefaction.core.common.accounts.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class AccountJoinEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Account account;

    @Setter
    private String joinMessage = "";

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
