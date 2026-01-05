package com.mvm.transaction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching //This enables spring can use cache
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        //Caffeine configuration
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)//Cache will have 5 min since creation
                .expireAfterAccess(10, TimeUnit.MINUTES) //Cache will have 10 min since last access
                .maximumSize(100)                       //Max size of users cached
                .recordStats()                          //To see statistics
        );
        //Names of caches we will use
        cacheManager.setCacheNames(Arrays.asList(
                "userExpenses",
                "userIncomes"
        ));
        return cacheManager;
    }
}
