package com.demo.model;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zhangbin
 * @Description: 委托订单的请求参数
 * @date create at 2018/12/21 3:27 PM
 */
@Data
@Valid
public class OrderCreateReqBo {

    /**
     * fix 客户端传的订单 id
     */
    @NotBlank
    private String sourceOrdId;

    /**
     * 交易对
     */
    @NotBlank
    private String symbol;

    /**
     * 委托价格（必须大于0）
     */
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal price;

    /**
     * 买卖单类型
     * fix 协议中： 1-买单，2-卖单
     */
    @NotNull
    @Range(min = 1, max = 2)
    private Integer orderAction;

    /**
     * 订单类型
     * fix 协议中： 1-市价单，2-限价单
     */
    @NotNull
    @Range(min = 1, max = 2)
    private Integer orderType;

    /**
     * 标的币数量（卖单或者限价买单此值必须大于0)
     */
    @NotNull
    private BigDecimal quantity;

    /**
     * 计价币数量（市价买单时此值必须大于0）
     */
    @NotNull
    private BigDecimal amount;
}
