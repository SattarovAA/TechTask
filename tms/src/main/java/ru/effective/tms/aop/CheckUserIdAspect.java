package ru.effective.tms.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import ru.effective.tms.exception.AccessException;
import ru.effective.tms.exception.InnerException;
import ru.effective.tms.model.entity.security.AppUserDetails;
import ru.effective.tms.model.entity.security.RoleType;
import ru.effective.tms.service.CommentService;
import ru.effective.tms.service.TaskService;
import ru.effective.tms.service.UserService;

import java.util.Arrays;
import java.util.Map;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class CheckUserIdAspect {
    /**
     * Service to get owner id.
     */
    private final UserService userService;
    /**
     * Service to search owner id in {@link #userService}.
     */
    private final TaskService taskService;
    /**
     * Service to search owner id in {@link #userService}.
     */
    private final CommentService commentService;

    @Before(value = "@annotation(param)")
    public void beforeCheckUserIdPrivacy(CheckUserIdPrivacy param) {
        if (isAuthoritiesHasAnyRole(param.alwaysAccessRoles())) {
            return;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request =
                ((ServletRequestAttributes) requestAttributes).getRequest();
        Map<String, String> pathVariable =
                (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long pathId = Long.valueOf(pathVariable.get("id"));
        Long ownerUserId;
        switch (param.entityType()) {
            case USER -> ownerUserId = userService.findById(pathId).getId();
            case TASK -> ownerUserId = taskService.findById(pathId)
                    .getAuthor()
                    .getId();
            case COMMENT -> ownerUserId = commentService.findById(pathId)
                    .getAuthor()
                    .getId();
            case NOT_FOUND -> throw new InnerException("Inner Exception entityType:NOT_FOUND");
            default -> throw new InnerException("Inner Exception entityType:Unsupported type");
        }
        Long currenUserId = getCurrentUserId();

        if (!currenUserId.equals(ownerUserId)) {
            throw new AccessException("This is not accessible to the current user.");
        }
    }

    private Long getCurrentUserId() {
        AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUserId();
    }

    private boolean isAuthoritiesHasAnyRole(RoleType[] roles) {
        var authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        return Arrays.stream(roles).anyMatch(e ->
                authorities.contains(new SimpleGrantedAuthority(e.toString()))
        );
    }
}
