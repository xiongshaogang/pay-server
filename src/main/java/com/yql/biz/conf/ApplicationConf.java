package com.yql.biz.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>支付系统配置文件</p>
 * creator simple
 * data 2016/11/8 0008.
 */
@ConfigurationProperties(prefix = "yql")
@Configuration
public class ApplicationConf implements ExternalApi {
    private String passwordMd5Str;
    private String orderNumKey;
    //应用ID
    private String appid;
    //商户号
    private String mchid;
    //微信key
    private String wxKey;
    private String wxNotifyUrl;
    private String aliPayNotifyUrl;
    //监听类的topic
    private String listenerTopic;
    //发送消息的topic
    private String sendMsgTopic;
    //机构代码
    private String institutionId;
    private Kunlun kunlun;

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getPasswordMd5Str() {
        return passwordMd5Str;
    }

    public void setPasswordMd5Str(String passwordMd5Str) {
        this.passwordMd5Str = passwordMd5Str;
    }

    public String getOrderNumKey() {
        return orderNumKey;
    }

    public void setOrderNumKey(String orderNumKey) {
        this.orderNumKey = orderNumKey;
    }

    public Kunlun getKunlun() {
        return kunlun;
    }

    public void setKunlun(Kunlun kunlun) {
        this.kunlun = kunlun;
    }

    public String getListenerTopic() {
        return listenerTopic;
    }

    public void setListenerTopic(String listenerTopic) {
        this.listenerTopic = listenerTopic;
    }

    public String getSendMsgTopic() {
        return sendMsgTopic;
    }

    public void setSendMsgTopic(String sendMsgTopic) {
        this.sendMsgTopic = sendMsgTopic;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getWxKey() {
        return wxKey;
    }

    public void setWxKey(String wxKey) {
        this.wxKey = wxKey;
    }

    public String getWxNotifyUrl() {
        return wxNotifyUrl;
    }

    public void setWxNotifyUrl(String wxNotifyUrl) {
        this.wxNotifyUrl = wxNotifyUrl;
    }

    public String getAliPayNotifyUrl() {
        return aliPayNotifyUrl;
    }

    public void setAliPayNotifyUrl(String aliPayNotifyUrl) {
        this.aliPayNotifyUrl = aliPayNotifyUrl;
    }

    @Override
    public String getPayUrl() {
        return kunlun.getHost()+kunlun.getPayUri();
    }

    public static class Kunlun {
        private String host; //接口host
        private String payUri; //关联方总数

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPayUri() {
            return payUri;
        }

        public void setPayUri(String payUri) {
            this.payUri = payUri;
        }
    }
}
