package cn.com.biz.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一字符编码, 过滤除静态资源外的所有请求
 * @author liuchengrui
 * @date 2020/11/28 23:07
 */
@WebFilter(filterName = "encodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) { }


    /**
     * <p>检验request和response是否符合Http规范，不符合则抛出异常，
     * 1. 如果请求url和"^/static/.*"匹配则直接放行，
     * 2. 设置请求和响应字符编码为'UTF-8'，放行</p>
     * @param servletRequest 请求
     * @param servletResponse 响应
     * @param filterChain 过滤器链
     * @throws IOException doFilter和getWriter抛出IO异常
     * @throws ServletException 请求和响应不符合Http要求抛出异常
     * @author liuchengrui
     * @date 2020/11/28 23:08
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest)) {
            throw new ServletException(servletRequest + " not HttpServletRequest");
        } else if (!(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(servletResponse + " not HttpServletResponse");
        }
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        String uri = req.getRequestURI();
        // 非静态资源 设置编码
        String urlRegx = "^/static/.*";
        if (!uri.matches(urlRegx)) {
            req.setCharacterEncoding("UTF-8");
            resp.setCharacterEncoding("UTF-8");
        }
        filterChain.doFilter(req, resp);
    }

    @Override
    public void destroy() { }
}
