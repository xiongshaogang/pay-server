package com.yql.biz.conf;

/**
 * <p>调用第三方接口地址</p>
 * @auther simple
 * data 2016/11/14 0014.
 */
public interface ExternalApi {
    /**
     * 快捷支付 url
     * @return
     */
    String getPayUrl();

    /**
     * 微信预付订单url
     * @return
     */
    String getWxPrepayUrl();

    /**
     * 微信查询订单url
     * @return
     */
    String getWxQueryOrder();

    /**
     * 关闭微信订单
     */
    String getWxCloseOrder();
}
