package com.ctrip.framework.apollo.biz.entity;

import com.ctrip.framework.apollo.common.entity.BaseEntity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 继承 BaseEntity 抽象类，Commit 实体，记录 Item 的 KV 变更历史
 * appId + clusterName + namespaceName 字段，可以确认唯一 Namespace 记录
 */
@Entity
@Table(name = "Commit")
@SQLDelete(sql = "Update Commit set isDeleted = 1 where id = ?")
@Where(clause = "isDeleted = 0")
public class Commit extends BaseEntity {

  /**
   * 变更集合
   * JSON 格式化
   */
  @Lob
  @Column(name = "ChangeSets", nullable = false)
  private String changeSets;

  /**
   * App编号
   */
  @Column(name = "AppId", nullable = false)
  private String appId;

  /**
   * Cluster的名字
   */
  @Column(name = "ClusterName", nullable = false)
  private String clusterName;

  /**
   * Namespace名字
   */
  @Column(name = "NamespaceName", nullable = false)
  private String namespaceName;

  /**
   * 备注
   */
  @Column(name = "Comment")
  private String comment;

  public String getChangeSets() {
    return changeSets;
  }

  public void setChangeSets(String changeSets) {
    this.changeSets = changeSets;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return toStringHelper().add("changeSets", changeSets).add("appId", appId).add("clusterName", clusterName)
        .add("namespaceName", namespaceName).add("comment", comment).toString();
  }

}
