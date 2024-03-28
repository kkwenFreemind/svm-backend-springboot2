package com.svm.backend.security.component;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * 動態權限相關業務類
 *
 * @author Kevin Chang
 */
public interface DynamicSecurityService {

    /**
     * 加载資源ANT通配符合資源对应MAP
     *
     * @return
     */
    Map<String, ConfigAttribute> loadDataSource();
}
