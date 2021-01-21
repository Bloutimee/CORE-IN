package com.iodefaction.core.common.permissions;

import com.iodefaction.core.common.entities.CachedMongoEntityManager;
import com.mongodb.client.MongoCollection;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PermissionGroupManager extends CachedMongoEntityManager<PermissionGroup> {

    @Getter
    private static PermissionGroupManager instance;

    public PermissionGroupManager(MongoCollection<PermissionGroup> mongoCollection) {
        super(mongoCollection);

        instance = this;
    }

    public void load() {
        Collection<PermissionGroup> all = this.getAll();

        Map<String, PermissionGroup> groups = new HashMap<>();

        all.forEach(permissionGroup -> groups.put(permissionGroup.getKey(), permissionGroup));

        this.getEntities().clear();
        this.getEntities().putAll(groups);

        if(this.getDefault() == null) {
            PermissionGroup permissionGroup = this.newInstance("Joueur");
            permissionGroup.setDef(true);
            this.getEntities().put("Joueur", permissionGroup);
            this.send(permissionGroup);
        }
    }

    public void save() {
        this.getCache().forEach(this::send);
    }

    @Override
    public PermissionGroup newInstance(String key) {
        return new PermissionGroup(key);
    }

    public PermissionGroup getDefault() {
        return this.getEntities().values().stream().filter(PermissionGroup::isDef).findFirst().orElse(null);
    }
}
