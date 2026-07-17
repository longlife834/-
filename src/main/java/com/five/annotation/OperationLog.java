package com.five.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /** 操作模块，如：用户管理、分类管理 */
    String module();
    /** 操作类型，如：新增、修改、删除、启用禁用 */
    String operation();
    /** 操作描述，支持 SpEL 表达式，如：'删除用户 #id' */
    String description() default "";
}
