package com.iodefaction.core.bukkit.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.iodefaction.core.bukkit.events.AccountReloadEvent;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class UpdateMessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(bytes);

        String s1 = dataInput.readUTF();

        Player p = Bukkit.getPlayer(s1);

        if(p == null) return;

        AccountReloadEvent event = new AccountReloadEvent(player, AccountManager.getInstance().getByKey(p.getName()));

        Bukkit.getServer().getPluginManager().callEvent(event);
    }
}
