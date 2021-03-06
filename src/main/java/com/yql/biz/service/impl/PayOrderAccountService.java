package com.yql.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.yql.biz.client.IFyPayForClient;
import com.yql.biz.client.IWxPayClient;
import com.yql.biz.conf.ApplicationConf;
import com.yql.biz.dao.IPayBankDao;
import com.yql.biz.dao.IPayOrderAccountDao;
import com.yql.biz.enums.PayType;
import com.yql.biz.enums.fy.FyRequestType;
import com.yql.biz.enums.pay.PayStatus;
import com.yql.biz.enums.pay.WxPayResult;
import com.yql.biz.model.PayBank;
import com.yql.biz.model.PayOrderAccount;
import com.yql.biz.service.IPayOrderAccountService;
import com.yql.biz.support.constants.PayConstants;
import com.yql.biz.support.helper.IPayOrderAccountHelper;
import com.yql.biz.support.helper.IPayOrderParamHelper;
import com.yql.biz.support.helper.SendMessageHelper;
import com.yql.biz.support.pay.CloseOrderComposition;
import com.yql.biz.support.pay.PayOrderCreatorComposition;
import com.yql.biz.support.pay.QueryOrderComposition;
import com.yql.biz.util.PayDateUtil;
import com.yql.biz.util.PayUtil;
import com.yql.biz.util.PlatformPayUtil;
import com.yql.biz.vo.*;
import com.yql.biz.vo.pay.fy.FyPayForRequest;
import com.yql.biz.vo.pay.fy.FyPayForResponse;
import com.yql.biz.vo.pay.fy.FyPayRequest;
import com.yql.biz.vo.pay.response.WeiXinCloseOrderResponse;
import com.yql.biz.vo.pay.response.WeiXinResponse;
import com.yql.biz.vo.pay.response.WeiXinResponseResult;
import com.yql.biz.vo.pay.wx.WeiXinAppRequest;
import com.yql.biz.vo.pay.wx.WeiXinNotifyVo;
import com.yql.biz.vo.pay.wx.WeiXinOrderVo;
import com.yql.core.exception.MessageRuntimeException;
import com.yql.core.web.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>支付订单号</p>
 * creator simple
 * data 2016/11/10 0010.
 */
@Service
public class PayOrderAccountService implements IPayOrderAccountService {
    private static final Logger log = LoggerFactory.getLogger(PayOrderAccountService.class);
    @Resource
    private IPayOrderAccountDao payOrderAccountDao;
    @Resource
    private ApplicationConf applicationConf;
    @Resource(name ="payOrderCreatorComposition")
    private PayOrderCreatorComposition payOrderCreator;
    @Resource
    private SendMessageHelper sendMessageHelper;
    @Resource
    private IPayBankDao payBankDao;
    @Resource(name = "queryOrderComposition")
    private QueryOrderComposition queryOrderComposition;
    @Resource(name = "closeOrderComposition")
    private CloseOrderComposition closeOrderComposition;
    @Resource
    private IPayOrderParamHelper payOrderCardParamHelper;
    @Resource
    private IWxPayClient wxPayClient;
    @Resource
    private IFyPayForClient fyPayForClient;
    @Resource
    private IPayOrderAccountHelper payOrderAccountHelper;

    @Override
    public ResultPayOrder order(PayOrderVo payOrderVo) {
        log.info("pay-server param:" + JSON.toJSONString(payOrderVo));
        PayOrderVo orderVo = payOrderCreator.transform(payOrderVo);
        ResultPayOrder payOrder = PayOrderVo.toResultOrder(orderVo);
        log.debug("支付下单返回data:"+JSON.toJSONString(payOrder));
        if (PayStatus.PAY_UNSUCCESS.getValue().equals(payOrder.getPayStatus())){
            throw new RuntimeException(payOrder.getMsg());
        }
        return payOrder;
    }


