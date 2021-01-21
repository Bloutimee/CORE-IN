package com.iodefaction.core.common.entities;

import com.iodefaction.api.common.entities.Entity;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class RedisCachedMongoEntityManager<E extends Entity>
        extends MongoEntityManager<E> implements CacheEntityManager<E> {
    @Getter
    private final RedissonClient redissonClient;

    @Setter @Getter
    private String keyPattern;

    public RedisCachedMongoEntityManager(MongoCollection<E> mongoCollection, RedissonClient redissonClient) {
        super(mongoCollection);

        this.setKeyPattern("entity:");
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean has(String key) {
        return this.isCached(key) || super.has(key);
    }

    public void update(E e) {
        super.delete(e.getKey());
        this.getMongoCollection().insertOne(e);
    }

    @Override
    public void delete(String key) {
        if(this.isCached(key)) {
            this.uncache(key);
            return;
        }

        super.delete(key);
    }

    @Override
    public void send(E e) {
        if(this.isCached(e.getKey())) {
            this.saveInCache(e);
            return;
        }

        super.send(e);
    }

    @Override
    public E getByKey(String key) {
        if(this.isCached(key)) {
            return this.getInCache(key);
        }

        return super.getByKey(key);
    }

    @Override
    public E create(String key) {
        if(this.has(key)) return this.getByKey(key);

        E e = this.newInstance(key);
        this.cache(e);

        return e;
    }

    @Override
    public Collection<E> getAll() {
        return super.getAll();
    }

    @Override
    public Collection<E> getCache() {
        List<E> list = new ArrayList<>();
        return list;
    }

    @Override
    public void cache(E e) {
        if(this.isCached(e.getKey())) return;

        RBucket<E> rBucket = this.redissonClient.getBucket(this.getKeyPattern() + e.getKey().toLowerCase());
        rBucket.set(e);
    }

    @Override
    public boolean isCached(String key) {
        RBucket<E> rBucket = this.redissonClient.getBucket(this.getKeyPattern() + key.toLowerCase());

        if(rBucket == null) return false;

        return rBucket.isExists();
    }

    @Override
    public void uncache(String key) {
        if(!this.isCached(key)) return;

        RBucket<E> rBucket = this.redissonClient.getBucket(this.getKeyPattern() + key.toLowerCase());
        rBucket.delete();
    }

    @Override
    public E getInCache(String key) {
        RBucket<E> rBucket = this.redissonClient.getBucket(this.getKeyPattern() + key.toLowerCase());

        return rBucket.get();
    }

    public void saveInCache(E e) {
        this.uncache(e.getKey());

        this.cache(e);
    }

    public abstract E newInstance(String key);
}
