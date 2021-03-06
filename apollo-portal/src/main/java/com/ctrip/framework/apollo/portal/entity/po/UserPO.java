package com.ctrip.framework.apollo.portal.entity.po;

import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author lepdou 2017-04-08
 */
@Entity
@Table(name = "Users")
public class UserPO {

  /**
   * 编号
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private long id;

  /**
   * 账号
   */
  @Column(name = "Username", nullable = false)
  private String username;

  /**
   * 密码
   */
  @Column(name = "Password", nullable = false)
  private String password;

  /**
   * 邮箱
   */
  @Column(name = "Email", nullable = false)
  private String email;

  /**
   * 是否开启
   */
  @Column(name = "Enabled", nullable = false)
  private int enabled;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getEnabled() {
    return enabled;
  }

  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }

  /**
   * 在 UserPO 的 #toUserInfo() 方法中，将 UserPO 转换成 UserBO
   * @return
   * userId 和 name 属性，都是指向 User.username
   */
  public UserInfo toUserInfo() {
    UserInfo userInfo = new UserInfo();
    userInfo.setName(this.getUsername());
    userInfo.setUserId(this.getUsername());
    userInfo.setEmail(this.getEmail());
    return userInfo;
  }
}
