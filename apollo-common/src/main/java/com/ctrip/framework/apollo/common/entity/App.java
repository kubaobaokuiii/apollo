package com.ctrip.framework.apollo.common.entity;

import com.ctrip.framework.apollo.common.utils.InputValidator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 数据库表名:App
 * @SQLDelete+@Where ，配合 BaseEntity.extends 字段，实现 App 的逻辑删除，判断是否做删除此应用操作
 * BaseEntity：App实体类继承BaseEntity抽象类
 * 注意:@SQLDelete+@Where 这里用到了反射 @Target 与 @Retention()
 * 问题:如何理解反射
 */
@Entity
@Table(name = "App")
@SQLDelete(sql = "Update App set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class App extends BaseEntity {

  /**
   * App名
   * @NotEmpty 用在集合类上面
   * @NotBlank 用在String上面
   * @NotNull 用在基本类型上
   */
  @NotBlank(message = "Name cannot be blank")
  @Column(name = "Name", nullable = false)
  private String name;

  /**
   * App编号
   * @Pattern 正则验证 及正则验证错误输出的信息message:只允许输入数字，字母和符号 - _ .
   *  @Column 列名
   */
  @NotBlank(message = "AppId cannot be blank")
  @Pattern(
      regexp = InputValidator.CLUSTER_NAMESPACE_VALIDATOR,
      message = InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE
  )
  @Column(name = "AppId", nullable = false)
  private String appId;

  /**
   * 部门编号
   */
  @Column(name = "OrgId", nullable = false)
  private String orgId;

  /**
   * 部门名
   */
  @Column(name = "OrgName", nullable = false)
  private String orgName;

  /**
   * 拥有人名
   * 例如在 Portal 系统中，使用系统的管理员账号，即 UserPO.username 字段
   */
  @NotBlank(message = "OwnerName cannot be blank")
  @Column(name = "OwnerName", nullable = false)
  private String ownerName;

  /**
   * 拥有人邮箱
   */
  @NotBlank(message = "OwnerEmail cannot be blank")
  @Column(name = "OwnerEmail", nullable = false)
  private String ownerEmail;

  public String getAppId() {
    return appId;
  }

  public String getName() {
    return name;
  }

  public String getOrgId() {
    return orgId;
  }

  public String getOrgName() {
    return orgName;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public void setOwnerEmail(String ownerEmail) {
    this.ownerEmail = ownerEmail;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String toString() {
    return toStringHelper().add("name", name).add("appId", appId)
        .add("orgId", orgId)
        .add("orgName", orgName)
        .add("ownerName", ownerName)
        .add("ownerEmail", ownerEmail).toString();
  }

  public static class Builder {

    public Builder() {
    }

    private App app = new App();

    public Builder name(String name) {
      app.setName(name);
      return this;
    }

    public Builder appId(String appId) {
      app.setAppId(appId);
      return this;
    }

    public Builder orgId(String orgId) {
      app.setOrgId(orgId);
      return this;
    }

    public Builder orgName(String orgName) {
      app.setOrgName(orgName);
      return this;
    }

    public Builder ownerName(String ownerName) {
      app.setOwnerName(ownerName);
      return this;
    }

    public Builder ownerEmail(String ownerEmail) {
      app.setOwnerEmail(ownerEmail);
      return this;
    }

    public App build() {
      return app;
    }

  }

  public static Builder builder() {
    return new Builder();
  }


}
