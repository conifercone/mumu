package com.sky.centaur.authentication.client.grpc;

import com.sky.centaur.authentication.client.api.AuthorityGrpcService;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCmd;
import com.sky.centaur.authentication.client.api.grpc.AuthorityAddGrpcCo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * AuthorityGrpcService单元测试
 *
 * @author kaiyu.shan
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("dev")
public class AuthorityGrpcServiceTest {

  private final AuthorityGrpcService authorityGrpcService;

  @Autowired
  public AuthorityGrpcServiceTest(AuthorityGrpcService authorityGrpcService) {
    this.authorityGrpcService = authorityGrpcService;
  }

  @Test
  @Transactional
  public void add() {
    AuthorityAddGrpcCmd authorityAddGrpcCmd = AuthorityAddGrpcCmd.newBuilder()
        .setAuthorityAddCo(
            AuthorityAddGrpcCo.newBuilder().setId(926369451).setCode("test").setName("test")
                .build())
        .build();
    AuthorityAddGrpcCo authorityAddGrpcCo = authorityGrpcService.add(authorityAddGrpcCmd);
    Assertions.assertEquals("test", authorityAddGrpcCo.getCode());
  }
}
