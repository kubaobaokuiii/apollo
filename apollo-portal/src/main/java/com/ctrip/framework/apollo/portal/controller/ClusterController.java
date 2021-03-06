package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.common.dto.ClusterDTO;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.portal.service.ClusterService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

import static com.ctrip.framework.apollo.common.utils.RequestPrecondition.checkModel;

/**
 * 提供 Cluster 的 API
 * 在创建 Cluster的界面中，点击【提交】按钮，调用创建 Cluster 的 API
 * 创建集群（添加集群）
 */
@RestController
public class ClusterController {

  private final ClusterService clusterService;
  private final UserInfoHolder userInfoHolder;

  public ClusterController(final ClusterService clusterService, final UserInfoHolder userInfoHolder) {
    this.clusterService = clusterService;
    this.userInfoHolder = userInfoHolder;
  }

  /**
   * 创建 Cluster 的 API 代码如下
   * @param appId 应用AppId
   * @param env 选择环境
   * @param cluster 集群名称
   * @Valid 做了非空校验
   * @return
   * post的接口
   * @PreAuthorize(...) 注解
   * 调用 PermissionValidator#hasCreateClusterPermission(appId) 方法，校验是否有创建 Cluster 的权限
   */
  @PreAuthorize(value = "@permissionValidator.hasCreateClusterPermission(#appId)")
  @PostMapping(value = "apps/{appId}/envs/{env}/clusters")
  public ClusterDTO createCluster(@PathVariable String appId, @PathVariable String env,
                                  @Valid @RequestBody ClusterDTO cluster) {

    //设置 ClusterDTO 的创建和修改人为当前管理员
    String operator = userInfoHolder.getUser().getUserId();
    cluster.setDataChangeLastModifiedBy(operator);
    cluster.setDataChangeCreatedBy(operator);

    //创建 Cluster 到 Admin Service
    return clusterService.createCluster(Env.valueOf(env), cluster);

  }

  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @DeleteMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}")
  public ResponseEntity<Void> deleteCluster(@PathVariable String appId, @PathVariable String env,
                                            @PathVariable String clusterName){
    clusterService.deleteCluster(Env.fromString(env), appId, clusterName);
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "apps/{appId}/envs/{env}/clusters/{clusterName:.+}")
  public ClusterDTO loadCluster(@PathVariable("appId") String appId, @PathVariable String env, @PathVariable("clusterName") String clusterName) {

    return clusterService.loadCluster(appId, Env.fromString(env), clusterName);
  }

}
