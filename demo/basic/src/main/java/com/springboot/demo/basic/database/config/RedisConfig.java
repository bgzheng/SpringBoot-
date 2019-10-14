package com.springboot.demo.basic.database.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

/**
 * Redis 配置类
 *
 * @author zhengbinggui
 * @version 2018/10/19
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 初始化缓存管理器,可以设置缓存的默认过期时间
     *
     * @return
     */
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory lettuceConnectionFactory) {
        LOGGER.info("init cacheManager begin...");
		/*// 生成一个默认配置，通过config对象即可对缓存进行自定义配置
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig ();
		// 设置缓存的默认过期时间，也是使用Duration设置
		// 不缓存空值
		config = config.entryTtl (Duration.ofMinutes (30)).disableCachingNullValues ();

		// 设置一个初始化的缓存空间set集合
		Set<String> cacheNames = new HashSet<> ();
		cacheNames.add ("smzeypt-redis-cache");

		// 对每个缓存空间应用不同的配置
		Map<String, RedisCacheConfiguration> configMap = new HashMap<> ();
		configMap.put ("smzeypt-redis-cache", config);

		// 使用自定义的缓存配置初始化一个cacheManager
		// 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
		RedisCacheManager cacheManager = RedisCacheManager.builder (factory).initialCacheNames (cacheNames)
				.withInitialCacheConfigurations (configMap).build ();
		return cacheManager;*/
        //关键点，spring cache的注解使用的序列化都从这来，没有这个配置的话使用的jdk自己的序列化，实际上不影响使用，只是打印出来不适合人眼识别
        //key序列化方式
        //value序列化方式
        //缓存过期时间
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues().entryTtl(Duration.ofDays(1));

        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(lettuceConnectionFactory).cacheDefaults(config).transactionAware();

        return builder.build();
    }

    /**
     * 设置自动key的生成规则，配置spring boot的注解，进行方法级别的缓存
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        LOGGER.info("init KeyGenerator begin...");
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(":");
            sb.append(method.getName());
            for (Object object : params) {
                if(null == object){
                    sb.append(":null");
                }else{
                    sb.append(":" + String.valueOf(object));
                }
            }
            String key = String.valueOf(sb);
            LOGGER.info(String.format("KeyGenerator key -> %s", key));
            return key;
        };
    }

    /**
     * 异常处理，当Redis发生异常时，打印日志，但是程序正常走
     *
     * @return
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        LOGGER.info("init CacheErrorHandler begin...");
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler () {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                LOGGER.error(String.format("Redis occur handleCacheGetError,key -> %s ,e -> %s", key, e));
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                LOGGER.error(
                        String.format("Redis occur handleCachePutError,key -> %s ,value -> %s ,e -> %s", key, value,
                                e));
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                LOGGER.error(String.format("Redis occur handleCacheEvictError,key -> %s ,e -> %s", key, e));
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                LOGGER.error(String.format("Redis occur handleCacheEvictError,e -> %s", e));
            }
        };

        return cacheErrorHandler;
    }

    private RedisSerializer keySerializer() {
        return new StringRedisSerializer ();
    }

    private RedisSerializer valueSerializer() {
        // 设置序列化
		/*Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer (Object.class);
		ObjectMapper om = new ObjectMapper ();
		om.setVisibility (PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping (ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper (om);
		return jackson2JsonRedisSerializer;*/

        //两种方式区别不大
        return new GenericJackson2JsonRedisSerializer ();
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<> ();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer (Object.class);

        ObjectMapper objectMapper = new ObjectMapper ();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer ());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
