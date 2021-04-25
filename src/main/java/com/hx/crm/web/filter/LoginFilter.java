package com.hx.crm.web.filter;


import com.hx.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入到验证登录的过滤器了");
        HttpServletRequest request= (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) resp;
        if("/login.jsp".equals(request.getServletPath())||"/user/login.do".equals(request.getServletPath())){
            chain.doFilter(req,resp);
        }else{
            HttpSession session=request.getSession();
            User user= (User) session.getAttribute("user");
            if(user!=null){
                chain.doFilter(req,resp);
            }else {
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }

        }

    }

    @Override
    public void destroy() {

    }
}
