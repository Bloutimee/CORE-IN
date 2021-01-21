package com.iodefaction.core.bukkit.listeners;

import com.iodefaction.api.bukkit.colors.Color;
import com.iodefaction.core.bukkit.Core;
import com.iodefaction.core.bukkit.events.AccountJoinEvent;
import com.iodefaction.core.bukkit.events.AccountReloadEvent;
import com.iodefaction.core.bukkit.utils.AccountUtils;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class AccountListener implements Listener {

    @Getter
    private final Core core;

    @Getter
    private final AccountManager accountManager;

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.core.getExecutor().execute(() -> {
            if(this.accountManager == null) {
                event.getPlayer().kickPlayer("" + Color.FAILED + "[N°1] Veuillez vous reconnecter ultérieurement !");
                return;
            }

            Account account;

            if(!this.accountManager.isCached(player.getName())) {
                /*
                 * N°0: Erreur de connexion avec la base de données côté Proxy
                 * N°1: Erreur de connexion avec la base de données côté Serveur
                 * N°2: Erreur côté Proxy, il n'arrive pas à récupérer une instance de Account.
                 * N°3: Erreur côté Serveur, il n'arrive pas à récupérer une instance de Account non plus.
                 * */

                MinecraftServer.getServer().postToMainThread(() -> event.getPlayer().kickPlayer("" + Color.FAILED + "[N°3] Veuillez vous reconnecter ultérieurement !"));
                return;
            }

            account = this.accountManager.getByKey(player.getName());

            if(account == null) {
                MinecraftServer.getServer().postToMainThread(() -> event.getPlayer().kickPlayer("" + Color.FAILED + "[N°3] Veuillez vous reconnecter ultérieurement !"));
                return;
            }

            AccountUtils.updatePermissions(this.core, account);
            AccountJoinEvent accountJoinEvent = new AccountJoinEvent(event.getPlayer(), account, "");

            Bukkit.getServer().getPluginManager().callEvent(accountJoinEvent);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AccountUtils.removePermissions(event.getPlayer());
    }

    @EventHandler
    public void onAccountReload(AccountReloadEvent event) {
        this.core.getExecutor().execute(() -> {
            AccountUtils.updatePermissions(this.core, event.getAccount());
        });
    }
}
