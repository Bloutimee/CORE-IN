package com.iodefaction.core.bukkit.utils;

import com.iodefaction.core.bukkit.Core;
import com.iodefaction.core.common.accounts.Account;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountUtils {
    private static final Map<String, PermissionAttachment> ATTACHMENTS = new ConcurrentHashMap<>();

    public static void updatePermissions(Core core, Account account) {
        Player player = Bukkit.getPlayer(account.getPlayerName());

        if(player == null) return;

        List<String> permissions = new ArrayList<>();

        permissions.addAll(account.getPermissionGroup().getPermissions());
        permissions.addAll(account.getSpecificPermissions());

        PermissionAttachment permissionAttachment = player.addAttachment(core);

        removePermissions(player);

        for (String permission : permissions) {
            permissionAttachment.setPermission(permission, true);
        }

        ATTACHMENTS.put(player.getName().toLowerCase(), permissionAttachment);
    }

    public static void removePermissions(Player player) {
        if(player == null) return;

        if(ATTACHMENTS.containsKey(player.getName().toLowerCase())) {
            player.removeAttachment(ATTACHMENTS.get(player.getName().toLowerCase()));

            ATTACHMENTS.remove(player.getName().toLowerCase());
        }
    }
}
