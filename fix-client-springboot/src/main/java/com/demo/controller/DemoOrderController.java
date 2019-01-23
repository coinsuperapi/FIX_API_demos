package com.demo.controller;

import com.demo.model.OrderCreateReqBo;
import com.demo.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;


/**
 * @author zhangbin
 * @Description:
 * @date create at 2018/12/5 7:05 PM
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/order")
public class DemoOrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 下单操作
     *
     * @param createReqBo
     * @return
     */
    @PostMapping("/create")
    public String order(@RequestBody @Validated OrderCreateReqBo createReqBo) {
        orderService.order(createReqBo);
        return "order create sent!";
    }

    /**
     * 撤单操作
     *
     * @param orderNo
     * @return
     */
    @PutMapping("/cancel/{orderNo}")
    public String cancel(@PathVariable @NotNull Long orderNo) {
        orderService.cancel(orderNo);
        return "order cancel sent!";
    }

    /**
     * 订单查询
     *
     * @param orderNo
     * @return
     */
    @GetMapping("/query/{orderNo}")
    public String query(@PathVariable @NotNull String orderNo) {
        orderService.query(orderNo);
        return "order query sent!";
    }
}
