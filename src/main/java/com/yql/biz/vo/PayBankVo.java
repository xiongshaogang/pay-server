package com.yql.biz.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yql.biz.enums.CardType;
import com.yql.biz.model.PayAccount;
import com.yql.biz.model.PayBank;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>支付银行vo</p>
 * creator simple
 * data 2016/11/10 0010.
 */
public class PayBankVo {
    @NotNull(message = "{com.yql.validation.constraints.userCode.message}")
    private String userCode;
    private Integer payAccountId;
    //银行卡
    @NotNull(message = "{com.yql.validation.constraints.bankCard.message}")
    private String bankCard;
    //银行卡名称
    private String bankName;
    //持卡人
   /* @NotNull(message = "{com.yql.validation.constraints.cardholder.message}")*/
    private String cardholder;
    //快捷支付金额
    private BigDecimal quickPaymentAmount;
    private CardType cardType;
    private String phoneNumber;
    //交易编码
    private String txCode;
    //排序字段
    private int sort;
    //银行卡所在城市code
    private String cityNo;

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getPayAccountId() {
        return payAccountId;
    }

    public void setPayAccountId(Integer payAccountId) {
        this.payAccountId = payAccountId;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        String bankCardNo = StringUtils.trimAllWhitespace(bankCard);
        this.bankCard = bankCardNo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public BigDecimal getQuickPaymentAmount() {
        return quickPaymentAmount;
    }

    public void setQuickPaymentAmount(BigDecimal quickPaymentAmount) {
        this.quickPaymentAmount = quickPaymentAmount;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTxCode() {
        return txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo;
    }

    public static void voToDomain(PayBank payBank, PayBankVo payBankVo, PayAccount payAccount){
        BeanUtils.copyProperties(payBankVo,payBank);
        payBank.setPayAccountId(payAccount.getId());
    }

    public static List<PayBankVo> domainToVoList(List<PayBank> list) {
        String json = JSON.toJSONString(list);
        List<PayBankVo> payBankList = JSON.parseObject(json, new TypeReference<List<PayBankVo>>() {
        });
        return payBankList;
    }
}
