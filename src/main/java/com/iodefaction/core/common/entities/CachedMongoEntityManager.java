package com.iodefaction.core.common.entities;

import com.iodefaction.api.common.entities.Entity;
import com.mongodb.client.MongoCollection;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CachedMongoEntityManager<E extends Entity>
        extends MongoEntityManager<E> implements CacheEntityManager<E> {

    @Getter
    private final Map<String, E> entities;

    public CachedMongoEntityManager(MongoCollection<E> mongoCollection) {
        super(mongoCollection);

        this.entities = new ConcurrentHashMap<>();
    }

    @Override
    public boolean has(String key) {
        if(this.isCached(key)) return true;

        return super.has(key);
    }

    @Override
    public void cache(E e) {
        if(this.isCached(e.getKey())) return;

        this.entities.put(e.getKey(), e);
    }

    public void update(E e) {
        super.delete(e.getKey());
        this.getMongoCollection().insertOne(e);
    }

    @Override
    public boolean isCached(String key) {
        return this.entities.containsKey(key);
    }

    @Override
    public E getByKey(String key) {
        if(this.isCached(key)) return this.getInCache(key);

        return super.getByKey(key);
    }

    @Override
    public void uncache(String key) {
        this.entities.remove(key);
    }

    @Override
    public E create(String key) {
        if(!this.isCached(key)) {
            E e = this.newInstance(key);

            this.cache(e);
            return e;
        }

        return super.create(key);
    }

    @Override
    public void delete(String key) {
        if(this.isCached(key)) {
            this.uncache(key);
        }

        super.delete(key);
    }

    @Override
    public E getInCache(String key) {
        return this.entities.get(key);
    }

    @Override
    public Collection<E> getCache() {
        return this.entities.values();
    }

    public abstract E newInstance(String key);
}
