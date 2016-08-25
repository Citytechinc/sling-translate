package com.icfolson.sling.translate.example.processor;

import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.model.TranslationModel;
import com.icfolson.sling.translate.api.processor.TranslationDictionaryProcessor;
import com.icfolson.sling.translate.example.annotations.En;

import java.lang.reflect.Method;

/**
 * A processor that leverages the {@link En} annotation to set default english values on translation models.
 */
public class EnProcessor implements TranslationDictionaryProcessor {

    @Override
    public void process(final DictionaryModel model) {
        for (final TranslationModel translationModel : model.getEntries()) {
            Method method = translationModel.getEntryMethod();
            En en = method.getAnnotation(En.class);
            if (en != null) {
                translationModel.getDefaultValues().put(En.LOCALE, en.value());
            }
        }
    }
}
