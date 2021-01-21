package com.iodefaction.core.common.entities;

import com.iodefaction.api.common.entities.Entity;
import com.iodefaction.api.common.entities.managers.EntityManager;

import java.util.Collection;

public interface CacheEntityManager<E extends Entity> extends EntityManager<E> {
    void cache(E e);
    boolean isCached(String key);
    void uncache(String key);
    E getInCache(String key);
    Collection<E> getCache();
}
