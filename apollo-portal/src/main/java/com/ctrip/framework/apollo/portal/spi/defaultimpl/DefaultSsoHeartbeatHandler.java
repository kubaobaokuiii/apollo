package com.ctrip.framework.apollo.portal.spi.defaultimpl;

import com.ctrip.framework.apollo.portal.spi.SsoHeartbeatHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jason Song(song_s@ctrip.com)
 * 实现SsoHeartbeatHandler方法，登录信息过期会刷新此接口
 * 在default_sso_heartbeat.html界面中记录了每 60 秒刷新一次页面
 */
public class DefaultSsoHeartbeatHandler implements SsoHeartbeatHandler {

  @Override
  public void doHeartbeat(HttpServletRequest request, HttpServletResponse response) {
    try {
        response.sendRedirect("default_sso_heartbeat.html");
    } catch (IOException e) {
    }
  }

}
