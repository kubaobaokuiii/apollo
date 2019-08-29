package com.ctrip.framework.apollo.portal.spi.defaultimpl;

import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.entity.BaseEntity;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.constant.PermissionType;
import com.ctrip.framework.apollo.portal.constant.RoleType;
import com.ctrip.framework.apollo.portal.entity.po.Permission;
import com.ctrip.framework.apollo.portal.entity.po.Role;
import com.ctrip.framework.apollo.portal.repository.PermissionRepository;
import com.ctrip.framework.apollo.portal.service.RoleInitializationService;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.service.SystemRoleManagerService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by timothy on 2017/4/26.
 * 默认 RoleInitializationService 实现类
 */
public class DefaultRoleInitializationService implements RoleInitializationService {

  @Autowired
  private RolePermissionService rolePermissionService;
  @Autowired
  private PortalConfig portalConfig;
  @Autowired
  private PermissionRepository permissionRepository;

  @Transactional
  public void initAppRoles(App app) {

    String appId = app.getAppId();

    /**
     * 创建App拥有者的角色名
     */
    String appMasterRoleName = RoleUtils.buildAppMasterRoleName(appId);

    //has created before
    /**
     * 校验角色是否存在，存在，则直接返回
     */
    if (rolePermissionService.findRoleByRoleName(appMasterRoleName) != null) {
      return;
    }

    //获取应用的修改创建人
    String operator = app.getDataChangeCreatedBy();

    //create app permissions
    /**
     * 创建App角色
     */
    createAppMasterRole(appId, operator);

    //create manageAppMaster permission
    createManageAppMasterRole(appId, operator);

    //assign master role to user
    /**
     * 授权角色给App拥有者
     */
    rolePermissionService
        .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(appId), Sets.newHashSet(app.getOwnerName()),
            operator);

