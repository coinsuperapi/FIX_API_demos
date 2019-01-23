package com.demo.service;

import com.demo.model.OrderCreateReqBo;

/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/6 1:42 PM
 */
public interface IOrderService {

    /**
     * 下单
     *
     * @return
     */
    String order(OrderCreateReqBo createReqBo);

    /**
     * 撤单
     *
     * @param orderNo
     */
    void cancel(Long orderNo);

    /**
     * 订单查询
     *
     * @param orderNo
     */
    void query(String orderNo);
}
