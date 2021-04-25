package com.hx.crm.workbench.service.impl;

import com.hx.crm.settings.dao.UserDao;
import com.hx.crm.settings.domain.User;
import com.hx.crm.vo.PaginationVo;
import com.hx.crm.workbench.dao.ActivityDao;
import com.hx.crm.workbench.dao.ActivityRemarkDao;
import com.hx.crm.workbench.domain.Activity;
import com.hx.crm.workbench.domain.ActivityRemark;
import com.hx.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityRemarkDao activityRemarkDao;
    @Autowired
    private UserDao userDao;

    @Override
    public boolean save(Activity at) {
        boolean flag=true;
        int count=activityDao.save(at);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public PaginationVo<Activity> pageList(Map<String, Object> map) {
//        取total
        int total=activityDao.getTotalByCondition(map);


//        取datalist
       List<Activity> dataList= activityDao.getActivityByCondition(map);

//        封装到vo
        PaginationVo<Activity> vo=new PaginationVo<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        System.out.println("==========================total"+total);
        for(Activity i:dataList)
            System.out.println("======================datalist"+i);
//        将vo返回
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        System.out.println("进入到删除操作的业务层");
        boolean flag=true;
//        查询出需要删除的备注的数量
        int Count1=activityRemarkDao.getCountByAids(ids);

        System.out.println(Count1);
//        删除备注，返回收到影响的条数(实际删除的数量)
        int Count2=activityRemarkDao.deleteByAids(ids);
        if(Count1!=Count2){
            flag=false;
        }

//        删除市场活动
        int Count3=activityDao.delete(ids);
        if(Count3!=ids.length){
            flag=false;
        }
        return flag;
    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
       Map<String,Object> map=new HashMap<>();
//       取ulist
        List<User> uList=userDao.getUserList();
//        取a
        Activity a=activityDao.getById(id);

//        打包到map之中
        map.put("uList",uList);
        map.put("a",a);
        return map;
    }

    @Override
    public boolean update(Activity at) {
        boolean flag=true;
        int count=activityDao.update(at);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity a=activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String id) {
        List<ActivityRemark> arList=activityRemarkDao.getRemarkListByAid(id);
        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag=true;
        int Count=activityRemarkDao.deleteRemark(id);
        if(Count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        Boolean flag=true;
        int Count=activityRemarkDao.saveRemark(ar);
        if(Count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        Boolean flag=true;
        int Count=activityRemarkDao.updateRemark(ar);
        if(Count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String clueId) {
        List<Activity> aList=activityDao.getActivityListByClueId(clueId);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, Object> map) {
        List<Activity> aList=activityDao.getActivityListByNameAndNotByClueId(map);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> aList=activityDao.getActivityListByName(aname);
        return aList;
    }
}
