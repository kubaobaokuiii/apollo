package com.ctrip.framework.apollo.common.entity;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 * BaseEntity：基础实体抽象类
 * @MappedSuperclass: 使用JPA的@MappedSuperclass注解将实体类的多个属性分别封装到不同的非实体类中。
 *@MappedSuperclass注解只能标注在类上
 * 标注为@MappedSuperclass的类将不是一个完整的实体类。它将不会映射到数据库表，但是它的属性都将映射到其子类的数据库表字段中
 * 标注为@MappedSuperclass的类不能再标注@Entity或@Table注解，
 * 也无需实现序列化接口。但是如果一个标注为@MappedSuperclass的类继承了另外一个实体类或者另外一个同样标注了
 * @MappedSuperclass的类的话，他将可以使用@AttributeOverride或@AttributeOverrides注解重定义其父类
 * (无论是否是实体类)的属性映射到数据库表中的字段。
 *
 * 问题:@Inheritance的作用???
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity {

  /**
   * 编号
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "Id")
  private long id;

  /**
   * 是否删除
   */
  @Column(name = "IsDeleted", columnDefinition = "Bit default '0'")
  protected boolean isDeleted = false;

  /**
   * 数据创建人
   * 例如在 Portal 系统中，使用系统的管理员账号，即 UserPO.username 字段
   */
  @Column(name = "DataChange_CreatedBy", nullable = false)
  private String dataChangeCreatedBy;

  /**
   * 数据创建时间
   */
  @Column(name = "DataChange_CreatedTime", nullable = false)
  private Date dataChangeCreatedTime;

  /**
   * 数据最后更新人
   * 例如在 Portal 系统中，使用系统的管理员账号，即 UserPO.username 字段
   */
  @Column(name = "DataChange_LastModifiedBy")
  private String dataChangeLastModifiedBy;

  /**
   * 数据最后更新人
   */
  @Column(name = "DataChange_LastTime")
  private Date dataChangeLastModifiedTime;

  public String getDataChangeCreatedBy() {
    return dataChangeCreatedBy;
  }

  public Date getDataChangeCreatedTime() {
    return dataChangeCreatedTime;
  }

  public String getDataChangeLastModifiedBy() {
    return dataChangeLastModifiedBy;
  }

  public Date getDataChangeLastModifiedTime() {
    return dataChangeLastModifiedTime;
  }

  public long getId() {
    return id;
  }

  public boolean isDeleted() {
    return isDeleted;
  }

  public void setDataChangeCreatedBy(String dataChangeCreatedBy) {
    this.dataChangeCreatedBy = dataChangeCreatedBy;
  }

  public void setDataChangeCreatedTime(Date dataChangeCreatedTime) {
    this.dataChangeCreatedTime = dataChangeCreatedTime;
  }

  public void setDataChangeLastModifiedBy(String dataChangeLastModifiedBy) {
    this.dataChangeLastModifiedBy = dataChangeLastModifiedBy;
  }

  public void setDataChangeLastModifiedTime(Date dataChangeLastModifiedTime) {
    this.dataChangeLastModifiedTime = dataChangeLastModifiedTime;
  }

  public void setDeleted(boolean deleted) {
    isDeleted = deleted;
  }

  public void setId(long id) {
    this.id = id;
  }

  /**
   * @PrePersist、@PreUpdate、@PreRemove 注解，CRD 操作前，设置对应的时间字段。
   */
  /**
   * 保存前置方法
   */
  @PrePersist
  protected void prePersist() {
    if (this.dataChangeCreatedTime == null) dataChangeCreatedTime = new Date();
    if (this.dataChangeLastModifiedTime == null) dataChangeLastModifiedTime = new Date();
  }

  /**
   * 更新前置方法
   */
  @PreUpdate
  protected void preUpdate() {
    this.dataChangeLastModifiedTime = new Date();
  }

  /**
   * 删除前置方法
   */
  @PreRemove
  protected void preRemove() {
    this.dataChangeLastModifiedTime = new Date();
  }

  protected ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(this).omitNullValues().add("id", id)
        .add("dataChangeCreatedBy", dataChangeCreatedBy)
        .add("dataChangeCreatedTime", dataChangeCreatedTime)
        .add("dataChangeLastModifiedBy", dataChangeLastModifiedBy)
        .add("dataChangeLastModifiedTime", dataChangeLastModifiedTime);
  }

  public String toString(){
    return toStringHelper().toString();
  }
}