    @Override
    @Transactional
    public String callPayNotify(HttpServletRequest request) {
        String notityXml = PayUtil.toXml(request);
        log.debug("callPayNotify:"+notityXml);
        WeiXinResponseResult weiXinResponse = new WeiXinResponseResult(WxPayResult.SUCCESS);
        WeiXinNotifyVo weiXinNotifyVo = (WeiXinNotifyVo)PlatformPayUtil.convertXmlStrToObject(WeiXinNotifyVo.class, notityXml);
        if (weiXinNotifyVo==null){
            weiXinResponse.setReturnCode(WxPayResult.FAIL);
            weiXinResponse.setReturnMsg("查询不到此商户订单");
            return PlatformPayUtil.payRequestXml(weiXinResponse);
        }
        PayOrderAccount orderAccount = payOrderAccountDao.findByPayNo(weiXinNotifyVo.getOutTradeNo());
        if (orderAccount!=null){
            //订单已经成功处理,直接返回给微信成功状态
            if (PayStatus.PAY_SUCCESS.getValue().equals(orderAccount.getPayStatus())){
                return PlatformPayUtil.payRequestXml(weiXinResponse);
            }else {
                ResultPayOrder resultPayOrder = new ResultPayOrder();
                if (WxPayResult.SUCCESS.name().equals(weiXinNotifyVo.getResultCode())){
                    String wxSign = weiXinNotifyVo.getSign();
                    log.debug("微信异步通知sign:"+wxSign);
                    weiXinNotifyVo.setSign(null);
                    String paySign = payOrderCardParamHelper.getSign(weiXinNotifyVo);
                    log.debug("微信异步通知sign【pay-server sigin】:"+paySign);
                    boolean sign = wxSign.equals(paySign);
                    log.debug("sign 是否成功:"+sign);
                    if (sign){
                        if (weiXinNotifyVo.getResultCode().equals(WxPayResult.SUCCESS.name())){
                            orderAccount.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
                            Date date = PayUtil.dataFormat(weiXinNotifyVo.getTimeEnd());
                            orderAccount.setBankTxTime(date);
                            orderAccount.setTxCode(weiXinNotifyVo.getOpenid());
                            orderAccount.setBankCode(weiXinNotifyVo.getBankType());
                            orderAccount.setPayOrder(weiXinNotifyVo.getTransactionId());
                            payOrderAccountHelper.saveOrder(orderAccount);
                            //payOrderAccountDao.save(orderAccount);
                        }
                        resultPayOrder.setPayNo(orderAccount.getPayNo());
                        resultPayOrder.setOrderNo(orderAccount.getOrderNo());
                        sendMessageHelper.sendWxNotifyResult(weiXinResponse,resultPayOrder,weiXinNotifyVo);
                    }else {
                        weiXinResponse.setReturnCode(WxPayResult.FAIL);
                        weiXinResponse.setReturnMsg("签名失败");
                    }
                }else {
                    weiXinResponse.setReturnCode(WxPayResult.FAIL);
                    weiXinResponse.setReturnMsg(weiXinNotifyVo.getResultCode());
                }
            }
        }else {
            weiXinResponse.setReturnCode(WxPayResult.FAIL);
            weiXinResponse.setReturnMsg("查询不到此商户订单");
        }
        String payRequestXml = PlatformPayUtil.payRequestXml(weiXinResponse);
        return payRequestXml;
    }

