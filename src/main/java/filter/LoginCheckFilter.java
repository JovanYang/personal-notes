package filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginCheckFilter implements Filter {
    private List<String> excludedUrls;

    @Override
    public void init(FilterConfig filterConfig) {
        String excludedUrlsStr = filterConfig.getInitParameter("excludedUrls");
        excludedUrls = Arrays.asList(excludedUrlsStr.split(","));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // 检查是否是不需要登录的URL
        if (isExcludedUrl(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 检查用户是否已登录
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/login.jsp");
        }
    }

    private boolean isExcludedUrl(String path) {
        return excludedUrls.stream().anyMatch(path::startsWith);
    }

    @Override
    public void destroy() {
    }
} 