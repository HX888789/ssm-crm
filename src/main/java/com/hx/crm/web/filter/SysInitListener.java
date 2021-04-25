package com.hx.crm.web.filter;

import com.hx.crm.settings.domain.DicValue;
import com.hx.crm.settings.service.DicService;
import com.hx.crm.settings.service.impl.DicServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    /*该方法用来监听上下文域对象的方法
    * 当域对象创建完毕后，马上执行该方法*/
    @Override
    public void contextInitialized(ServletContextEvent event) {

        System.out.println("上下文域对象创建了");
        System.out.println("服务器缓存处理数据字典开始");
//        event：该参数能够取得监听的对象
//        监听的什么对象 就能取得什么对象
        ServletContext application=event.getServletContext();
//        ApplicationContext context= WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
         DicService ds= WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DicServiceImpl.class);
//        取数据字典 应该管业务层要七个list 可以打包成为一个map
        Map<String,List<DicValue>> map=ds.getAll();
        Set<String> set=map.keySet();
        for (String key:set){
            application.setAttribute(key,map.get(key));
        }
        System.out.println("服务器缓存处理数据字典结束");

//        数据字典处理完毕后，处理properties文件，解析该文件，将该属性文件中的键值对关系处理成为java中键值对的关系

//        解析properties文件
        ResourceBundle rb=ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> e = rb.getKeys();

        Map<String ,String> pMap = new HashMap<>();

        while (e.hasMoreElements()){
//            阶段
            String key = e.nextElement();
//            可能性
            String value = rb.getString(key);

            pMap.put(key,value);
            application.setAttribute("pMap",pMap);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
