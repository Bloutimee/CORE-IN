package com.iodefaction.core.bungee.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.utils.TabUtils;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class AccountCommand extends Command {

    @Getter
    private final AccountManager accountManager;

    public AccountCommand(AccountManager accountManager) {
        super("account");

        this.accountManager = accountManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

            Account account = AccountManager.getInstance().getByKey(proxiedPlayer.getName());

            if(!account.hasPermission("groups.manage")) {
                proxiedPlayer.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Vous n'avez pas la permission !");
                return;
            }
        }

        if(args.length == 0) {
            sender.sendMessage(Color.SEPARATOR);
            sender.sendMessage(Color.SECONDARY + "Comptes");
            sender.sendMessage(" ");
            sender.sendMessage(Color.SECONDARY + "/account delete <nom>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer un compte");
            sender.sendMessage(Color.SECONDARY + "/account setprefix <nom> <rang>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Mettre à jour le rang de préfixe d'un compte.");
            sender.sendMessage(Color.SECONDARY + "/account setpermission <nom> <rang>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Mettre à jour le rang de permission d'un compte.");
            sender.sendMessage(Color.SECONDARY + "/account addperm <nom> <permission>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Ajouter une permission à un compte.");
            sender.sendMessage(Color.SECONDARY + "/account delperm <nom> <permission>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer une permission à un compte.");
            sender.sendMessage(Color.SEPARATOR);
            return;
        }

        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length != 2) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account delete <nom>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer un compte");
                return;
            }

            String name = args[1];

            if(!this.accountManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'existe pas !");
                return;
            }

            this.accountManager.delete(name);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Le compte a bien été supprimé !");
        }

        if(args[0].equalsIgnoreCase("setprefix")) {
            if(args.length < 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account setprefix <nom> <rang>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Mettre à jour le rang de préfixe d'un compte");

                return;
            }

            String name = args[1],
                    rank = args[2];

            if(!this.accountManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'existe pas !");
                return;
            }

            if(PrefixGroupManager.getInstance().getByKey(rank) == null) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            Account account = this.accountManager.getByKey(name);

            if(args.length == 4) {
                if(!this.isNumeric(args[3])) {
                    sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account setprefix <nom> <rang> [minutes]" +
                            Color.DARK_COLOR + ": " +
                            Color.PRIMARY + "Mettre à jour le rang de préfixe d'un compte");
                    return;
                }

                int minutes = Integer.parseInt(args[3]);
                long millis = TimeUnit.MINUTES.toMillis(minutes);

                account.createCooldown("prefixGroup", millis);
            }

            account.setPrefixGroupName(rank);

            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien mis le rang de préfixe \"" + rank + "\" au compte " + account.getKey());

            this.accountManager.send(account);
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");

            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if(proxiedPlayer != null) {
                TabUtils.refreshTab(proxiedPlayer, account);
                this.reloadUser(proxiedPlayer);
            }
        }

        if(args[0].equalsIgnoreCase("setpermission")) {
            if(args.length < 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account setpermission <nom> <rang> [minutes]" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Mettre à jour le rang de permission d'un compte");

                return;
            }

            String name = args[1],
                    rank = args[2];

            if(!this.accountManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'existe pas !");
                return;
            }

            if(PermissionGroupManager.getInstance().getByKey(rank) == null) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            Account account = this.accountManager.getByKey(name);

            if(args.length == 4) {
                if(!this.isNumeric(args[3])) {
                    sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account setprefix <nom> <rang>" +
                            Color.DARK_COLOR + ": " +
                            Color.PRIMARY + "Mettre à jour le rang de préfixe d'un compte");
                    return;
                }

                int minutes = Integer.parseInt(args[3]);
                long millis = TimeUnit.MINUTES.toMillis(minutes);

                account.createCooldown("permissionGroup", millis);
            }

            account.setPermissionGroupName(rank);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien mis le rang de permission \"" + rank + "\" au compte " + account.getKey());

            this.accountManager.send(account);
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");

            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if(proxiedPlayer != null) {
                this.reloadUser(proxiedPlayer);
            }
        }

        if(args[0].equalsIgnoreCase("addperm")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account addperm <nom> <permission>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Ajouter une permission à un compte");

                return;
            }

            String name = args[1],
                    permission = args[2];

            if(!this.accountManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'existe pas !");
                return;
            }

            Account account = this.accountManager.getByKey(name);

            if(account.getSpecificPermissions().contains(permission)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte a déjà cette permission !");
                return;
            }

            account.getSpecificPermissions().add(permission.toLowerCase());

            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien ajouté la permission \"" + permission.toLowerCase() + "\" au compte " + account.getKey());

            this.accountManager.send(account);
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");

            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if(proxiedPlayer != null) {
                this.reloadUser(proxiedPlayer);
            }
        }

        if(args[0].equalsIgnoreCase("delperm")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /account delperm <nom> <permission>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer une permission à un compte");

                return;
            }

            String name = args[1],
                    permission = args[2];

            if(!this.accountManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'existe pas !");
                return;
            }

            Account account = this.accountManager.getByKey(name);

            if(!account.getSpecificPermissions().contains(permission)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce compte n'a pas cette permission !");
                return;
            }

            account.getSpecificPermissions().remove(permission.toLowerCase());

            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien supprimé la permission \"" + permission.toLowerCase() + "\" au compte " + account.getKey());

            this.accountManager.send(account);
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");

            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(name);

            if(proxiedPlayer != null) {
                this.reloadUser(proxiedPlayer);
            }
        }
    }

    private boolean isNumeric(String arg) {
        try {
            Integer.parseInt(arg);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void reloadUser(ProxiedPlayer player) {
        Server server = player.getServer();

        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();

        dataOutput.writeUTF(player.getName());

        server.sendData("update", dataOutput.toByteArray());
    }
}
