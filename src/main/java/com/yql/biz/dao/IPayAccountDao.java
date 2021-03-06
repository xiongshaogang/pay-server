package com.yql.biz.dao;

import com.yql.biz.model.PayAccount;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>支付账户dao</p>
 * creator simple
 * data 2016/11/7 0007.
 */
@Repository
public interface IPayAccountDao  extends JpaRepository<PayAccount, Integer> {

    /**
     * 根据userCode查询payAccount 信息
     * @param userCode
     * @return
     */
    PayAccount findByUserCode(String userCode);


}
