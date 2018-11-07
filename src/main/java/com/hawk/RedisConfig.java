package com.hawk;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;

/**
 * Redis 缓存配置类
 *
 * @author wangshuguang
 * @since 2018/03/13
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 默认情况下，key 值存在问题，当方法入参相同时，key 值也相同，这样会造成不同的方法读取相同的缓存，从而造成异常
     * 修改后的 key 值为 className + methodName + 参数值列表，可以支持使用 @Cacheable 时不指定 Key
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 配置 CacheManager
     * @param redisTemplate
     * @return
     */
    @Bean
    @SuppressWarnings("unchecked")
    public CacheManager cacheManager(
            RedisTemplate<?, ?> redisTemplate) {
        // RedisCache 需要一个 RedisCacheWriter 来实现读写 Redis
        RedisCacheWriter writer = RedisCacheWriter.lockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        // SerializationPair 用于 Java 对象和 Redis 之间的序列化和反序列化
        // Spring Boot 默认采用 JdkSerializationRedisSerializer 的二进制数据序列化方式
        // 使用该方式，保存在 redis 中的值是人类无法阅读的乱码，并且该 Serializer 要求目标类必须实现 Serializable 接口
        // 本示例中，使用 StringRedisSerializer 来序列化和反序列化 redis 的 key 值
        RedisSerializationContext.SerializationPair keySerializationPair = RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer());
        // 使用 Jackson2JsonRedisSerializer 来序列化和反序列化 redis 的 value 值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisSerializationContext.SerializationPair valueSerializationPair = RedisSerializationContext.SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer);
        // 构造一个 RedisCache 的配置对象，设置缓存过期时间和 Key、Value 的序列化机制
        // 这里设置缓存过期时间为 7 天
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(7))
                .serializeKeysWith(keySerializationPair).serializeValuesWith(valueSerializationPair);
        return new RedisCacheManager(writer, config);
    }
}
