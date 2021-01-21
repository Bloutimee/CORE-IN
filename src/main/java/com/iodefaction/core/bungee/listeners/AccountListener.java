package com.iodefaction.core.bungee.listeners;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.Core;
import com.iodefaction.core.bungee.utils.TabUtils;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import com.iodefaction.core.common.permissions.PermissionGroup;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroup;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@AllArgsConstructor
public class AccountListener implements Listener {

    @Getter
    private final Core core;

    @Getter
    private final AccountManager accountManager;

    @EventHandler
    public void onLoginEvent(LoginEvent event) {
        String playerName = event.getConnection().getName();

        if(event.isCancelled()) return;

        if(this.accountManager == null) {
            event.setCancelled(true);
            event.setCancelReason("" + Color.FAILED + "[N°0] Veuillez vous reconnecter ultérieurement.");
            return;
        }

        event.registerIntent(this.core);

        this.core.getExecutor().execute(() -> {
            Account account;

            if(this.accountManager.has(playerName)) {
                account = this.accountManager.getByKey(playerName);

                this.accountManager.cache(account);
            } else {
                account = this.accountManager.create(playerName);
            }

            if(account == null) {
                /*
                 * N°0: Erreur de connexion avec la base de données côté Proxy
                 * N°1: Erreur de connexion avec la base de données côté Serveur
                 * N°2: Erreur côté Proxy, il n'arrive pas à récupérer une instance de Account.
                 * N°3: Erreur côté Serveur, il n'arrive pas à récupérer une instance de Account non plus.
                 * */

                event.setCancelled(true);
                event.setCancelReason("" + Color.FAILED + "[N°2] Veuillez vous reconnecter ultérieurement.");
                return;
            }

            if(account.hasPermission("staff.use")) {
                this.core.getStaffMembers().add(account.getPlayerName());
            }

            if(this.core.isInMaintenance() && !this.core.getStaffMembers().contains(account.getPlayerName())) {
                event.setCancelled(true);
                event.setCancelReason("" + Color.FAILED + "Le serveur est en maintenance ! Veuillez réessayer de vous connecter ultérieurement !");
            }

            if(account.hasCooldown("permissionGroup") && account.isCooldownExpired("permissionGroup")) {
                PermissionGroupManager permissionGroupManager = PermissionGroupManager.getInstance();

                if(permissionGroupManager != null) {
                    PermissionGroup permissionGroup = permissionGroupManager.getDefault();

                    if(permissionGroup != null) {
                        account.setPermissionGroupName(permissionGroup.getKey());
                        account.removeCooldown("permissionGroup");
                        this.accountManager.send(account);
                    }
                }
            }

            if(account.hasCooldown("prefixGroup") && account.isCooldownExpired("prefixGroup")) {
                PrefixGroupManager prefixGroupManager = PrefixGroupManager.getInstance();

                if(prefixGroupManager != null) {
                    PrefixGroup prefixGroup = prefixGroupManager.getDefault();

                    if(prefixGroup != null) {
                        account.setPrefixGroupName(prefixGroup.getKey());
                        account.removeCooldown("prefixGroup");

                        this.accountManager.send(account);
                    }
                }
            }

            event.completeIntent(this.core);
        });
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        Account a = this.accountManager.getByKey(proxiedPlayer.getName());
        TabUtils.refreshTab(proxiedPlayer, a);
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        this.core.getStaffMembers().remove(player.getName());

        this.core.getExecutor().execute(() -> {
            if(this.accountManager.isCached(player.getName())) {
                Account account = this.accountManager.getByKey(player.getName());

                if(account == null) return;

                this.accountManager.uncache(account.getKey());
                this.accountManager.send(account);
            }
        });
    }
}
