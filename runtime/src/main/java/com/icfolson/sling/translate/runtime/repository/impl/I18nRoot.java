package com.icfolson.sling.translate.runtime.repository.impl;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class I18nRoot {

    private static final Logger LOG = LoggerFactory.getLogger(I18nRoot.class);

    private final Resource resource;
    private final Map<String, LanguageRoot> languages = new HashMap<>();

    public I18nRoot(final Resource resource) {
        this.resource = resource;
        for (final Resource child : resource.getChildren()) {
            final LanguageRoot languageRoot = new LanguageRoot(child);
            final String localeId = languageRoot.getLocaleId();
            if (StringUtils.isNotEmpty(localeId)) {
                languages.put(localeId, languageRoot);
            }
        }
    }

    public LanguageRoot getLanguage(final String localeId) {
        LanguageRoot languageRoot = languages.get(localeId);
        if (languageRoot == null) {
            languageRoot = createLanguage(localeId);
            languages.put(localeId, languageRoot);
        }
        return languageRoot;
    }

    private LanguageRoot createLanguage(final String localeId) {
        final ResourceResolver resolver = resource.getResourceResolver();
        final Map<String, Object> props = Maps.newHashMap();
        props.put("jcr:primaryType", "nt:unstructured");
        props.put("jcr:mixinTypes", new String[]{"mix:language"});
        props.put("jcr:language", localeId);
        try {
            final Resource languageResource = resolver.create(resource, localeId, props);
            return new LanguageRoot(languageResource);
        } catch (PersistenceException e) {
            LOG.error("Error creating language node", e);
            throw new IllegalStateException(e);
        }
    }

}