    @Override
    @Transactional
    public void updateDrawMoneyStatus(String payOrderNo,Integer payStatus,String msg) {
        PayOrderAccount byOrderNo = payOrderAccountDao.findByOrderNo(payOrderNo);
        if (!PayType.DRAW_MONEY.equals(byOrderNo.getPayType())) throw new MessageRuntimeException("error.payserver.payType.param");
        byOrderNo.setPayStatus(payStatus);
        byOrderNo.setMsg(msg);
        payOrderAccountHelper.saveOrder(byOrderNo);
        //payOrderAccountDao.save(byOrderNo);
        PayOrderVo payOrderVo = new PayOrderVo();
        payOrderVo.setOrderNo(payOrderNo);
        payOrderVo.setPayStatus(payStatus);
        sendMessageHelper.sendDrawMoney(payOrderVo);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DrawMoneyVo> findDrawMoneyList() {
        Date startTime = PayDateUtil.getStartBeforeDay();
        Date endTime = PayDateUtil.getEndTime();
        List<PayOrderAccount> list = payOrderAccountDao.findByPayTypeAndPayStatusAndCreatedTimeBetween(PayType.DRAW_MONEY,PayStatus.HANDLING.getValue(),startTime,endTime);
        List<DrawMoneyVo> drawMoneyVos = new ArrayList<>();
        DrawMoneyVo drawMoneyVo = null;
        PayBank bank = null;
        for (PayOrderAccount order: list) {
            bank = payBankDao.findByTxCode(order.getTxCode());
            drawMoneyVo = DrawMoneyVo.toVo(order,bank);
            drawMoneyVos.add(drawMoneyVo);
        }
        return drawMoneyVos;
    }

    @Override
    public void updateDrawMoney() {
        Date startTime = PayDateUtil.getStartBeforeDay();
        Date endTime = PayDateUtil.getEndTime();
        List<PayOrderAccount> list = payOrderAccountDao.findByPayTypeAndPayStatusAndCreatedTimeBetween(PayType.DRAW_MONEY,PayStatus.HANDLING.getValue(),startTime,endTime);
        log.debug("定时任务执行 提现操作:"+JSON.toJSONString(list));
        FyPayForRequest fyPayForRequest = null;
        for (PayOrderAccount order: list) {
            PayBank p = payBankDao.findByTxCode(order.getTxCode());
            int cent = PayUtil.priceToCent(order.getTotalPrice());
            fyPayForRequest = new FyPayForRequest(p.getBankId(),p.getCityNo(),p.getBankCard(),p.getCardholder(),cent,p.getPhoneNumber());
            String payRequestXml = PlatformPayUtil.payRequestXml(fyPayForRequest);
            FyPayRequest fyPayRequest = new FyPayRequest(applicationConf.getFyMerid(), FyRequestType.payforreq,payRequestXml);
            String md5String = fyPayRequest.toMd5String(applicationConf.getFyTradeKey());
            fyPayRequest.setMac(md5String);
            FyPayForResponse fyPayForResponse = fyPayForClient.payFor(fyPayRequest);
            int payStatus = PayStatus.PAY_UNSUCCESS.getValue();
            String msg = fyPayForResponse.getRet();
            if (PayConstants.FY_PAY_FOR_SUCCESS.equals(fyPayForResponse.getRet())){
                payStatus = PayStatus.PAY_SUCCESS.getValue();
            }
            updateDrawMoneyStatus(order.getOrderNo(),payStatus,msg);
        }
    }

    @Override
    public ResultQueryOrder findWxOrderInfo(String orderNo) {
        PayOrderAccount orderAccount = payOrderAccountDao.findByOrderNo(orderNo);
        ResultQueryOrder wxQueryOrder = queryOrderComposition.transform(orderAccount);
        return wxQueryOrder;
    }

    @Override
    public WeiXinCloseOrderResponse closeOrder(String orderNo) {
        PayOrderAccount payOrderAccount = payOrderAccountDao.findByOrderNo(orderNo);
        WeiXinCloseOrderResponse transform = closeOrderComposition.transform(payOrderAccount);
        return transform;
    }

    @Override
    public AppPrepayInfo prepay(String orderNo,String spbillCreateIp) {
        if (!PayUtil.validatedIp(spbillCreateIp)) spbillCreateIp="171.221.202.60";
        PayOrderAccount payOrderAccount = payOrderAccountDao.findByOrderNoAndPayType(orderNo,PayType.WX_PAY);
        if (payOrderAccount==null) throw  new MessageRuntimeException("error.payserver.param.order.notnull");
        WeiXinOrderVo weiXinOrderVo = new WeiXinOrderVo();
        weiXinOrderVo.setSpbillCreateIp(spbillCreateIp);
        String wxPayParam = payOrderCardParamHelper.getWxPayParam(weiXinOrderVo,payOrderAccount);
        ResponseModel<WeiXinResponse> weiXinResponseResponseModel = wxPayClient.sendPrepay(wxPayParam);
        if (weiXinResponseResponseModel!=null && weiXinResponseResponseModel.getData()!=null){
            WeiXinAppRequest weiXinAppRequest = new WeiXinAppRequest();
            WeiXinResponse data = weiXinResponseResponseModel.getData();
            if (data.getReturnCode().equals(WxPayResult.SUCCESS)){
                if (data.getAppId().equals(weiXinOrderVo.getAppId()) && data.getMchId().equals(weiXinOrderVo.getMchId()) ){
                    if (data.getResultCode().equals(WxPayResult.FAIL)) throw new RuntimeException(data.getErrCodeDes());
                    weiXinAppRequest.setAppId(weiXinOrderVo.getAppId());
                    weiXinAppRequest.setPrepayid(data.getPrepayId());
                    weiXinAppRequest.setPartnerid(data.getMchId());
                    weiXinAppRequest.setNonceStr(weiXinOrderVo.getNonceStr());
                    String sign = payOrderCardParamHelper.getSign(weiXinAppRequest);
                    weiXinAppRequest.setSign(sign);
                    log.debug("生成app参数   :"+JSON.toJSONString(weiXinAppRequest));
                    Map<String, Object> stringObjectMap = PlatformPayUtil.obtObjParm(weiXinAppRequest);
                    String json =  JSON.toJSONString(stringObjectMap);
                    return new AppPrepayInfo(json);
                }else {
                    log.error("预付订单异常  : 微信response appID MchId"+data.getAppId()+"\t"+ data.getMchId()+"\n"+" 系统微信账户信息: appID MchId"+weiXinOrderVo.getMchId() +"\t"+weiXinOrderVo.getAppId());
                    throw new RuntimeException("下微信预付订单异常,appID MchId 不一致");
                }
            }else {
                throw new RuntimeException(data.getReturnMsg());
            }
        }
        return null;
    }

    @Override
    public ResultPayOrder findOrderInfo(String orderNo) {
        PayOrderAccount byOrderNo = payOrderAccountDao.findByOrderNo(orderNo);
        PayOrderVo payOrderVo = PayOrderVo.domainToVo(byOrderNo);
        ResultPayOrder payOrder = PayOrderVo.toResultOrder(payOrderVo);
        return payOrder;
    }

    @Override
    @Transactional
    public void updateWxOrder() {
        Date startTime = PayDateUtil.getWxStartTime();
        Date endTime = PayDateUtil.getWxEndTime();
        List<PayOrderAccount> list = payOrderAccountDao.findByPayTypeAndCreatedTimeBetween(PayType.WX_PAY,startTime,endTime);
        sendMessageHelper.timeCheckOrderStatus(list);
    }
}
