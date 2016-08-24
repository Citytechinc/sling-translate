package com.icfolson.sling.translate.runtime.sling;

import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.runtime.domain.TranslationDictionaryDynamicProxy;
import com.icfolson.sling.translate.runtime.provider.DictionaryProvider;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.i18n.ResourceBundleProvider;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The adapter factory used to generate dynamic proxy adapters for dictionary classes.
 */
public class TranslationAdapterFactory implements AdapterFactory, DictionaryProvider {

    private final String i18nRootPath;
    private final Map<Class<?>, DictionaryModel> models = new HashMap<>();
    private final List<ResourceBundleProvider> bundleProviders;

    public TranslationAdapterFactory(final String i18nRootPath, final List<DictionaryModel> models,
        final List<ResourceBundleProvider> bundleProviders) {

        this.i18nRootPath = i18nRootPath;
        for (final DictionaryModel model : models) {
            this.models.put(model.getDictionaryClass(), model);
        }
        this.bundleProviders = bundleProviders;
    }

    @Override
    public <AdapterType> AdapterType getAdapter(final Object adaptable, final Class<AdapterType> type) {
        return getTranslation(adaptable, type);
    }

    public <T> T getTranslation(final Object adaptable, final Class<T> translationClass) {
        final DictionaryModel model = models.get(translationClass);
        final Locale locale = getLocale(adaptable);
        if (model == null || locale == null) {
            return null;
        }
        final ResourceBundle resourceBundle = getResourceBundle(locale);
        @SuppressWarnings("unchecked")
        T out = (T) Proxy.newProxyInstance(translationClass.getClassLoader(), new Class<?>[] {translationClass},
            new TranslationDictionaryDynamicProxy(model, resourceBundle));
        return out;
    }

    private Locale getLocale(final Object adaptable) {
        if (adaptable instanceof SlingHttpServletRequest) {
            // OOTB support for SlingHttpServletRequest
            return ((SlingHttpServletRequest) adaptable).getLocale();
        } else if (adaptable instanceof Adaptable) {
            return ((Adaptable) adaptable).adaptTo(Locale.class);
        }
        return null;
    }

    private ResourceBundle getResourceBundle(final Locale locale) {
        ResourceBundle resourceBundle = null;
        for (final ResourceBundleProvider resourceBundleProvider : bundleProviders) {
            if (resourceBundle == null) {
                resourceBundle = resourceBundleProvider.getResourceBundle(locale);
            }
        }
        return resourceBundle;
    }

    @Override
    public String getI18nRootPath() {
        return i18nRootPath;
    }

    @Override
    public List<DictionaryModel> getDictionaries() {
        return new ArrayList<>(models.values());
    }
}
