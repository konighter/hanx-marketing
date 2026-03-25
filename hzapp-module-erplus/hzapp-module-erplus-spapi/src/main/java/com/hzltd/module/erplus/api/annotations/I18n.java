package com.hzltd.module.erplus.api.annotations;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;


@Target({TYPE, FIELD, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18n {

}
