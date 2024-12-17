package ru.effective.tms.aop;

import ru.effective.tms.model.aop.EntityType;
import ru.effective.tms.model.entity.security.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserIdPrivacy {
    EntityType entityType();
    RoleType[] alwaysAccessRoles();
}
