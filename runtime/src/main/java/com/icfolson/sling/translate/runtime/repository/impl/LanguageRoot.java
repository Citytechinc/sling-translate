package com.icfolson.sling.translate.runtime.repository.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;

public class LanguageRoot {

    private static final Logger LOG = LoggerFactory.getLogger(LanguageRoot.class);

    private final Resource resource;
    private final Map<String, TranslationNode> translations = Maps.newHashMap();

    public LanguageRoot(final Resource resource) {
        this.resource = resource;
        for (final Resource child : resource.getChildren()) {
            final TranslationNode translationNode = new TranslationNode(child);
            final String translationKey = translationNode.getTranslationKey();
            if (StringUtils.isNotEmpty(translationKey)) {
                translations.put(translationKey, translationNode);
            }
        }
    }

    public String getLocaleId() {
        final ValueMap valueMap = ResourceUtil.getValueMap(resource);
        return valueMap.get("jcr:language", String.class);
    }

    public TranslationNode getTranslation(final String translationKey) {
        return translations.get(translationKey);
    }

    public TranslationNode createTranslation(final String translationKey) {
        if (translations.containsKey(translationKey)) {
            throw new IllegalStateException("Attempting to create existing translation");
        }
        TranslationNode out = doCreateTranslation(translationKey);
        translations.put(translationKey, out);
        return out;
    }

    private TranslationNode doCreateTranslation(final String translationKey) {
        final ResourceResolver resolver = resource.getResourceResolver();
        final Map<String, Object> props = Maps.newHashMap();
        props.put("jcr:primaryType", "nt:unstructured");
        props.put("jcr:mixinTypes", new String[]{"sling:Message"});
        props.put("sling:key", translationKey);
        try {
            final String name = JcrUtil.createValidChildName(resource.adaptTo(Node.class), translationKey);
            final Resource translationResource = resolver.create(resource, name, props);
            return new TranslationNode(translationResource);
        } catch (PersistenceException | RepositoryException e) {
            LOG.error("Error creating language node", e);
            throw new IllegalStateException(e);
        }
    }
}
