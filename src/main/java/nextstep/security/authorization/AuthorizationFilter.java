package nextstep.security.authorization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nextstep.security.authentication.Authentication;
import nextstep.security.authorization.manager.RequestMatcherDelegatingAuthorizationManager;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthorizationFilter extends OncePerRequestFilter {

    private final RequestMatcherDelegatingAuthorizationManager authorizationManager;

    public AuthorizationFilter(RequestMatcherDelegatingAuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = getAuthentication(request);

            AuthorizationDecision authorizationDecision = authorizationManager.check(authentication,
                    request);

            if (!authorizationDecision.isGranted()) {
                throw new AccessDeniedException();
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private static Authentication getAuthentication(HttpServletRequest request) {
        // 시큐리티 컨텍스트를 통해 인증 객체를 가져온다.
        SecurityContext securityContext = SecurityContextHolder.getContext();

        // 가져온 객체에 권한이 있는지 체크한다.
        return securityContext.getAuthentication();
    }

}
