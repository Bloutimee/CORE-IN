package com.iodefaction.core.common.entities;

import com.google.common.collect.Lists;
import com.iodefaction.api.common.entities.Entity;
import com.iodefaction.api.common.entities.managers.EntityManager;
import com.mongodb.client.MongoCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

import static com.mongodb.client.model.Filters.eq;

@AllArgsConstructor
public abstract class MongoEntityManager<E extends Entity> implements EntityManager<E> {

    @Getter
    private final MongoCollection<E> mongoCollection;

    @Override
    public E getByKey(String key) {
        if(!has(key)) return null;

        return mongoCollection.find(eq("key", key)).first();
    }

    @Override
    public Collection<E> getAll() {
        return Lists.newArrayList(mongoCollection.find());
    }

    @Override
    public void send(E e) {
        delete(e.getKey());

        mongoCollection.insertOne(e);
    }

    @Override
    public void delete(String key) {
        mongoCollection.deleteOne(eq("key", key));
    }

    @Override
    public E create(String key) {
        E e = newInstance(key);

        mongoCollection.insertOne(e);

        return e;
    }

    @Override
    public boolean has(String key) {
        return mongoCollection.count(eq("key", key)) >= 1;
    }

    public abstract E newInstance(String key);
}
