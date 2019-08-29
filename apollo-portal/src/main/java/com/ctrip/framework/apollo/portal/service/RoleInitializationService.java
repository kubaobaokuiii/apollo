package com.ctrip.framework.apollo.portal.service;

import com.ctrip.framework.apollo.common.entity.App;

/**
 * 提供角色初始化服务
 */
public interface RoleInitializationService {

  /**
   * 初始化App级别的Role
   * @param app
   */
  public void initAppRoles(App app);

  /**
   * 初始化NameSpace级的Role
   * @param appId
   * @param namespaceName
   * @param operator
   */
  public void initNamespaceRoles(String appId, String namespaceName, String operator);

  public void initNamespaceEnvRoles(String appId, String namespaceName, String operator);

  public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator);

  public void initCreateAppRole();

  public void initManageAppMasterRole(String appId, String operator);

}
