package com.iodefaction.core.common.permissions;

import com.iodefaction.api.common.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class PermissionGroup implements Entity {
    @BsonIgnore
    private String name;

    @Setter
    private List<String> permissions;

    @Getter @Setter
    private List<String> inheritance;

    @Getter @Setter
    private boolean def;

    public PermissionGroup(String name) {
        this.name = name;
        this.setPermissions(new ArrayList<>());
        this.setInheritance(new ArrayList<>());

        this.setDef(false);
    }

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>(this.permissions);

        PermissionGroupManager groupManager = PermissionGroupManager.getInstance();

        for (String s : this.inheritance) {
            PermissionGroup byKey = groupManager.getByKey(s);

            if(byKey == null) continue;

            for (String permission : byKey.getPermissions()) {
                if(!permissions.contains(permission)) {
                    permissions.add(permission);
                }
            }
        }

        return permissions;
    }

    public List<String> getRankPermissions() {
        return this.permissions;
    }

    @Override
    public String getKey() {
        return this.name;
    }

    @Override
    public void setKey(String key) {
        this.name = key;
    }
}
