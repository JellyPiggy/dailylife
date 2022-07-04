package com.jide.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.jide.reggie.common.BaseContext;
import com.jide.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 晓蝈
 * @version 1.0
 */
@Slf4j
@WebFilter("/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //1. 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        //定义不需要处理的路径
        String[] uris = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2. 判断本次请求, 是否需要登录, 才可以访问
        boolean check = check(uris, requestURI);
        //3. 如果不需要，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            chain.doFilter(request, response);
            return;
        }
        //4-1. 判断后台管理用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
            log.info("用户已登录，用户id为：{}", empId);

            //通过写的工具类往ThreadLocal为当前线程提供的空间中放入当前登录人员的id
            BaseContext.setCurrentId(empId);

            chain.doFilter(request, response);
            return;
        }

        //4-2. 判断移动端用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
            log.info("移动端用户登录，用户id为：{}", userId);

            //通过写的工具类往ThreadLocal为当前线程提供的空间中放入当前登录人员的id
            BaseContext.setCurrentId(userId);

            chain.doFilter(request, response);
            return;
        }
        //5. 如果未登录, 则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @return
     */
    public boolean check(String[] uris, String requestUri) {
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestUri);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
