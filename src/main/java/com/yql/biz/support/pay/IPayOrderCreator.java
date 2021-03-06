package com.yql.biz.support.pay;

import com.yql.biz.vo.PayOrderVo;

/**
 * <p>支付订单</p>
 * creator simple
 * data 2016/11/11 0011.
 */
public interface IPayOrderCreator{

    PayOrderVo transform(PayOrderVo payOrderVo);

    boolean supports(PayOrderVo payOrderVo);
}
