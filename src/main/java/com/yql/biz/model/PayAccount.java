package com.yql.biz.model;

import com.yql.biz.enums.IdentificationType;
import com.yql.biz.enums.SmallPayMoney;
import com.yql.biz.util.PayUtil;
import com.yql.core.model.Domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * <p>支付账号实体类</p>
 * @author  simple
 * @version 1.0.0
 * data 2016/11/7 0007.
 */
@Entity
@Table(name = "pay_account")
@Cacheable
public class PayAccount extends Domain {
    //用户code
    @Column(name = "user_code")
    @NotNull(message = "{com.yql.validation.constraints.userCode.message}")
    private String userCode;
    //支付密码
    @Column(name = "pay_password")
    private String payPassword;
    //随机数
    @Column(name = "random_code")
    private String randomCode  = PayUtil.randomCode(6);
    //是否默认系统支付银行卡顺序 1 默认 0 不默认
    @Column(name = "system_pay_seq")
    private boolean systemPaySeq;
    //小额支付 0 未开通 1 开通
    @Column(name = "small_pay")
    private boolean smallPay;
    //小额支付金额 枚举 MONEY_200  MONEY_500 MONEY_800 MONEY_1000 MONEY_2000
    @Column(name = "small_pay_money")
    @Enumerated(value = EnumType.STRING)
    private SmallPayMoney smallPayMoney = SmallPayMoney.MONEY_200;
    //是否实名认证
    @Column(name = "real_name_auth")
    private boolean realNameAuth;
    //证件类型
    @Enumerated(value = EnumType.STRING)
    private IdentificationType  identificationType;
    //证件号码
    private String identificationNumber;
    //电话号码
    private String phoneNumber;

    public IdentificationType getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    public boolean isSystemPaySeq() {
        return systemPaySeq;
    }

    public void setSystemPaySeq(boolean systemPaySeq) {
        this.systemPaySeq = systemPaySeq;
    }

    public boolean isSmallPay() {
        return smallPay;
    }

    public void setSmallPay(boolean smallPay) {
        this.smallPay = smallPay;
    }

    public SmallPayMoney getSmallPayMoney() {
        return smallPayMoney;
    }

    public void setSmallPayMoney(SmallPayMoney smallPayMoney) {
        this.smallPayMoney = smallPayMoney;
    }

    public boolean isRealNameAuth() {
        return realNameAuth;
    }

    public void setRealNameAuth(boolean realNameAuth) {
        this.realNameAuth = realNameAuth;
    }
}
