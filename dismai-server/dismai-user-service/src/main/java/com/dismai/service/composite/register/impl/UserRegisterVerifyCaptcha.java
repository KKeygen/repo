package com.dismai.service.composite.register.impl;

import com.dismai.captcha.model.common.ResponseModel;
import com.dismai.captcha.model.vo.CaptchaVO;
import com.dismai.core.RedisKeyManage;
import com.dismai.util.StringUtil;
import com.dismai.dto.UserRegisterDto;
import com.dismai.enums.BaseCode;
import com.dismai.enums.VerifyCaptcha;
import com.dismai.exception.DismaiFrameException;
import com.dismai.redis.RedisCache;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.service.CaptchaHandle;
import com.dismai.service.composite.register.AbstractUserRegisterCheckHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRegisterVerifyCaptcha extends AbstractUserRegisterCheckHandler {
    
    @Autowired
    private CaptchaHandle captchaHandle;
    
    @Autowired
    private RedisCache redisCache;
    
    @Override
    protected void execute(UserRegisterDto param) {
        String password = param.getPassword();
        String confirmPassword = param.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new DismaiFrameException(BaseCode.TWO_PASSWORDS_DIFFERENT);
        }
        String verifyCaptcha = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.VERIFY_CAPTCHA_ID,param.getCaptchaId()), String.class);
        if (StringUtil.isEmpty(verifyCaptcha)) {
            throw new DismaiFrameException(BaseCode.VERIFY_CAPTCHA_ID_NOT_EXIST);
        }
        if (VerifyCaptcha.YES.getValue().equals(verifyCaptcha)) {
            if (StringUtil.isEmpty(param.getCaptchaVerification())) {
                throw new DismaiFrameException(BaseCode.VERIFY_CAPTCHA_EMPTY);
            }
            log.info("传入的captchaVerification:{}",param.getCaptchaVerification());
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaVerification(param.getCaptchaVerification());
            ResponseModel responseModel = captchaHandle.verification(captchaVO);
            if (!responseModel.isSuccess()) {
                throw new DismaiFrameException(responseModel.getRepCode(),responseModel.getRepMsg());
            }
        }
    }
    
    @Override
    public Integer executeParentOrder() {
        return 0;
    }
    
    @Override
    public Integer executeTier() {
        return 1;
    }
    
    @Override
    public Integer executeOrder() {
        return 1;
    }
}
