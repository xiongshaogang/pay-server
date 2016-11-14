package com.yql.biz.service.impl;

import com.yql.biz.dao.IPayAccountDao;
import com.yql.biz.dao.IPayBankDao;
import com.yql.biz.exception.MessageRuntimeException;
import com.yql.biz.model.PayAccount;
import com.yql.biz.model.PayBank;
import com.yql.biz.service.IPayBankService;
import com.yql.biz.vo.PayBankVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>支付银行卡Service</p>
 * creator simple
 * data 2016/11/10 0010.
 */
@Service
@Transactional
public class PayBankService implements IPayBankService {

    @Resource
    private IPayBankDao payBankDao;
    @Resource
    private IPayAccountDao payAccountDao;


    @Override
    public PayBank savePayBanke(PayBankVo payBankVo) {
        PayAccount payAccount = payAccountDao.findByUserCode(payBankVo.getUserCode());
        if (null == payAccount) {
            throw new MessageRuntimeException("error.payserver.saveySecurity.userCode");
        }
        if (payAccount.isRealNameAuth()) {
            PayBank payBank = payBankDao.findByPayAccountIdAndBankCard(payAccount.getId(), payBankVo.getBankCard());
            if (payBank != null) {
                throw new MessageRuntimeException("error.payserver.paybankCard.repeat");
            }
            PayBank newPayBak = PayBankVo.voToDomain(payBankVo, payAccount);
            List<PayBank> list = payBankDao.findByPayAccountIdOrderBySort(payAccount.getId());
            if (!CollectionUtils.isEmpty(list)) {
                newPayBak.setSort(list.size());
            }
            //// TODO: 2016/11/10 0010 调用第三方借口
            return payBankDao.save(newPayBak);
        } else {
            throw new MessageRuntimeException("error.payserver.isRealNameAuth");
        }

    }

    @Override
    public List<PayBankVo> findByUserCode(String userCode) {
        List<PayBank> list = payBankDao.findByUserCode(userCode);
        return PayBankVo.domainToVoList(list);
    }
}
