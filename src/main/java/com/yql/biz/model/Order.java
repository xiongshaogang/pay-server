package com.yql.biz.model;


import com.yql.core.model.Domain;

public class Order extends Domain {

    private long orderNo; //订单号

    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }
}
