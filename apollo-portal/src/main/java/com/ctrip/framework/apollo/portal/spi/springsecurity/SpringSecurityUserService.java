package com.ctrip.framework.apollo.portal.spi.springsecurity;

import com.google.common.collect.Lists;

import com.ctrip.framework.apollo.core.utils.StringUtils;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.UserPO;
import com.ctrip.framework.apollo.portal.repository.UserRepository;
import com.ctrip.framework.apollo.portal.spi.UserService;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * @author lepdou 2017-03-10
 * 基于Spring Security的用户UserService实现类
 */
public class SpringSecurityUserService implements UserService {

  //使用 PasswordEncoder 对 password 加密
  private PasswordEncoder encoder = new BCryptPasswordEncoder();

  /**
   * 默认角色数组
   */
  private List<GrantedAuthority> authorities;

  @Autowired
  private JdbcUserDetailsManager userDetailsManager;

  @Autowired
  private UserRepository userRepository;

  /**
   * authorities 属性，只有一个元素，为 "ROLE_user"
   */
  @PostConstruct
  public void init() {
    authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_user"));
  }

  /**
   *新增或者更新角色
   * @param user
   */
  @Transactional
  public void createOrUpdate(UserPO user) {
    String username = user.getUsername();

    //想创建Spring Security 的用户User
    User userDetails = new User(username, encoder.encode(user.getPassword()), authorities);

    //若存在，则进行更新
    if (userDetailsManager.userExists(username)) {

      userDetailsManager.updateUser(userDetails);

    } else {
      //若不存在，则进行新增用户
      userDetailsManager.createUser(userDetails);
    }

    /**
     * 更新邮箱
     */
    UserPO managedUser = userRepository.findByUsername(username);
    managedUser.setEmail(user.getEmail());

    userRepository.save(managedUser);
  }

  @Override
  public List<UserInfo> searchUsers(String keyword, int offset, int limit) {
    List<UserPO> users;
    if (StringUtils.isEmpty(keyword)) {
      users = userRepository.findFirst20ByEnabled(1);
    } else {
      users = userRepository.findByUsernameLikeAndEnabled("%" + keyword + "%", 1);
    }

    List<UserInfo> result = Lists.newArrayList();
    if (CollectionUtils.isEmpty(users)) {
      return result;
    }

    result.addAll(users.stream().map(UserPO::toUserInfo).collect(Collectors.toList()));

    return result;
  }

  @Override
  public UserInfo findByUserId(String userId) {
    UserPO userPO = userRepository.findByUsername(userId);
    return userPO == null ? null : userPO.toUserInfo();
  }

  @Override
  public List<UserInfo> findByUserIds(List<String> userIds) {
    List<UserPO> users = userRepository.findByUsernameIn(userIds);

    if (CollectionUtils.isEmpty(users)) {
      return Collections.emptyList();
    }

    List<UserInfo> result = Lists.newArrayList();

    result.addAll(users.stream().map(UserPO::toUserInfo).collect(Collectors.toList()));

    return result;
  }


}
