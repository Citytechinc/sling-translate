package com.icfolson.sling.translate.api.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The model used to represent a translation entry, as defined by a method in an annotated dictionary class
 */
public class TranslationModel {

    private final Method entryMethod;
    private final Map<String, String> defaultValues = new HashMap<String, String>();
    private String translationKey;
    private String comment;

    public TranslationModel(final Method entryMethod) {
        this.entryMethod = entryMethod;
    }

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    public Method getEntryMethod() {
        return entryMethod;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public void setTranslationKey(final String translationKey) {
        this.translationKey = translationKey;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
