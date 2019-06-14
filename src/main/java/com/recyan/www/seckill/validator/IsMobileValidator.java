package com.recyan.www.seckill.validator;

import com.recyan.www.seckill.util.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//IsMobile, String [注解名称与注解验证的类型]
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {


    private boolean required = false;

    //在验证器初始化的时候被调用的 即验证前判断是否手机号是必填的
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        //@Ismobile
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (required) {
            //校验值
            return ValidatorUtil.isMobile(value);
        } else {
            //非必填的。可为空
            if (StringUtils.isEmpty(value)) {
                return true;
            } else {
                //非必填，但是不空的情况下还是需要验证
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
