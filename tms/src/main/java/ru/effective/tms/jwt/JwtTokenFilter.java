package ru.effective.tms.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.effective.tms.model.entity.security.AppUserDetails;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    /**
     * jwtUtils for working with jwt token.
     */
    private final JwtUtils jwtUtils;
    /**
     * To load user by username from {@link AppUserDetails}.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Authenticate once per request filter.
     * Use jwt token.
     *
     * @param request     {@link HttpServletRequest}
     * @param response    {@link HttpServletResponse}
     * @param filterChain security filter chain.
     * @throws ServletException for filterChain().
     * @throws IOException      for filterChain().
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().contains("/v3/api-docs")
                || request.getServletPath().contains("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwtToken = getToken(request);
            if (jwtToken != null && jwtUtils.validation(jwtToken)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwtToken);
                AppUserDetails userDetails =
                        (AppUserDetails) userDetailsService.loadUserByUsername(username);
                jwtUtils.checkTokenByUserId(userDetails.getUserId());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Get jwt token from {@link HttpServletRequest}.
     *
     * @param request {@link HttpServletRequest}
     * @return jwt token.
     */
    private String getToken(HttpServletRequest request) {
        int bearerTokenLength = 7;
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(bearerTokenLength);
        }
        return null;
    }
}
