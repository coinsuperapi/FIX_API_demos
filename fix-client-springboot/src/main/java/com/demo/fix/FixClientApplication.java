package com.demo.fix;

import com.alibaba.fastjson.JSONObject;
import com.demo.service.ISessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import quickfix.Application;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.StringField;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.RawData;
import quickfix.field.RawDataLength;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;


/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/5 8:42 PM
 */
@Slf4j
@Component("clientApplication")
public class FixClientApplication extends quickfix.fix44.MessageCracker implements Application {

    @Autowired
    private ISessionService sessionService;
    @Value("${api.fix.secret}")
    private String apiFixSecret;

    private String getMsgString(Message message, SessionID sessionID) {
        Map<String, String> map = new LinkedHashMap<>();
        Iterator<Field<?>> iterator = message.iterator();
        Session session = sessionService.getSession(sessionID);
        DataDictionary dataDictionary = session.getDataDictionary();
        while (iterator.hasNext()) {
            Field<?> field = iterator.next();
            try {
                map.put(dataDictionary.getFieldName(field.getField()),
                        message.getString(field.getField()));
            } catch (FieldNotFound fieldNotFound) {
                log.error("field not found! fieldTag:{}", field.getField());
            }
        }
        return JSONObject.toJSONString(map);
    }

    /**
     * 每当一个新的会话被创建，该方法被调用。
     *
     * @param sessionID
     */
    @Override
    public void onCreate(SessionID sessionID) {
        log.info("onCreate: SessionId={}", sessionID);
    }

    /**
     * 当登录操作成功完成时，该方法被调用。
     *
     * @param sessionId
     */
    @Override
    public void onLogon(SessionID sessionId) {
        log.info("logon success! SessionId:{}", sessionId);
    }

    @Override
    public void onLogout(SessionID sessionId) {
        log.info("logout success! SessionId:{}", sessionId);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        String msgString = getMsgString(message, sessionID);
        try {
            StringField msgType = message.getHeader().getField(new MsgType());
            if(MsgType.LOGON.equals(msgType.getValue())){
                StringBuilder sb = new StringBuilder();
                TreeMap<String,String> fields = new TreeMap<>();
                fields.put("SendingTime",MessageUtils.getStringField(message.toString(),SendingTime.FIELD));
                fields.put("MsgType",MessageUtils.getStringField(message.toString(),MsgType.FIELD));
                fields.put("MsgSeqNum",MessageUtils.getStringField(message.toString(),MsgSeqNum.FIELD));
                fields.put("SenderCompID",MessageUtils.getStringField(message.toString(),SenderCompID.FIELD));
                fields.put("TargetCompID",MessageUtils.getStringField(message.toString(),TargetCompID.FIELD));
                fields.put("secretkey",apiFixSecret);
                for(String fValue:fields.values()){
                    sb.append(fValue).append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                String sign = DigestUtils.md5Hex(sb.toString());
                message.getHeader().setField(new RawData(sign));
                message.getHeader().setField(new RawDataLength(sign.length()));
            }
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        }
        log.info("toAdmin: Message={}, SessionId={}, msgString={}", message, sessionID, msgString);
    }

    /**
     * 判断是否是指定消息
     *
     * @param message
     * @param type
     * @return
     */
    private boolean isMessageOfType(Message message, String type) {
        try {
            return type.equals(message.getHeader().getField(new MsgType()).getValue());
        } catch (FieldNotFound fieldNotFound) {
            log.error("The unknown message type!");
            return false;
        }
    }

    /**
     * 每个管理级别的消息将通过该方法处理，如心跳，登录以及注销。
     *
     * @param message
     * @param sessionID
     * @throws FieldNotFound
     * @throws IncorrectTagValue
     */
    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectTagValue {
        String msgString = getMsgString(message, sessionID);
        log.info("fromAdmin: Message={}, SessionId={}, msgString={}", message, sessionID, msgString);
        try {
            // syncSeqNum(message, sessionID);
            crack(message, sessionID);
        } catch (UnsupportedMessageType unsupportedMessageType) {

        }
    }

    /**
     * 同步和服务端的序列号
     *
     * @param message
     * @param sessionID
     */
    private void syncSeqNum(Message message, SessionID sessionID) {
        // MsgSeqNum too low, expecting 108 but received 87
        String prefix = "MsgSeqNum too low, expecting ";
        Optional.ofNullable(message)
                .map(msg -> {
                    try {
                        return msg.getField(new Text());
                    } catch (FieldNotFound fieldNotFound) {
                        log.debug("Not MsgSeqNum too low!");
                        return null;
                    }
                })
                .map(field -> field.getValue())
                .filter(value -> value.startsWith(prefix))
                .map(value -> value.substring(prefix.length(), value.lastIndexOf(" but received")))
                .map(strSeq -> Integer.parseInt(strSeq))
                .ifPresent(seqNum -> {
                    Session session = sessionService.getSession(sessionID);
                    try {
                        session.setNextSenderMsgSeqNum(seqNum);
                        session.logon();
                    } catch (IOException e) {
                        log.error("set next seqNum error!", e);
                    }
                });
    }

    @Override
    public void toApp(Message message, SessionID sessionID) {
        String msgString = getMsgString(message, sessionID);
        log.info("toApp: Message={}, SessionId={}, msgString:{}", message, sessionID, msgString);
    }

    /**
     * 每个应用级别的消息将通过该方法处理，如委托指令，执行报告，证券信息以及市场数据。
     *
     * @param message
     * @param sessionID
     */
    @Override
    public void fromApp(Message message, SessionID sessionID) {
        String msgString = getMsgString(message, sessionID);
        log.info("fromApp: Message={}, SessionId={}, msgString:{}", message, sessionID, msgString);
    }

}

