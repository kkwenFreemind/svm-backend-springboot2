package com.svm.backend.security.component;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Iterator;

/**
 * 動態權限决策管理器，用於判斷用户是否有訪問權限
 *
 * @author Kevin Chang
 */
@Slf4j
public class DynamicAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {

        log.info("call: {}", object.toString());

        // 當接口未被配置資源時直接放行
        if (CollUtil.isEmpty(configAttributes)) {
            return;
        }

        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //將訪問所需資源或用户擁有資源進行比對

            String needAuthority = configAttribute.getAttribute();
            log.info("needAuthority: {}", needAuthority);

            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
                    log.info("===>{}", grantedAuthority.getAuthority());
                    return;
                }
            }
        }
        throw new AccessDeniedException("抱歉，您没有訪問權限");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
