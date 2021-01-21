package com.iodefaction.core.bungee.commands;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.Core;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.atomic.AtomicReference;

public class PingCommand extends Command {

    @Getter
    private final Core core;

    public PingCommand(Core core) {
        super("ping");

        this.core = core;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AtomicReference<ProxiedPlayer> reference = new AtomicReference<>();

        if(args.length == 1) {
            reference.set(core.getProxy().getPlayer(args[0]));
        } else {
            if(sender instanceof ProxiedPlayer) {
                reference.set((ProxiedPlayer) sender);
            }
        }

        if(reference.get() == null) {
            sender.sendMessage(Color.FAILED + "Ce joueur n'est pas connecté !");
            return;
        }

        ProxiedPlayer target = reference.get();

        sender.sendMessage(Color.getPrefix("PING", Color.PRIMARY) + target.getName() + " a actuellement " + Color.SECONDARY + target.getPing() + "ms" + Color.PRIMARY + " de latence !");
    }
}
