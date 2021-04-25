package com.hx.crm.workbench.service.impl;

import com.hx.crm.utils.DateTimeUtil;
import com.hx.crm.utils.UUIDUtil;
import com.hx.crm.workbench.dao.*;
import com.hx.crm.workbench.domain.*;
import com.hx.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClueServiceImpl implements ClueService {
//    线索相关表
    @Autowired
    private ClueDao clueDao;
    @Autowired
    private ClueActivityRelationDao clueActivityRelationDao;
    @Autowired
    private ClueRemarkDao clueRemarkDao;
//    交易相关表
    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private CustomerRemarkDao customerRemarkDao;
    @Autowired
    private ContactsDao contactsDao;
    @Autowired
    private ContactsRemarkDao contactsRemarkDao;
    @Autowired
    private ContactsActivityRelationDao contactsActivityRelationDao;
//    转换相关表
    @Autowired
    private TranDao tranDao;
    @Autowired
    private TranHistoryDao tranHistoryDao;

    @Override
    public boolean save(Clue clue) {
        boolean flag=true;
        int Count= clueDao.save(clue);
        if(Count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Clue detail(String id) {
        Clue c=clueDao.detail(id);
        return c;
    }

    @Override
    public boolean unbund(String id) {
        boolean flag=true;
        int Count=clueActivityRelationDao.unbund(id);
        if(Count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean bund(String[] aid, String cid) {
        boolean flag=true;

//        把数组遍历了
        for(String id:aid){
//            取得每一个aid和cid做关联
            ClueActivityRelation car=new ClueActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setClueId(cid);
            car.setActivityId(id);

//            执行添加关联关系表中的记录
            int Count=clueActivityRelationDao.bund(car);
            if(Count!=1){
                flag=false;
            }
        }
        return flag;
    }

    @Override
    public boolean convert(String clueId, Tran t, String createBy) {
        String createTime= DateTimeUtil.getSysTime();

        boolean flag=true;

//        （1）通过线索id获取线索对象（线索当中封装了线索的信息）
        Clue c = clueDao.getByid(clueId);

//        （2）通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在）

        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        if(cus==null){

//            说明以前没有这个客户 需要新建一个
            cus=new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setAddress(c.getAddress());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setOwner(c.getOwner());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setName(company);
            cus.setDescription(c.getDescription());
            cus.setCreateTime(createTime);
            cus.setCreateBy(createBy);
            cus.setContactSummary(c.getContactSummary());

//            添加客户
            int Count1 = customerDao.save(cus);
            if(Count1!=1)
            {
                flag=false;
            }
        }

//        （3）通过线索对象提取联系人信息，保存联系人(经过第二步之后客户对象已经有了，将来在处理其他表的时候，如果要使用客户的id，直接使用cus.id就可以了)
        Contacts con=new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setEditTime(c.getEditTime());
        con.setDescription(c.getDescription());
        con.setCustomerId(cus.getId());
        con.setCreateTime(createTime);
        con.setCreateBy(createBy);
        con.setContactSummary(c.getContactSummary());
        con.setAppellation(c.getAppellation());
        con.setAddress(c.getAddress());
//        添加联系人
        int Count2 = contactsDao.save(con);
        if(Count2 !=1){
            flag=false;
        }
//        经过第三步处理后，联系人的信息我们已经拥有了，将来在处理其他表的时候，如果要使用到联系人的id 直接使用con.getId

//        （4）线索备注转换到客户备注以及联系人备注
//        查询出与该线索关联的备注信息列表
        List<ClueRemark> clueRemarkList = clueRemarkDao.getListByClueId(clueId);
        //            取出每一条线索的备注
        for (ClueRemark clueRemark:clueRemarkList) {

//            取出备注信息
            String noteContent = clueRemark.getNoteContent();

//            创建客户备注对象，添加客户备注
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setId(UUIDUtil.getUUID());
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(cus.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setNoteContent(noteContent);
            int Count3 = customerRemarkDao.save(customerRemark);
            if (Count3 != 1) {
                flag = false;
            }


//            创建联系人备注对象，添加联系人
            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setId(UUIDUtil.getUUID());
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(con.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setNoteContent(noteContent);
            int Count4 = contactsRemarkDao.save(contactsRemark);
            if (Count4 != 1) {
                flag = false;
            }
        }

//            （5）”线索和市场活动“ 的关系转换到”联系人和市场活动的关系“
//            查询出该条线索关联的市场活动，查询与市场活动的关联关系列表
            List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getListByClueId(clueId);
//            遍历出每一条与市场活动关联的关联关系记录
            for(ClueActivityRelation clueActivityRelation :clueActivityRelationList){

//                从每一条遍历出来的记录中取出关联的市场活动id
                String activityId=clueActivityRelation.getActivityId();

                /*创建联系人与市场活动的关联关系对象 让第三步生成的联系人与市场活动做关联*/
                ContactsActivityRelation contactsActivityRelation =new ContactsActivityRelation();
                contactsActivityRelation.setId(UUIDUtil.getUUID());
                contactsActivityRelation.setActivityId(activityId);
                contactsActivityRelation.setContactsId(con.getId());
//                添加联系人与市场活动的关联关系
                int Count5 = contactsActivityRelationDao.save(contactsActivityRelation);
                if(Count5!=1){
                    flag=false;
                }

            }

//            如果有创建交易需求，创建一条交易
        if(t!=null){

//            t对象在controller里面已经封装好的信息如下
//            id moner name expecteDate stage activityId createBy createTime，
//            可以通过第一步生成的c对象，取出一些信息，继续完成对t对象的封装
            t.setSource(c.getSource());
            t.setOwner(c.getOwner());
            t.setNextContactTime(c.getNextContactTime());
            t.setDescription(c.getDescription());
            t.setCustomerId(cus.getId());
            t.setContactSummary(c.getContactSummary());
            t.setContactsId(con.getId());
//            添加交易
            int Count6 = tranDao.save(t);
            if(Count6 != 1){
                flag = false;
            }

//        （7）如果创建了交易，则创建一条交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setCreateTime(createTime);
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setStage(t.getStage());
            tranHistory.setTranId(t.getId());
            tranHistory.setCreateBy(createBy);

//            添加交易历史
            int Count7 = tranHistoryDao.save(tranHistory);
            if(Count7!=1){
                flag=false;
            }
        }

//        (8)删除线索备注
        for (ClueRemark clueRemark:clueRemarkList) {

            int Count8 = clueRemarkDao.delete(clueRemark);
            if(Count8!=1){
                flag=false;
            }

        }

//        （9）删除线索和市场活动的关系
        for(ClueActivityRelation clueActivityRelation :clueActivityRelationList){


            int Count9=clueActivityRelationDao.delete(clueActivityRelation);
            if(Count9!=1){
                flag=false;
            }

        }

//        （10）删除线索
        int Count10 = clueDao.delete(clueId);
        if(Count10!=1){
            flag=false;
        }

        return flag;
    }


}
