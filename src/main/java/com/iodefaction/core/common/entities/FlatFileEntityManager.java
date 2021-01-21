package com.iodefaction.core.common.entities;

import com.google.gson.Gson;
import com.iodefaction.api.common.entities.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class FlatFileEntityManager<T extends Entity> implements CacheEntityManager<T> {
    @Getter
    private final File folder;

    @Getter @Setter
    private Map<String, T> entities;

    public FlatFileEntityManager(File folder) {
        this.folder = folder;
        this.entities = new HashMap<>();
    }

    @Override
    public void cache(T t) {
        if(this.isCached(t.getKey())) return;

        this.entities.put(t.getKey().toLowerCase(), t);
    }

    @Override
    public boolean isCached(String s) {
        return this.entities.containsKey(s.toLowerCase());
    }

    @Override
    public void uncache(String s) {
        this.entities.remove(s.toLowerCase());
    }

    @Override
    public T getInCache(String s) {
        return this.entities.get(s.toLowerCase());
    }

    @Override
    public Collection<T> getCache() {
        return this.entities.values();
    }

    @Override
    public T getByKey(String s) {
        return this.getInCache(s);
    }

    @Override
    public Collection<T> getAll() {
        return this.getCache();
    }

    @Override
    public void send(T t) {
        if(this.isCached(t.getKey())) {
            this.uncache(t.getKey());
        }

        this.cache(t);
    }

    @Override
    public void delete(String s) {
        this.uncache(s);
    }

    @Override
    public T create(String s) {
        T t = this.newInstance(s);

        this.cache(t);

        return t;
    }

    @Override
    public boolean has(String s) {
        return this.isCached(s);
    }

    @SneakyThrows
    public void load(Gson gson) {
        if(this.folder == null) return;
        if(!this.folder.exists()) return;
        if(this.folder.isFile()) return;

        for (File file : this.folder.listFiles()) {
            T t = gson.fromJson(FileUtils.readFileToString(file, "UTF-8"), ((Class<T>) this.newInstance("").getClass()));

            this.cache(t);
        }
    }

    @SneakyThrows
    public void save(Gson gson) {
        if(this.folder == null) return;
        if(this.folder.exists() && this.folder.isFile()) return;

        if(!this.folder.exists()) {
            this.folder.mkdirs();
        }

        for (T value : this.entities.values()) {
            File tFile = new File(this.folder, value.getKey().toLowerCase() + ".json");

            if(!tFile.exists()) {
                tFile.createNewFile();
            }

            FileUtils.writeStringToFile(tFile, gson.toJson(value), "UTF-8");
        }
    }

    public abstract T newInstance(String s);
}
