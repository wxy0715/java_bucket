package com.cjree.core.basic.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cjree.core.cache.base.Cacheable;
import com.cjree.core.common.config.SpringContainer;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractCacheableModel extends BaseModel implements Cacheable {

    @JsonIgnore
    @Override
    public String getCacheKey() {
        String applicationName = SpringContainer.getProperty("spring.application.name");
        if (StringUtils.isEmpty(applicationName)) {
            applicationName = "default";
        }
        return applicationName + ":cache:" + getClass().getSimpleName() + ":" + getId();
    }

}
