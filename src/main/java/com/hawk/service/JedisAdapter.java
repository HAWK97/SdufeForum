package com.hawk.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class JedisAdapter implements InitializingBean {

    private JedisPool pool = null;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password}")
    private String password;

    @Override
    public void afterPropertiesSet() {
        // 在初始化 Bean 时配置 JedisPool 连接池
        if (StringUtils.isEmpty(password)) {
            pool = new JedisPool(new GenericObjectPoolConfig(), "localhost", 6379, 2000, null);
        } else {
            pool = new JedisPool(new GenericObjectPoolConfig(), host, 6379, 2000, password);
        }
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void incr(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.incrBy(key, 1L);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.srem(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return 0;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void zadd(String key, double score, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.zadd(key, score, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void zrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.zrem(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public boolean zismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return null != jedis.zrank(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return false;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return 0;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public Set<String> zrevrange(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key, 0, -1);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void lpush(String key, String value) {
        Jedis jedis = null;
        try {
            // 在JedisPool连接池中取得Jedis连接
            jedis = pool.getResource();
            jedis.lpush(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void rpush(String key, String value) {
        Jedis jedis = null;
        try {
            // 在JedisPool连接池中取得Jedis连接
            jedis = pool.getResource();
            jedis.rpush(key, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public List<String> lrange(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public long llen(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.llen(key);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
            return 0;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    public void lrem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.lrem(key, 0, value);
        } catch (Exception e) {
            log.error("Jedis连接异常：" + e.getMessage());
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }
}
