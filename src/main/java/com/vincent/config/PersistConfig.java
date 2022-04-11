package com.vincent.config;

import com.vincent.enums.VincentEvent;
import com.vincent.enums.VincentState;
import com.vincent.pojo.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.RepositoryStateMachinePersist;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.redis.RedisStateMachineContextRepository;
import org.springframework.statemachine.redis.RedisStateMachinePersister;

import javax.annotation.Resource;

/**
 * @author wang_cheng
 * @date 2022/04/11 14:24
 * @desc
 **/
@Configuration
public class PersistConfig {

    @Resource
    private InMemoryStateMachinePersist inMemoryStateMachinePersist;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private OrderStateMachinePersist orderStateMachinePersist;

    @Bean(name="orderPersister")
    public StateMachinePersister<VincentState, VincentEvent, Order> orderPersister() {
        return new DefaultStateMachinePersister<>(orderStateMachinePersist);
    }

    /**
     * 注入RedisStateMachinePersister对象【redis】
     *
     * @return
     */
    @Bean(name = "orderRedisPersister")
    public RedisStateMachinePersister<VincentState, VincentEvent> redisPersister() {
        return new RedisStateMachinePersister<>(redisPersist());
    }


    /**
     * 通过redisConnectionFactory创建StateMachinePersist
     *
     * @return
     */
    public StateMachinePersist<VincentState, VincentEvent, String> redisPersist() {
        RedisStateMachineContextRepository<VincentState, VincentEvent> repository =
                new RedisStateMachineContextRepository<>(redisConnectionFactory);
        return new RepositoryStateMachinePersist<>(repository);
    }


    /**
     * 注入StateMachinePersister对象[内存]
     *
     * @return
     */
    @Bean(name = "orderMemoryPersister")
    public StateMachinePersister<VincentState, VincentEvent, String> getPersister() {
        return new DefaultStateMachinePersister<>(inMemoryStateMachinePersist);

    }
}
