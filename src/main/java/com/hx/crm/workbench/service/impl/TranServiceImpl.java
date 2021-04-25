package com.hx.crm.workbench.service.impl;

import com.hx.crm.utils.DateTimeUtil;
import com.hx.crm.utils.UUIDUtil;
import com.hx.crm.workbench.dao.CustomerDao;
import com.hx.crm.workbench.dao.TranDao;
import com.hx.crm.workbench.dao.TranHistoryDao;
import com.hx.crm.workbench.domain.Customer;
import com.hx.crm.workbench.domain.Tran;
import com.hx.crm.workbench.domain.TranHistory;
import com.hx.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranServiceImpl implements TranService {
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;
    @Autowired
    private CustomerDao customerDao;


    @Override
    public boolean save(Tran t, String customerName) {
        /*
        * 交易添加业务
        *   在做添加之前，t里面少一个customerId，就是客户的主键
        *   先处理客户相关的需求
        *  (1) 判断customerName 根据客户名称在客户表进行精确查询
        *         如果有这个客户 则取出这个客户的id封装到t对象中 如果没有这个客户 就新建一个客户，封装到t对象中
        *  (2) 经过以上操作后，t对象中的信息就全了，需要执行添加交易的操作
        *
        *  (3) 创建交易历史
        * */
        boolean flag=true;

        Customer cus = customerDao.getCustomerByName(customerName);
        if (cus==null){
//            如果cus为空 需要创建客户
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setName(customerName);
            cus.setCreateTime(t.getCreateTime());
            cus.setCreateBy(t.getCreateBy());
            cus.setNextContactTime(t.getNextContactTime());
            cus.setOwner(t.getOwner());

//            添加用户
            int Count1 = customerDao.save(cus);
            if(Count1 != 1){
                flag = false;
            }
        }

//        通过以上处理 这个客户就一定存在了
//        将客户的id封装到t对象中
        t.setCustomerId(cus.getId());

//        添加交易
        int Count2 = tranDao.save(t);
        if(Count2 != 1){
            flag = false;
        }

//        添加交易历史
        TranHistory th=new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setMoney(t.getMoney());
        th.setMoney(t.getMoney());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateBy(t.getCreateBy());
        th.setCreateTime(t.getCreateTime());

        int Count3 =tranHistoryDao.save(th);
        if(Count3 != 1){
            flag = false;
        }


        return flag;
    }

    @Override
    public Tran detail(String id) {
        Tran t = tranDao.detail(id);
        return  t;
    }

    @Override
    public List<TranHistory> getHistoryListByTranId(String TranId) {

        List<TranHistory> thList = tranHistoryDao.getHistoryListByTranId(TranId);

        return thList;
    }

    @Override
    public boolean changeStage(Tran t) {
        boolean flag =  true;

        int Count1 = tranDao.changeStage(t);

        if(Count1!=1){
            flag=false;
        }
//        交易阶段改变后，生成一条交易历史
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setCreateBy(t.getEditBy());
        th.setCreateTime(DateTimeUtil.getSysTime());
        th.setExpectedDate(t.getExpectedDate());
        th.setMoney(t.getMoney());
        th.setTranId(t.getId());
        th.setStage(t.getStage());
        th.setPossibility(t.getPossibility());

//        添加交易历史
        int Count2 = tranHistoryDao.save(th);
        if (Count2!=1){
            flag = false;
        }
        return flag;
    }
}
