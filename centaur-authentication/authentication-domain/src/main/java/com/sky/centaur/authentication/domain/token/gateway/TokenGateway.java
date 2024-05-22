package com.sky.centaur.authentication.domain.token.gateway;

/**
 * token领域网关
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
public interface TokenGateway {

  boolean validity(String token);
}
