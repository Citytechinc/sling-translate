package com.icfolson.sling.translate.runtime.domain;

import com.day.cq.i18n.I18n;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.model.TranslationModel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Dynamic proxy used to provide implementations to {@link TranslationDictionary}-annotated interfaces
 */
public class TranslationDictionaryDynamicProxy implements InvocationHandler {

    private final DictionaryModel model;
    private final I18n i18n;
    private final Locale locale;

    public TranslationDictionaryDynamicProxy(final DictionaryModel model, ResourceBundle resourceBundle) {
        this.model = model;
        this.i18n = new I18n(resourceBundle);
        this.locale = resourceBundle.getLocale();
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        TranslationModel entryModel = this.model.getEntry(method);
        String value = null;
        if (entryModel != null) {
            value = i18n.get(entryModel.getTranslationKey(), entryModel.getComment(), args);
            if (value == null || entryModel.getTranslationKey().equals(value)) {
                // TODO Sloppy. Need general util for fallback through enclosing locales: en-US -> en
                String var = entryModel.getDefaultValues().get(locale.getLanguage());
                value = i18n.getVar(var, entryModel.getComment(), args);
            }
        }
        return value;
    }

    @Override
    public String toString() {
        return "TranslationDictionaryDynamicProxy{" +
            "i18n=" + i18n +
            ", model=" + model +
            ", locale=" + locale +
            '}';
    }
}
