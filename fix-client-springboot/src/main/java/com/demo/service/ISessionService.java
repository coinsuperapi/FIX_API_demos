package com.demo.service;

import quickfix.Session;
import quickfix.SessionID;

/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/6 5:29 PM
 */
public interface ISessionService {

    SessionID getSessionID();

    Session getSession(SessionID sessionID);

    Session getSession();
}
