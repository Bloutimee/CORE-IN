package com.iodefaction.core.bungee.commands;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.Core;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.StringUtils;

public class StaffCommand extends Command {

    @Getter
    private final Core core;

    public StaffCommand(Core core) {
        super("staff");

        this.core = core;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(this.core.getStaffMembers().contains(sender.getName())) {
            String message = StringUtils.join(args, " ");

            for (String staffMember : this.core.getStaffMembers()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(staffMember);
                if(player == null) continue;

                player.sendMessage(new TextComponent(Color.getPrefix("Staff", Color.PRIMARY) + Color.SECONDARY + sender.getName() + Color.DARK_COLOR + ": " + Color.PRIMARY + message));
            }
        }
    }
}