    /**
     * 初始化NameSpace角色
     */
    initNamespaceRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);

    initNamespaceEnvRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);

    //assign modify、release namespace role to user
    /**
     * 授权角色给App创建者（Namespace 是自动创建的，并且是通过创建人来操作的）
     */
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.MODIFY_NAMESPACE),
        Sets.newHashSet(operator), operator);
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.RELEASE_NAMESPACE),
        Sets.newHashSet(operator), operator);

  }

  /**
   * 初始化NameSpace角色
   * @param appId
   * @param namespaceName
   * @param operator
   */
  @Transactional
  public void initNamespaceRoles(String appId, String namespaceName, String operator) {

    //创建NameSpace修改的角色
    String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName);

    //若不存在对应的角色Role，则创建
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE,
          modifyNamespaceRoleName, operator);
    }

    //创建NameSpace发布的角色名
    String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName);

    //若不存在对应的角色Role，则创建
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE,
          releaseNamespaceRoleName, operator);
    }
  }

  @Transactional
  public void initNamespaceEnvRoles(String appId, String namespaceName, String operator) {
    List<Env> portalEnvs = portalConfig.portalSupportedEnvs();

    for (Env env : portalEnvs) {
      initNamespaceSpecificEnvRoles(appId, namespaceName, env.toString(), operator);
    }
  }

  @Transactional
  public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator) {
    String modifyNamespaceEnvRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName, env);
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE, env,
          modifyNamespaceEnvRoleName, operator);
    }

    String releaseNamespaceEnvRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName, env);
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE, env,
          releaseNamespaceEnvRoleName, operator);
    }
  }

  @Transactional
  public void initCreateAppRole() {
    if (rolePermissionService.findRoleByRoleName(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME) != null) {
      return;
    }
    Permission createAppPermission = permissionRepository.findTopByPermissionTypeAndTargetId(PermissionType.CREATE_APPLICATION, SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID);
    if (createAppPermission == null) {
      // create application permission init
      createAppPermission = createPermission(SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID, PermissionType.CREATE_APPLICATION, "apollo");
      rolePermissionService.createPermission(createAppPermission);
    }
    //  create application role init
    Role createAppRole = createRole(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME, "apollo");
    rolePermissionService.createRoleWithPermissions(createAppRole, Sets.newHashSet(createAppPermission.getId()));
  }

  @Transactional
  private void createManageAppMasterRole(String appId, String operator) {
    Permission permission = createPermission(appId, PermissionType.MANAGE_APP_MASTER, operator);
    rolePermissionService.createPermission(permission);
    Role role = createRole(RoleUtils.buildManageAppMasterRoleName(PermissionType.MANAGE_APP_MASTER, appId), operator);
    Set<Long> permissionIds = new HashSet<>();
    permissionIds.add(permission.getId());
    rolePermissionService.createRoleWithPermissions(role, permissionIds);
  }

  // fix historical data
  @Transactional
  public void initManageAppMasterRole(String appId, String operator) {
    String manageAppMasterRoleName = RoleUtils.buildManageAppMasterRoleName(PermissionType.MANAGE_APP_MASTER, appId);
    if (rolePermissionService.findRoleByRoleName(manageAppMasterRoleName) != null) {
      return;
    }
    synchronized (DefaultRoleInitializationService.class) {
      createManageAppMasterRole(appId, operator);
    }
  }

  /**
   * 创建App拥有者角色
   * @param appId
   * @param operator
   */
  private void createAppMasterRole(String appId, String operator) {

    /**
     * 创建App对应的Permissions集合，并保存到数据库
     */
    Set<Permission> appPermissions =
        Stream.of(PermissionType.CREATE_CLUSTER, PermissionType.CREATE_NAMESPACE, PermissionType.ASSIGN_ROLE)
            .map(permissionType -> createPermission(appId, permissionType, operator)).collect(Collectors.toSet());
    Set<Permission> createdAppPermissions = rolePermissionService.createPermissions(appPermissions);
    Set<Long>
        appPermissionIds =
        createdAppPermissions.stream().map(BaseEntity::getId).collect(Collectors.toSet());

    //create app master role
    /**
     * 调用 RoleUtils#buildAppMasterRoleName(appId) 方法，创建 App 拥有者的角色名
     */
    Role appMasterRole = createRole(RoleUtils.buildAppMasterRoleName(appId), operator);

    rolePermissionService.createRoleWithPermissions(appMasterRole, appPermissionIds);
  }

  /**
   * 创建Permission对象
   * @param targetId
   * @param permissionType
   * @param operator
   * @return
   */
  private Permission createPermission(String targetId, String permissionType, String operator) {
    Permission permission = new Permission();
    permission.setPermissionType(permissionType);
    permission.setTargetId(targetId);
    permission.setDataChangeCreatedBy(operator);
    permission.setDataChangeLastModifiedBy(operator);
    return permission;
  }

  /**
   * 创建并保存App对应的Role对象
   * @param roleName
   * @param operator
   * @return
   */
  private Role createRole(String roleName, String operator) {
    Role role = new Role();
    role.setRoleName(roleName);
    role.setDataChangeCreatedBy(operator);
    role.setDataChangeLastModifiedBy(operator);
    return role;
  }

  /**
   * 创建NameSpace角色
   * @param appId
   * @param namespaceName
   * @param permissionType
   * @param roleName
   * @param operator
   */
  private void createNamespaceRole(String appId, String namespaceName, String permissionType,
                                   String roleName, String operator) {
    /**
     * 创建NameSpace对应的Permission对象，并保存到数据库
     * RoleUtils.buildNamespaceTargetId()，创建NamaSpace的目标编号
     */
    Permission permission =
        createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName), permissionType, operator);
    Permission createdPermission = rolePermissionService.createPermission(permission);

    /**
     *
     */
    Role role = createRole(roleName, operator);
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }

  private void createNamespaceEnvRole(String appId, String namespaceName, String permissionType, String env,
                                      String roleName, String operator) {
    Permission permission =
        createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName, env), permissionType, operator);
    Permission createdPermission = rolePermissionService.createPermission(permission);

    Role role = createRole(roleName, operator);
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }
}
