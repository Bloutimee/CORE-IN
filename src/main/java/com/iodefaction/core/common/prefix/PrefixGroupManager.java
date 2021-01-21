package com.iodefaction.core.common.prefix;

import com.iodefaction.core.common.entities.CachedMongoEntityManager;
import com.mongodb.client.MongoCollection;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrefixGroupManager extends CachedMongoEntityManager<PrefixGroup> {

    @Getter
    private static PrefixGroupManager instance;

    public PrefixGroupManager(MongoCollection<PrefixGroup> mongoCollection) {
        super(mongoCollection);

        instance = this;
    }

    public void load() {
        Collection<PrefixGroup> all = this.getAll();

        Map<String, PrefixGroup> groups = new HashMap<>();

        all.forEach(permissionGroup -> groups.put(permissionGroup.getKey(), permissionGroup));

        this.getEntities().clear();
        this.getEntities().putAll(groups);

        if(this.getDefault() == null) {
            PrefixGroup prefixGroup = this.newInstance("Joueur");
            prefixGroup.setDef(true);
            this.getEntities().put("Joueur", prefixGroup);

            this.send(prefixGroup);
        }
    }

    public void save() {
        this.getCache().forEach(this::send);
    }

    @Override
    public PrefixGroup newInstance(String key) {
        return new PrefixGroup(key);
    }

    public PrefixGroup getDefault() {
        return this.getEntities().values().stream().filter(PrefixGroup::isDef).findFirst().orElse(null);
    }
}
