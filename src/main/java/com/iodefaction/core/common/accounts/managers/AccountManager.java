package com.iodefaction.core.common.accounts.managers;

import com.iodefaction.core.common.accounts.Account;
import com.iodefaction.core.common.entities.RedisCachedMongoEntityManager;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.redisson.api.RedissonClient;

public class AccountManager extends RedisCachedMongoEntityManager<Account> {

    @Getter
    private static AccountManager instance;

    public AccountManager(MongoCollection<Account> mongoCollection, RedissonClient redissonClient) {
        super(mongoCollection, redissonClient);

        instance = this;
    }

    @Override
    public Account newInstance(String key) {
        return new Account(key);
    }
}
