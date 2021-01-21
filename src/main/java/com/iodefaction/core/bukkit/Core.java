package com.iodefaction.core.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iodefaction.api.bukkit.common.plugins.Plugin;
import com.iodefaction.api.common.mongo.MongoConnection;
import com.iodefaction.api.common.mongo.MongoCredentials;
import com.iodefaction.api.common.redis.RedisConnection;
import com.iodefaction.api.common.redis.RedisCredentials;
import com.iodefaction.core.bukkit.listeners.AccountListener;
import com.iodefaction.core.bukkit.listeners.GroupMessageListener;
import com.iodefaction.core.bukkit.listeners.UpdateMessageListener;
import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.accounts.managers.AccountManager;
import com.iodefaction.core.common.permissions.PermissionGroup;
import com.iodefaction.core.common.permissions.PermissionGroupManager;
import com.iodefaction.core.common.prefix.PrefixGroup;
import com.iodefaction.core.common.prefix.PrefixGroupManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Core extends Plugin {

    private static File CREDENTIALS_FOLDER;

    @Getter
    private final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Getter
    private final Executor executor = Executors.newFixedThreadPool(10);

    @Override
    public void onPluginEnable() {
        CREDENTIALS_FOLDER = new File(getDataFolder(), "credentials");
        if(!CREDENTIALS_FOLDER.exists()) CREDENTIALS_FOLDER.mkdirs();

        registerConnections();

        this.getServer().getPluginManager().registerEvents(new AccountListener(this, AccountManager.getInstance()), this);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "groups", new GroupMessageListener());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "update", new UpdateMessageListener());
    }

    private void registerConnections() {
        RedisConnection redisConnection = getRedisConnection();
        MongoConnection mongoConnection = getMongoConnection();

        MongoDatabase mongoDatabase = mongoConnection.getMongoClient().getDatabase(mongoConnection.getDatabaseName())
                .withCodecRegistry(fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().register(Account.class, PrefixGroup.class, PermissionGroup.class).build())));

        new AccountManager(mongoDatabase.getCollection("accounts", Account.class), redisConnection.getRedissonClient());
        new PermissionGroupManager(mongoDatabase.getCollection("permission_groups", PermissionGroup.class)).load();
        new PrefixGroupManager(mongoDatabase.getCollection("prefix_groups", PrefixGroup.class)).load();
    }

    @SneakyThrows
    private MongoConnection getMongoConnection() {
        File mongoCredentials = new File(CREDENTIALS_FOLDER, "mongo.json");

        if(!mongoCredentials.exists()) {
            mongoCredentials.createNewFile();

            FileUtils.writeStringToFile(mongoCredentials, gson.toJson(new MongoCredentials("127.0.0.1", "gné", "", "", 1)), "UTF-8");

            throw new IllegalAccessException("Set your credentials in credentials/mongo.json.");
        }

        MongoCredentials credentials = gson.fromJson(FileUtils.readFileToString(mongoCredentials, "UTF-8"), MongoCredentials.class);

        return new MongoConnection(credentials);
    }

    @SneakyThrows
    private RedisConnection getRedisConnection() {
        File redisCredentials = new File(CREDENTIALS_FOLDER, "redis.json");

        if(!redisCredentials.exists()) {
            redisCredentials.createNewFile();

            FileUtils.writeStringToFile(redisCredentials, gson.toJson(new RedisCredentials("127.0.0.1", "gné", 6379, 1)), "UTF-8");

            throw new IllegalAccessException("Set your credentials in credentials/redis.json.");
        }

        RedisCredentials credentials = gson.fromJson(FileUtils.readFileToString(redisCredentials, "UTF-8"), RedisCredentials.class);

        return new RedisConnection(credentials);
    }

    @Override
    public void onPluginDisable() {
        PermissionGroupManager.getInstance().save();
        PrefixGroupManager.getInstance().save();
    }
}
