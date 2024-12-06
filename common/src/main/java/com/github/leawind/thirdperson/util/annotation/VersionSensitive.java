package com.github.leawind.thirdperson.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于标记版本敏感内容
 *
 * <p>有些内容很可能需要随着Minecraft版本更新而更新。
 *
 * <p>对这类内容使用此注解，以便在将此模组移植到其他MC版本时检查相关内容。
 *
 * <p>通常应当对实现方法，而非接口方法使用此注解。
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.SOURCE)
public @interface VersionSensitive {
  String value() default "";

  String since() default "";

  String until() default "";
}
