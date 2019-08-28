package com.ctrip.framework.apollo.portal.constant;

/**
 * 权限类型
 * 分成 App 和 Namespace 两种级别的权限类型
 * App级别:targetId指向App编号
 * NameSpace级别:targetId指向App编号+NameSpace名字，是所有环境 + 所有集群都有权限，所以不能具体某个 Namespace
 */
public interface PermissionType {

  /**
   * system level permission
   */
  String CREATE_APPLICATION = "CreateApplication";
  String MANAGE_APP_MASTER = "ManageAppMaster";

  /**
   * APP level permission
   */

  //创建命名空间
  String CREATE_NAMESPACE = "CreateNamespace";

  //创建集群
  String CREATE_CLUSTER = "CreateCluster";

  /**
   * 分配用户权限的权限
   */
  String ASSIGN_ROLE = "AssignRole";

  /**
   * namespace level permission
   */

  //修改命名空间
  String MODIFY_NAMESPACE = "ModifyNamespace";

  //发布命名空间
  String RELEASE_NAMESPACE = "ReleaseNamespace";


}
