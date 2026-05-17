package com.dismai.filter.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dismai.context.ContextHandler;
import com.dismai.enums.BaseCode;
import com.dismai.exception.DismaiFrameException;
import com.dismai.filter.AbstractServerFilter;
import com.dismai.threadlocal.BaseParameterHolder;
import com.dismai.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.dismai.constant.Constant.GRAY_FLAG_FALSE;
import static com.dismai.constant.Constant.GRAY_FLAG_TRUE;
import static com.dismai.constant.Constant.GRAY_PARAMETER;
import static com.dismai.constant.Constant.SERVER_GRAY;

@Slf4j
public class ServerGrayFilter extends AbstractServerFilter {
    
    /**
     * 此服务的灰度标识
     * */
    @Value(SERVER_GRAY)
    private String serverGray;
    
    private final ContextHandler contextHandler;
    
    private final Map<String,String> map = new HashMap<>();
    
    public ServerGrayFilter(ContextHandler contextHandler){
        this.contextHandler = contextHandler;
        this.map.put(GRAY_FLAG_FALSE, GRAY_FLAG_FALSE);
        this.map.put(GRAY_FLAG_TRUE, GRAY_FLAG_TRUE);
    }
    

    @Override
    public boolean doFilter(List<? extends ServiceInstance> servers, ServiceInstance server) {
        boolean result;
        try {
            String grayFromRequest = getRequestGray();
            String grayFromMetaData = getServerGray(server);
            result = grayFromMetaData.equalsIgnoreCase(grayFromRequest);

            if (!result && grayFromRequest.equalsIgnoreCase(GRAY_FLAG_TRUE)) {
                if (CollectionUtil.isEmpty(servers)) {
                    throw new DismaiFrameException(BaseCode.SERVER_LIST_NOT_EXIST);
                }
                if(!hasGrayServer(servers)) {
                    result = true;
                }
            }
        }catch (Exception e) {
            result = false;
            log.error("CustomAwarePredicate#apply error",e);
        }
        return result;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private String getRequestGray() {
        String grayFromRequest = Optional.ofNullable(contextHandler.getValueFromHeader(GRAY_PARAMETER))
                .filter(StringUtil::isNotEmpty)
                .orElseGet(() -> BaseParameterHolder.getParameter(GRAY_PARAMETER));
        return normalizeGray(Optional.ofNullable(grayFromRequest).filter(StringUtil::isNotEmpty).orElse(serverGray));
    }

    private String getServerGray(ServiceInstance server) {
        String grayFromMetaData = Optional.ofNullable(server.getMetadata())
                .filter(CollectionUtil::isNotEmpty)
                .map(metadata -> metadata.get(GRAY_PARAMETER))
                .filter(StringUtil::isNotEmpty)
                .orElse(GRAY_FLAG_FALSE);
        return normalizeGray(grayFromMetaData);
    }

    private String normalizeGray(String gray) {
        if (StringUtil.isEmpty(gray)) {
            return GRAY_FLAG_FALSE;
        }
        return Optional.ofNullable(map.get(gray.toLowerCase(Locale.ROOT))).orElse(GRAY_FLAG_FALSE);
    }

    private boolean hasGrayServer(List<? extends ServiceInstance> servers) {
        return servers.stream()
                .map(this::getServerGray)
                .anyMatch(gray -> Objects.equals(gray, GRAY_FLAG_TRUE));
    }
}
