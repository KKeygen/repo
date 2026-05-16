package com.dismai.service;

import com.alibaba.fastjson.JSONObject;
import com.dismai.core.RedisKeyManage;
import com.dismai.util.StringUtil;
import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;
import com.dismai.jwt.TokenUtil;
import com.dismai.redis.RedisCache;
import com.dismai.redis.RedisKeyBuild;
import com.dismai.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenService {
    
    @Autowired
    private RedisCache redisCache;
    
    public String parseToken(String token,String tokenSecret){
        String userStr = TokenUtil.parseToken(token,tokenSecret);
        if (StringUtil.isNotEmpty(userStr)) {
            return JSONObject.parseObject(userStr).getString("userId");
        }
        return null;
    }
    
    public UserVo getUser(String token,String code,String tokenSecret){
        UserVo userVo = null;
        String userId = parseToken(token,tokenSecret);
        if (StringUtil.isNotEmpty(userId)) {
            userVo = redisCache.get(RedisKeyBuild.createRedisKey(RedisKeyManage.USER_LOGIN, code, userId), UserVo.class);
        }
        return Optional.ofNullable(userVo).orElseThrow(() -> new DismaiFrameException(BaseCode.LOGIN_USER_NOT_EXIST));
    }
}
