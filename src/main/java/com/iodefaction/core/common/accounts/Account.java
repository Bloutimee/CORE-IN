package com.iodefaction.core.common.accounts;

import com.iodefaction.api.common.entities.Entity;
import com.iodefaction.core.common.permissions.PermissionGroup;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroup;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Entity {
    @BsonIgnore
    private String playerName;
    private String permissionGroupName, prefixGroupName;
    private Map<String, Boolean> settings;
    private Map<String, Long> cooldowns;
    private List<String> specificPermissions;

    public Account(String playerName) {
        this.playerName = playerName;

        this.specificPermissions = new ArrayList<>();
        this.settings = new HashMap<>();
        this.cooldowns = new HashMap<>();

        this.setPrefixGroupName("Joueur");
        this.setPermissionGroupName("Joueur");
    }

    @Override
    public String getKey() {
        return this.playerName;
    }

    @Override
    public void setKey(String key) {
        this.playerName = key;
    }

    @BsonIgnore
    public long getCooldown(String name) {
        return this.cooldowns.get(name.toLowerCase());
    }

    @BsonIgnore
    public boolean hasCooldown(String name) {
        return this.cooldowns.containsKey(name.toLowerCase());
    }

    @BsonIgnore
    public boolean isCooldownExpired(String name) {
        if(!this.hasCooldown(name)) return true;

        return this.getCooldown(name) <= currentTimeMillis();
    }

    @BsonIgnore
    public void createCooldown(String name, long millis) {
        if(this.hasCooldown(name)) {
            this.cooldowns.remove(name.toLowerCase());
        }

        this.cooldowns.put(name.toLowerCase(), currentTimeMillis() + millis);
    }

    @BsonIgnore
    public void removeCooldown(String name) {
        if(this.hasCooldown(name)) {
            this.cooldowns.remove(name.toLowerCase());
        }
    }

    @BsonIgnore
    public boolean hasPermission(String name) {
        boolean groupHas = false,
            accountHas = this.specificPermissions.contains(name.toLowerCase()) || this.specificPermissions.contains("*");

        if(this.getPermissionGroup() != null) {
            groupHas = this.getPermissionGroup().getPermissions().contains(name.toLowerCase()) || this.getPermissionGroup().getPermissions().contains("*");
        }

        return groupHas || accountHas;
    }

    @BsonIgnore
    public PermissionGroup getPermissionGroup() {
        PermissionGroupManager permissionGroupManager = PermissionGroupManager.getInstance();

        if(permissionGroupManager == null) return null;

        return permissionGroupManager.getByKey(this.permissionGroupName);
    }

    @BsonIgnore
    public PrefixGroup getPrefixGroup() {
        PrefixGroupManager prefixGroupManager = PrefixGroupManager.getInstance();

        if(prefixGroupManager == null) return null;

        return prefixGroupManager.getByKey(this.prefixGroupName);
    }

    @BsonIgnore
    private boolean getSetting(String name) {
        return this.settings.get(name.toLowerCase());
    }

    @BsonIgnore
    private boolean hasSetting(String name) {
        return this.settings.containsKey(name.toLowerCase());
    }

    @BsonIgnore
    public boolean isSettingEnabled(String name) {
        if(!this.hasSetting(name)) return false;

        return this.getSetting(name);
    }

    @BsonIgnore
    public void toggleSetting(String name) {
        boolean exValue = this.isSettingEnabled(name);

        if(this.hasSetting(name)) {
            this.settings.remove(name);
        }

        this.settings.put(name, !exValue);
    }
}
