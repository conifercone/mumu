package com.sky.centaur.authentication.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 用户基本信息
 *
 * @author 单开宇
 * @since 2024-01-12
 */
@Entity
@Table(name = "users")
public class Users {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "enabled")
  private Boolean enabled;

}
