package com.sky.centaur.authentication.infrastructure.token.redis;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.sky.centaur.authentication.infrastructure.token.redis.dataobject.TokenRedisDo;

/**
 * token redis repository
 *
 * @author 单开宇
 * @since 2024-03-19
 */
public interface TokenRepository extends
    RedisDocumentRepository<TokenRedisDo, String> {

}
