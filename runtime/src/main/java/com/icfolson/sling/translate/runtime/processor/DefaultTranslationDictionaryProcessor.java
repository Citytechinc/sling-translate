package com.icfolson.sling.translate.runtime.processor;

import com.icfolson.sling.translate.api.annotation.DefaultTranslation;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.api.annotation.Translation;
import com.icfolson.sling.translate.api.model.TranslationModel;
import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.processor.TranslationDictionaryProcessor;
import com.icfolson.sling.translate.runtime.util.MethodUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * The default {@link TranslationDictionaryProcessor} that generates models based on the OOTB annotations
 * {@link TranslationDictionary}, {@link Translation}, and {@link DefaultTranslation}
 */
public class DefaultTranslationDictionaryProcessor implements TranslationDictionaryProcessor {

    public void process(final DictionaryModel model) {
        final Class<?> dictionaryClass = model.getDictionaryClass();
        TranslationDictionary annotation = dictionaryClass.getAnnotation(TranslationDictionary.class);
        if (annotation != null) {
            boolean ignoreUnannotated = annotation.ignoreUnannotated();
            for (final Method method : dictionaryClass.getMethods()) {
                TranslationModel entry = process(method, ignoreUnannotated);
                if (entry != null) {
                    model.addEntry(entry);
                }
            }
        }
    }

    private TranslationModel process(Method method, boolean ignoreUnannotated) {
        if (!isValidTranslationMethod(method, ignoreUnannotated)) {
            return null;
        }
        TranslationWrapper annotation = new TranslationWrapper(method);
        TranslationModel model = new TranslationModel(method);
        model.setTranslationKey(annotation.value());
        model.setComment(annotation.comment());
        for (DefaultTranslation defaultTranslation: annotation.defaults()) {
            model.getDefaultValues().put(defaultTranslation.locale(), defaultTranslation.value());
        }
        return model;
    }

    private static boolean isValidTranslationMethod(Method method, boolean ignoreUnannotated) {
        return String.class.isAssignableFrom(method.getReturnType())
            && method.getName().matches(MethodUtil.GETTER)
            && (!ignoreUnannotated || method.getAnnotation(Translation.class) != null);
    }

    private static class TranslationWrapper implements Translation {

        private final Method method;
        private final Translation annotation;

        private TranslationWrapper(final Method method) {
            this.method = method;
            this.annotation = method.getAnnotation(Translation.class);
        }

        public String value() {
            String key = annotation != null ? annotation.value() : null;
            key = key != null ? key : MethodUtil.getGetterPropertyName(method.getName());
            return key;
        }

        @Override
        public String comment() {
            return annotation != null ? annotation.comment() : null;
        }

        public DefaultTranslation[] defaults() {
            return annotation != null ? annotation.defaults() : new DefaultTranslation[0];
        }

        public Class<? extends Annotation> annotationType() {
            return Translation.class;
        }

        public boolean isAnnotated() {
            return annotation != null;
        }
    }
}
