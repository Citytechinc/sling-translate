package com.icfolson.sling.translate.example.annotations;

import com.icfolson.sling.translate.api.processor.TranslationDictionaryProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom annotation that allows specification of a default "en" value for a translation.  See
 * {@link TranslationDictionaryProcessor} for annotation processing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface En {

    String LOCALE = "en";

    /**
     * @return the default message for the "en" locale
     */
    String value();
}
