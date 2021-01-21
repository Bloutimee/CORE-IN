package com.iodefaction.core.bungee.commands;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import com.iodefaction.core.common.prefix.PrefixGroup;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.StringUtils;

public class PrefixGroupCommand extends Command {

    @Getter
    private final PrefixGroupManager prefixGroupManager;

    public PrefixGroupCommand(PrefixGroupManager prefixGroupManager) {
        super("prefixgroup");

        this.prefixGroupManager = prefixGroupManager;
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
            sender.sendMessage(Color.SECONDARY + "Groupes de préfixe");
            sender.sendMessage(" ");
            sender.sendMessage(Color.SECONDARY + "/prefixgroup create <nom>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Créer un groupe de permission");
            sender.sendMessage(Color.SECONDARY + "/prefixgroup delete <nom>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Supprimer un groupe de permission");
            sender.sendMessage(Color.SECONDARY + "/prefixgroup setprefix <nom> <permission>" +
                    Color.DARK_COLOR + ": " +
                    Color.PRIMARY + "Mettre à jour le préfixe d'un groupe.");
            sender.sendMessage(Color.SEPARATOR);
            return;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(args.length != 2) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /prefixgroup create <nom>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Créer un groupe de permission");

                return;
            }

            String name = args[1];

            if(prefixGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe existe déjà !");
                return;
            }

            prefixGroupManager.create(name);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Le groupe a bien été créé !");
            refreshGroups();
        }

        if(args[0].equalsIgnoreCase("delete")) {
            if(args.length != 2) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /prefixgroup delete <nom>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Supprimer un groupe de permission");

                return;
            }

            String name = args[1];

            if(!prefixGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            prefixGroupManager.delete(name);
            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Le groupe a bien été supprimé !");
            refreshGroups();
        }

        if(args[0].equalsIgnoreCase("setprefix")) {
            if(args.length < 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /prefixgroup setprefix <nom> <préfixe>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Mettre un préfixe à un groupe");

                return;
            }

            String name = args[1];

            String prefix = ChatColor.translateAlternateColorCodes('&', StringUtils.join(args, " ").substring(10 + name.length() + 1));

            if(!prefixGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PrefixGroup prefixGroup = prefixGroupManager.getByKey(name);

            prefixGroup.setPrefix(prefix);

            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien mis à jour le préfixe à \"" + prefix + Color.SUCCESS + "\" au groupe " + prefixGroup.getKey());

            prefixGroupManager.send(prefixGroup);
            refreshGroups();
            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }

        if(args[0].equalsIgnoreCase("setorder")) {
            if(args.length < 3) {
                sender.sendMessage(Color.getPrefix("AVERTISSEMENT", Color.PRIMARY) + "La commande doit être utilisée comme ceci : /prefixgroup setorder <nom> <nbre>" +
                        Color.DARK_COLOR + ": " +
                        Color.PRIMARY + "Modifier l'ordre d'un groupe");

                return;
            }

            String name = args[1], order = args[2];

            if(!StringUtils.isNumeric(order)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "L'argument n'est pas numérique !");
                return;
            }

            if(!prefixGroupManager.has(name)) {
                sender.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Ce groupe n'existe pas !");
                return;
            }

            PrefixGroup prefixGroup = prefixGroupManager.getByKey(name);

            prefixGroup.setOrder(Integer.parseInt(order));

            sender.sendMessage(Color.getPrefix("Succès", Color.SUCCESS) + "Vous avez bien mis à jour l'ordre à \"" + order + Color.SUCCESS + "\" au groupe " + prefixGroup.getKey());

            prefixGroupManager.send(prefixGroup);

            refreshGroups();

            sender.sendMessage(Color.getPrefix("INFO", Color.SECONDARY) + "La modification a été envoyée à tous les serveurs !");
        }
    }

    private void refreshGroups() {
        ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> serverInfo.sendData("groups", new byte[0]));
    }
}
