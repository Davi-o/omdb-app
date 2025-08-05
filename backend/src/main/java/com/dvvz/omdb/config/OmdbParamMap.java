package com.dvvz.omdb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "omdb")
public class OmdbParamMap {

    private Map<String, String> paramMapping;

    public Map<String, String> getParamMapping() {
        return paramMapping;
    }

    public void setParamMapping(Map<String, String> paramMapping) {
        this.paramMapping = paramMapping;
    }
}
