package com.icfolson.sling.translate.runtime.repository.impl;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

public class TranslationNode {

    private final Resource resource;

    public TranslationNode(final Resource resource) {
        this.resource = resource;
    }

    public String getTranslationKey() {
        final ValueMap valueMap = ResourceUtil.getValueMap(resource);
        return valueMap.get("sling:key", String.class);
    }

    public void setMessage(final String message) {
        final ModifiableValueMap valueMap = resource.adaptTo(ModifiableValueMap.class);
        valueMap.put("sling:message", message);
    }

}
