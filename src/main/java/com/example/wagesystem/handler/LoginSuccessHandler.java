package com.example.wagesystem.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public LoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }


    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        HttpSession session = request.getSession();
        if (session != null) {
            System.out.println("이거 실행됨?");
            String redirectUrl = (String) session.getAttribute("prevPage");
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            System.out.println("직급은? = " + authorities);
            if(authorities.contains("ROLE_MANAGER")){
                return "/admin/main";
            }
            if (redirectUrl != null) {
                session.removeAttribute("prevPage");
                return redirectUrl; // 이전 페이지로 리디렉션
            } else {
               if (authorities.contains("ROLE_USER")) {
                    System.out.println("아 설마");
                    return "/main/index"; // 일반 사용자로 로그인한 경우 사용자용 첫 화면으로 리디렉션
                }
            }
        }
        System.out.println("안된듯");

        // 세션이 없거나 기타 상황에서 기본 URL로 리디렉션
        return super.determineTargetUrl(request, response);
    }

}
