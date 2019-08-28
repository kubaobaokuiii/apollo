package com.ctrip.framework.apollo.portal.spi;

import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;

import java.util.List;

/**
 * @author Jason Song(song_s@ctrip.com)
 * User服务接口，用于给Portal提供用户搜索相关功能
 */
public interface UserService {

  List<UserInfo> searchUsers(String keyword, int offset, int limit);

  UserInfo findByUserId(String userId);

  List<UserInfo> findByUserIds(List<String> userIds);

}
