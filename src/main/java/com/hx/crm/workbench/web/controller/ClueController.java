package com.hx.crm.workbench.web.controller;

import com.hx.crm.settings.domain.User;
import com.hx.crm.settings.service.UserService;
import com.hx.crm.utils.DateTimeUtil;
import com.hx.crm.utils.UUIDUtil;
import com.hx.crm.workbench.domain.Activity;
import com.hx.crm.workbench.domain.Clue;
import com.hx.crm.workbench.domain.Tran;
import com.hx.crm.workbench.service.ActivityService;
import com.hx.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clue")
public class ClueController{
    @Autowired
    private UserService us;
    @Autowired
    private ClueService cs;
    @Autowired
    private ActivityService as;
    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList(){
        System.out.println("取得用户信息列表2");
        List<User> uList=us.getUserList();
        return uList;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(Clue clue, HttpServletRequest request){
        System.out.println("进入到线索添加操作");
        String id = UUIDUtil.getUUID();
        String createTime= DateTimeUtil.getSysTime();
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        clue.setId(id);
        clue.setCreateTime(createTime);
        clue.setCreateBy(createBy);
        boolean flag=cs.save(clue);
        return flag;
    }
    @RequestMapping("/detail.do")
    @ResponseBody
    private ModelAndView detail(String id){
        System.out.println("跳转到相信信息页");
        ModelAndView mv=new ModelAndView();
        Clue c=cs.detail(id);
        mv.addObject("c",c);
        mv.setViewName("/clue/detail");
        return mv;
    }
    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByClueId(String clueId){
        System.out.println("根据id查询关联的市场活动列表");
        List<Activity> aList= as.getActivityListByClueId(clueId);
        return aList;
    }
    @RequestMapping("/unbund.do")
    @ResponseBody
    private boolean unbund(String id){
        System.out.println("进入到解除关联操作");
        boolean flag=cs.unbund(id);
        return flag;
    }
    @RequestMapping("/getActivityListByNameAndNotByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByNameAndNotByClueId(String aname,String clueId){
        System.out.println("进入到市场活动列表（根据市场模糊查询，排除掉已经关联指定线索的列表）");
        Map<String ,Object> map=new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);
        List<Activity> aList=as.getActivityListByNameAndNotByClueId(map);
        return aList;

    }
    @RequestMapping("/bund.do")
    @ResponseBody
    private boolean bund(String aid[],String cid){
        System.out.println("执行关联市场活动的操作");
        boolean flag=cs.bund(aid,cid);
        return flag;
    }


    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String aname){
        System.out.println("查询市场活动列表（根据名称模糊查）");
        List<Activity> aList=as.getActivityListByName(aname);
        return aList;
    }


    @RequestMapping("/convert.do")
    private ModelAndView convert(String clueId, String flag,HttpServletRequest request){
        System.out.println("执行线索转换的操作");
        System.out.println("clueId======================"+clueId);
        Tran t=null;
        ModelAndView mv=new ModelAndView();
        String createBy=((User)request.getSession().getAttribute("user")).getName();
        if("a".equals(flag)){
//            接受交易表单中的数据
            t=new Tran();
            String money=request.getParameter("money");
            String name=request.getParameter("name");
            String expectedDate=request.getParameter("expectedDate");
            String stage=request.getParameter("stage");
            String activityId=request.getParameter("activityId");
            String id=UUIDUtil.getUUID();
            String createTime= DateTimeUtil.getSysTime();
            t.setId(id);
            t.setCreateTime(createTime);
            t.setCreateBy(createBy);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
        }
        /*业务层传递的参数
        * 1.必须传递的参数clueId，有了这个后我们才知道转换哪条记录
        * 2.必须传递参数t，因为在线索转换的过程中，有可能会临时创建一笔交易（业务层的t也有可能是null）*/
        boolean flag1=cs.convert(clueId,t,createBy);
        if(flag1)
        {
        mv.setViewName("redirect:/workbench/clue/index.jsp");
        }
        mv.addObject(flag1);
        return mv;
    }

}
