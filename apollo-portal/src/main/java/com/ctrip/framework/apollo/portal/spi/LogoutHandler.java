package com.ctrip.framework.apollo.portal.spi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用来实现用户退出功能
 */
public interface LogoutHandler {

  void logout(HttpServletRequest request, HttpServletResponse response);

}
