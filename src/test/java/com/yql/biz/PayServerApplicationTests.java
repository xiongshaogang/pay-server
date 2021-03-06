package com.yql.biz;

import com.alibaba.fastjson.JSON;
import com.yql.biz.dao.IPayAccountDao;
import com.yql.biz.dao.IPayOrderAccountDao;
import com.yql.biz.enums.SmallPayMoney;
import com.yql.biz.model.PayAccount;
import com.yql.biz.model.PayOrderAccount;
import com.yql.biz.service.IPayAccountService;
import com.yql.biz.vo.ProblemAnswerVo;
import com.yql.biz.vo.ResultPayOrder;
import com.yql.biz.vo.SecurityVo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableSpringConfigured
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PayServerApplicationTests {
    @Resource
    private IPayAccountService payAccountService;
    @Resource
    private IPayAccountDao payAccountDao;
    @Resource
    private IPayOrderAccountDao payOrderAccountDao;

    @Test
    @Ignore
    public void finAccount() {
        PayAccount byUserCode = payAccountService.findByUserCode("123456");
    }

    @Test
    @Ignore
    public void save() {
        PayAccount byUserCode = new PayAccount();
        byUserCode.setPayPassword("1245454");
        byUserCode.setRandomCode("1212");
        byUserCode.setSmallPayMoney(SmallPayMoney.MONEY_500);
        byUserCode.setSystemPaySeq(false);
        byUserCode.setUserCode("878");
        byUserCode.setCreatedTime(new Date());
        payAccountService.savePayAccount(byUserCode);
    }

    @Test
    @Ignore
    public void update() {
        PayAccount payAccount = payAccountDao.getOne(2);
        payAccount.setUserCode("6543111");
        payAccountService.savePayAccount(payAccount);
    }

    @Test
    @Ignore
    public void  testUpdateOrder(){
        PayOrderAccount byOrderNo = this.payOrderAccountDao.findByOrderNo("587965123548245466");
        PayOrderAccount orderAccount = new PayOrderAccount();
        BeanUtils.copyProperties(byOrderNo,orderAccount);
        orderAccount.setPayNo("123213");
        this.payOrderAccountDao.save(orderAccount);
    }


    @Test
    @Ignore
    public void  testProblem(){
        SecurityVo securityVo = new SecurityVo();
        securityVo.setUserCode("12345678");

        List<ProblemAnswerVo> list = new ArrayList<>();
        ProblemAnswerVo problemAnswerVo = new ProblemAnswerVo();
        problemAnswerVo.setAnswer("ni ijijij");
        problemAnswerVo.setProblemId(5);
        list.add(problemAnswerVo);
        ProblemAnswerVo problemAnswerVo1 = new ProblemAnswerVo();
        problemAnswerVo1.setAnswer("5846262");
        problemAnswerVo1.setProblemId(10001);
        list.add(problemAnswerVo1);
        securityVo.setAnswers(list);

        String s = JSON.toJSONString(securityVo);
        System.out.println(s);


    }

    @Test
    public void test1(){
        ResultPayOrder resultPayOrder = new ResultPayOrder();
        resultPayOrder.setOrderNo("1234569");
        resultPayOrder.setPayNo("254842221212");
        resultPayOrder.setPayStatus(20);
        resultPayOrder.setPayPrice(new BigDecimal(583));
        resultPayOrder.setMsg("成功");
        System.out.println(JSON.toJSONString(resultPayOrder));
    }
}
