package com.iodefaction.core.bukkit.listeners;

import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class GroupMessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        PermissionGroupManager.getInstance().load();
        PrefixGroupManager.getInstance().load();

        Bukkit.getServer().getLogger().info("Groups reloaded!");
    }
}
