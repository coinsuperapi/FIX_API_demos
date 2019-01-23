package com.demo.service.impl;

import com.demo.model.OrderCreateReqBo;
import com.demo.service.IOrderService;
import com.demo.service.ISessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.CashOrderQty;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.fix44.NewOrderSingle;
import quickfix.fix44.OrderCancelRequest;
import quickfix.fix44.OrderStatusRequest;

import java.time.LocalDateTime;

/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/6 1:42 PM
 */
@Slf4j
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private ISessionService sessionService;

    @Override
    public String order(OrderCreateReqBo createReqBo) {
        SessionID sessionID = sessionService.getSessionID();
        NewOrderSingle order = new NewOrderSingle();
        order.set(new ClOrdID(createReqBo.getSourceOrdId()));
        Side side = new Side();
        side.setValue(createReqBo.getOrderAction() == 1 ? Side.BUY : Side.SELL);
        order.set(side);
        order.set(new TransactTime(LocalDateTime.now()));
        OrdType ordType = new OrdType();
        ordType.setValue(createReqBo.getOrderType() == 1 ? OrdType.MARKET : OrdType.LIMIT);
        order.set(ordType);
        order.set(new OrderQty(createReqBo.getQuantity().doubleValue()));
        order.set(new Price(createReqBo.getPrice().doubleValue()));
        order.set(new Symbol(createReqBo.getSymbol()));
        order.set(new CashOrderQty(createReqBo.getAmount().doubleValue()));
        try {
            boolean b = Session.sendToTarget(order, sessionID);
            log.info("buy result:{}", b);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "order";
    }

    @Override
    public void cancel(Long orderNo) {
        SessionID sessionID = sessionService.getSessionID();
        OrderCancelRequest cancelRequest = new OrderCancelRequest();
        cancelRequest.set(new OrderID(String.valueOf(orderNo)));
        try {
            Session.sendToTarget(cancelRequest, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    @Override
    public void query(String orderNo) {
        SessionID sessionID = sessionService.getSessionID();
        OrderStatusRequest statusRequest = new OrderStatusRequest();
        statusRequest.set(new OrderID(String.valueOf(orderNo)));
        try {
            Session.sendToTarget(statusRequest, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }
}
