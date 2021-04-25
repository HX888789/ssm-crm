package com.hx.crm.workbench.web.controller;

import com.hx.crm.settings.domain.User;
import com.hx.crm.settings.service.UserService;
import com.hx.crm.utils.DateTimeUtil;
import com.hx.crm.utils.UUIDUtil;
import com.hx.crm.vo.PaginationVo;
import com.hx.crm.workbench.domain.Activity;
import com.hx.crm.workbench.domain.ActivityRemark;
import com.hx.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService as;
    @Autowired
    private UserService us;


    @RequestMapping("/getUserList.do")
    @ResponseBody
private List<User> getUserList(){
    System.out.println("获取用户信息列表1");
    List<User> user=us.getUserList();
    return user;
    }
    @RequestMapping("/save.do")
    @ResponseBody
private boolean save(Activity at, HttpServletRequest req){
        System.out.println("进入到市场添加操作");
        String id = UUIDUtil.getUUID();
//        创建时间：当前系统时间
        String createTime= DateTimeUtil.getSysTime();
//    创建人，当前登录用户
        String createBy= ((User) req.getSession().getAttribute("user")).getName();
        at.setId(id);
        at.setCreateTime(createTime);
        at.setCreateBy(createBy);
        boolean flag=as.save(at);
        return flag;
}
    @RequestMapping("/pageList.do")
    @ResponseBody
    private PaginationVo<Activity> pageList(String name, String owner, String startDate,
                                            String endDate, @RequestParam("pageNo") String pageNoStr,
                                            @RequestParam("pageSize") String pageSizeStr){
        System.out.println("进入到分页查询操作");
        int pageNo=Integer.valueOf(pageNoStr);
        int pageSize=Integer.valueOf(pageSizeStr);
//        计算出略过的数量
        int skipCount=(pageNo-1)*pageSize;
        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        PaginationVo<Activity> vo=as.pageList(map);

       return vo;
    }

    @RequestMapping("/delete.do")
    @ResponseBody
private boolean delete(@RequestParam("id") String ids[]){
        System.out.println("进入到市场活动的删除操作");
        boolean flag=as.delete(ids);
        return flag;
    }


    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Map<String, Object> getUserListAndActivity(String id){
        System.out.println("根据市场活动查询用户列表信息和根据市场活动查询单条记录的操作");
        Map<String,Object> map=as.getUserListAndActivity(id);
        return map;
    }
    @RequestMapping("/update.do")
    @ResponseBody
    private boolean update(Activity at,HttpServletRequest request){
        System.out.println("进入到修改操作了");
        //       修改时间：当前系统时间
        String editTime= DateTimeUtil.getSysTime();
//        修改人：当前登录的账户
        String editBy=((User) request.getSession().getAttribute("user")).getName();
        at.setCreateTime(editTime);
        at.setCreateBy(editBy);
        boolean flag=as.update(at);
        return flag;
    }
    @RequestMapping("/detail.do")
    @ResponseBody
    private ModelAndView detail(String id){
        System.out.println("进入到查看详细页面操作");
        ModelAndView mv=new ModelAndView();
        Activity a=as.detail(id);
        mv.addObject("a",a);
        mv.setViewName("/activity/detail");
        return mv;
    }

    @RequestMapping("/getRemarkListByAid.do")
    @ResponseBody
    private List<ActivityRemark> getRemarkListByAid(@RequestParam("activityId") String id){
        System.out.println("根据市场活动id，取得备注信息列表");
        List<ActivityRemark> arList=as.getRemarkListByAid(id);
        return arList;
    }

    @RequestMapping("/deleteRemark.do")
    @ResponseBody
    private Boolean deleteRemark(String id){
        System.out.println("删除备注操作");
        boolean flag=as.deleteRemark(id);
        return flag;
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    private Map<String ,Object> saveRemark(ActivityRemark ar,HttpServletRequest request){
        System.out.println("执行备注添加操作");
        String id =UUIDUtil.getUUID();
        String createTime= DateTimeUtil.getSysTime();
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        String editFlag="0";
        ar.setId(id);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);
        boolean flag=as.saveRemark(ar);
        Map<String ,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);
        return map;
    }
    @RequestMapping("/updateRemark.do")
    @ResponseBody
    private Map<String ,Object> updateRemark(ActivityRemark ar,HttpServletRequest request){
        System.out.println("执行修改备注的操作");
        String editTime= DateTimeUtil.getSysTime();
        String editBy=((User)request.getSession().getAttribute("user")).getName();
        String editFlag="1";
        ar.setEditFlag(editFlag);
        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        boolean flag=as.updateRemark(ar);
        Map<String ,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);
        return map;
    }
}
