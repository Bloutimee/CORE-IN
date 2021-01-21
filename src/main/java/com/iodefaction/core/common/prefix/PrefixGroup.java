package com.iodefaction.core.common.prefix;

import com.iodefaction.api.common.entities.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonIgnore;

@NoArgsConstructor
@AllArgsConstructor
public class PrefixGroup implements Entity {
    @BsonIgnore
    private String name;

    @Getter @Setter
    private String prefix;

    @Getter @Setter
    private int order = 0;

    @Getter @Setter
    private boolean def;

    public PrefixGroup(String name) {
        this.name = name;
        this.setPrefix("");

        this.setDef(false);
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public void setKey(String key) {
        this.name = key;
    }
}
