package com.sky.centaur.authentication.infrastructure.token.redis;

import com.redis.om.spring.repository.RedisDocumentRepository;
import com.sky.centaur.authentication.infrastructure.token.redis.dataobject.OidcIdTokenRedisDo;

/**
 * oidc id token redis repository
 *
 * @author kaiyu.shan
 * @since 2024-03-19
 */
public interface OidcIdTokenRepository extends
    RedisDocumentRepository<OidcIdTokenRedisDo, Integer> {

}
