package com.ctrip.framework.apollo.portal.controller;

import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.UserPO;
import com.ctrip.framework.apollo.portal.spi.LogoutHandler;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.spi.springsecurity.SpringSecurityUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 用户管理
 * 提供User的API
 * 在用户管理界面，点击提交按钮，点击创建或者更新User的API
 */
@RestController
public class UserInfoController {

  private final UserInfoHolder userInfoHolder;
  private final LogoutHandler logoutHandler;
  private final UserService userService;

  public UserInfoController(
      final UserInfoHolder userInfoHolder,
      final LogoutHandler logoutHandler,
      final UserService userService) {
    this.userInfoHolder = userInfoHolder;
    this.logoutHandler = logoutHandler;
    this.userService = userService;
  }

  /**
   * 创建或者更新User
   * @param user
   * 调用 PermissionValidator#isSuperAdmin() 方法，校验是否为超级管理员
   */
  @PreAuthorize(value = "@permissionValidator.isSuperAdmin()")
  @PostMapping("/users")
  public void createOrUpdateUser(@RequestBody UserPO user) {

    //校验username或者password非空
    if (StringUtils.isContainEmpty(user.getUsername(), user.getPassword())) {
      throw new BadRequestException("Username and password can not be empty.");
    }

    //新增或者更新User
    if (userService instanceof SpringSecurityUserService) {
      ((SpringSecurityUserService) userService).createOrUpdate(user);
    } else {
      throw new UnsupportedOperationException("Create or update user operation is unsupported");
    }

  }

  /**
   * UserInfoHolder获取当前登录用户信息，SSO 一般都是把当前登录用户信息放在线程 ThreadLocal 上
   * @return
   */
  @GetMapping("/user")
  public UserInfo getCurrentUserName() {

    return userInfoHolder.getUser();

  }

  /**
   * 用户退出
   * @param request
   * @param response
   * @throws IOException
   */
  @GetMapping("/user/logout")
  public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
    logoutHandler.logout(request, response);
  }

  @GetMapping("/users")
  public List<UserInfo> searchUsersByKeyword(@RequestParam(value = "keyword") String keyword,
                                             @RequestParam(value = "offset", defaultValue = "0") int offset,
                                             @RequestParam(value = "limit", defaultValue = "10") int limit) {
    return userService.searchUsers(keyword, offset, limit);
  }

  @GetMapping("/users/{userId}")
  public UserInfo getUserByUserId(@PathVariable String userId) {
    return userService.findByUserId(userId);
  }


}
