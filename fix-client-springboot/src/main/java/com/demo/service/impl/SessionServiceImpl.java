package com.demo.service.impl;

import com.demo.service.ISessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

import java.util.Optional;
import java.util.Properties;

/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/6 5:30 PM
 */
@Service
public class SessionServiceImpl implements ISessionService {

    @Autowired
    private SessionSettings clientSessionSettings;

    private SessionID sessionID;

    @Override
    public SessionID getSessionID() {
        return Optional.ofNullable(sessionID)
                .orElseGet(() -> {
                    String beginString, senderCompID, senderSubID, senderLocationID, targetCompID, targetSubID, targetLocationID, sessionQualifier;
                    Properties properties = clientSessionSettings.getDefaultProperties();
                    beginString = properties.getProperty(SessionSettings.BEGINSTRING);
                    senderCompID = properties.getProperty(SessionSettings.SENDERCOMPID);
                    senderSubID = properties.getProperty(SessionSettings.SENDERSUBID);
                    senderLocationID = properties.getProperty(SessionSettings.SENDERLOCID);
                    targetCompID = properties.getProperty(SessionSettings.TARGETCOMPID);
                    targetSubID = properties.getProperty(SessionSettings.TARGETSUBID);
                    targetLocationID = properties.getProperty(SessionSettings.TARGETLOCID);
                    sessionQualifier = properties.getProperty(SessionSettings.SESSION_QUALIFIER);
                    SessionID sId = new SessionID(beginString, senderCompID, senderSubID, senderLocationID, targetCompID, targetSubID, targetLocationID, sessionQualifier);
                    this.sessionID = sId;
                    return sId;
                });
    }

    @Override
    public Session getSession(SessionID sessionID) {
        return Session.lookupSession(sessionID);
    }

    @Override
    public Session getSession() {
        return getSession(getSessionID());
    }
}
