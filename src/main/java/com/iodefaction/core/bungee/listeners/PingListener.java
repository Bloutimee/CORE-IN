package com.iodefaction.core.bungee.listeners;

import com.iodefaction.api.bungee.colors.Color;
import com.iodefaction.core.bungee.Core;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.UUID;

public class PingListener implements Listener {

    private static final File FAVICON = new File("favicon.png");

    @Getter
    private final Core core;

    @Getter
    private Favicon favicon;

    @SneakyThrows
    public PingListener(Core core) {
        this.core = core;

        if(FAVICON.exists()) {
            this.favicon = Favicon.create(ImageIO.read(FAVICON));
        }
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) {
        event.getResponse().setDescription(Color.PRIMARY + "Bienvenue sur " + Color.SECONDARY + ChatColor.BOLD + "IodeNetwork" + Color.PRIMARY + " !\n" +
                "Nous vous souhaitons un " + Color.SECONDARY + "bon jeu " + Color.PRIMARY + "!");

        if(this.favicon != null) {
            event.getResponse().setFavicon(this.favicon);
        }

        if(event.getResponse().getVersion().getProtocol() < 47) {
            event.getResponse().getPlayers().setSample(new ServerPing.PlayerInfo[0]);
            event.getResponse().getVersion().setName(Color.FAILED + "Disponible à partir de la 1.8");
            event.getResponse().getVersion().setProtocol(2);
            return;
        }

        if(this.core.isInMaintenance()) {
            event.getResponse().setVersion(new ServerPing.Protocol(Color.FAILED + "En maintenance", 2));
        }

        event.getResponse().getPlayers().setSample(new ServerPing.PlayerInfo[]{
                new ServerPing.PlayerInfo(Color.DARK_COLOR + "§l§m" + StringUtils.repeat('-', 20), UUID.randomUUID()),
                new ServerPing.PlayerInfo(Color.SECONDARY + "IodeNetwork", UUID.randomUUID()),
                new ServerPing.PlayerInfo(Color.SECONDARY + "", UUID.randomUUID()),
                new ServerPing.PlayerInfo(this.core.isInMaintenance() ? Color.FAILED + "Nous revenons dès" : Color.PRIMARY + "Bienvenue !", UUID.randomUUID()),
                new ServerPing.PlayerInfo(this.core.isInMaintenance() ? Color.FAILED + "que possible." : Color.PRIMARY + "Rejoignez nos " + Color.SECONDARY + event.getResponse().getPlayers().getOnline() + Color.PRIMARY + " joueurs !", UUID.randomUUID()),
                new ServerPing.PlayerInfo(Color.DARK_COLOR + "§l§m" + StringUtils.repeat('-', 20), UUID.randomUUID()),
        });
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getTag().equals("groups")) {
            ProxyServer.getInstance().getServers().forEach((s, serverInfo) -> serverInfo.sendData("groups", new byte[0]));

            PrefixGroupManager.getInstance().load();
            PermissionGroupManager.getInstance().load();
        }
    }
}
