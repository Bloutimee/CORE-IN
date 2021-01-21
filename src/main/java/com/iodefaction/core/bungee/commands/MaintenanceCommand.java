package com.iodefaction.core.bungee.commands;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.Core;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {
    @Getter
    private final Core core;

    public MaintenanceCommand(Core core) {
        super("maintenance");

        this.core = core;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

            Account account = AccountManager.getInstance().getByKey(proxiedPlayer.getName());

            if(!account.hasPermission("maintenance.manage")) {
                proxiedPlayer.sendMessage(Color.getPrefix("Erreur", Color.FAILED) + "Vous n'avez pas la permission !");
                return;
            }
        }

        core.setInMaintenance(!core.isInMaintenance());
        sender.sendMessage(Color.PRIMARY + "Le mode maintenance est désormais " + (core.isInMaintenance() ? Color.SUCCESS + "activé" : Color.FAILED + "désactivé") + Color.PRIMARY + " !");
    }
}
