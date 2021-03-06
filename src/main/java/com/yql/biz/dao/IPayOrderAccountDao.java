package com.yql.biz.dao;

import com.yql.biz.enums.PayType;
import com.yql.biz.model.PayOrderAccount;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <P>支付记录dao</P>
 * creator simple
 * data 2016/11/7 0007.
 */
@Repository
public interface IPayOrderAccountDao  extends JpaRepository<PayOrderAccount,Integer>{
    /**
     * 根据订单号查询订单信息
     * @param orderNo 订单号
     */
    PayOrderAccount findByOrderNo(String orderNo);
    /**
     * 根据支付订单号查询订单信息
     * @param payNo 支付订单号（pay-server生成的）
     */
    PayOrderAccount findByPayNo(String payNo);

    /**
     *
     * @param drawMoney 支付状态
     * @param value 状态
     *              @see com.yql.biz.enums.pay.PayStatus
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    List<PayOrderAccount> findByPayTypeAndPayStatusAndCreatedTimeBetween(PayType drawMoney, Integer value, Date startTime, Date endTime);

    /**
     *
     * @param orderNo 订单号
     * @param wxPay 支付类型
     */
    PayOrderAccount findByOrderNoAndPayType(String orderNo, PayType wxPay);

    /**
     * 查询某一个支付状态 一段时间的订单信息
     * @param wxPay 支付状态
     * @param startTime 开始时间
     * @param endTime 介绍时间
     */
    List<PayOrderAccount> findByPayTypeAndCreatedTimeBetween(PayType wxPay, Date startTime, Date endTime);
}
