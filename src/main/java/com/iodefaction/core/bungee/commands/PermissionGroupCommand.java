package com.iodefaction.core.bungee.commands;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import com.iodefaction.core.common.permissions.PermissionGroup;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PermissionGroupCommand extends Command {

    @Getter
    private final PermissionGroupManager permissionGroupManager;

    public PermissionGroupCommand(PermissionGroupManager permissionGroupManager) {
        super("permissiongroup");

        this.permissionGroupManager = permissionGroupManager;
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
            sender.sendMessage(Color.SECONDARY + "Groupes de permissions");
            sender.sendMessage(" ");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup create <nom>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Créer un groupe de permission");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup delete <nom>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer un groupe de permission");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup addperm <nom> <permission>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Ajouter une permission à un groupe.");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup delperm <nom> <permission>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer une permission à un groupe.");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup addinheritance <nom> <inheritance>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Ajouter une inheritance à un groupe.");
            sender.sendMessage(Color.SECONDARY + "/permissiongroup delinheritance <nom> <inheritance>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer une inheritance à un groupe.");
            sender.sendMessage(Color.SEPARATOR);
            return;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(args.length != 2) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup create <nom>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Créer un groupe de permission");

                return;
            }

            String name = args[1];

            if(this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe existe déjà !");
                return;
            }

            this.permissionGroupManager.create(name);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Le groupe a bien été créé !");
        }

        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length != 2) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup delete <nom>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer un groupe de permission");

                return;
            }

            String name = args[1];

            if(!this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            this.permissionGroupManager.delete(name);
            this.refreshGroups();
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Le groupe a bien été supprimé !");
        }

        if(args[0].equalsIgnoreCase("addperm")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup addperm <nom> <permission>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Ajouter une permission à un groupe");

                return;
            }

            String name = args[1],
                    permission = args[2];

            if(!this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PermissionGroup permissionGroup = this.permissionGroupManager.getByKey(name);

            if(permissionGroup.getRankPermissions().contains(permission.toLowerCase())) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe a déjà cette permission !");
                return;
            }

            permissionGroup.getRankPermissions().add(permission.toLowerCase());
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien ajouté la permission \"" + permission.toLowerCase() + "\" au groupe " + permissionGroup.getKey());

            this.permissionGroupManager.send(permissionGroup);
            this.refreshGroups();
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }

        if(args[0].equalsIgnoreCase("addinheritance")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup addinheritance <nom> <inheritance>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Ajouter une permission à un groupe");

                return;
            }

            String name = args[1],
                    inheritance = args[2];

            if(!this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PermissionGroup permissionGroup = this.permissionGroupManager.getByKey(name);

            if(permissionGroup.getInheritance().contains(inheritance)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe a déjà cette inheritance !");
                return;
            }

            permissionGroup.getInheritance().add(inheritance);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien ajouté l'inheritance \"" + inheritance + "\" au groupe " + permissionGroup.getKey());

            this.permissionGroupManager.send(permissionGroup);
            this.refreshGroups();
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }

        if(args[0].equalsIgnoreCase("delperm")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup delperm <nom> <permission>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer une permission à un groupe");

                return;
            }

            String name = args[1],
                    permission = args[2];

            if(!this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PermissionGroup permissionGroup = this.permissionGroupManager.getByKey(name);

            if(!permissionGroup.getRankPermissions().contains(permission.toLowerCase())) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'a pas cette permission !");
                return;
            }

            permissionGroup.getRankPermissions().remove(permission.toLowerCase());
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien supprimé la permission \"" + permission.toLowerCase() + "\" au groupe " + permissionGroup.getKey());

            this.permissionGroupManager.send(permissionGroup);
            this.refreshGroups();
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }

        if(args[0].equalsIgnoreCase("delinheritance")) {
            if(args.length != 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /permissiongroup delinheritance <nom> <inheritance>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer une permission à un groupe");

                return;
            }

            String name = args[1],
                    inheritance = args[2];

            if(!this.permissionGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PermissionGroup permissionGroup = this.permissionGroupManager.getByKey(name);

            if(!permissionGroup.getInheritance().contains(inheritance)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'a pas cette inheritance !");
                return;
            }

            permissionGroup.getInheritance().remove(inheritance);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien supprimé l'inheritance \"" + inheritance + "\" au groupe " + permissionGroup.getKey());

            this.permissionGroupManager.send(permissionGroup);
            this.refreshGroups();
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }
    }

    private void refreshGroups() {
        ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> serverInfo.sendData("groups", new byte[0]));
    }
}
