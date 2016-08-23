package com.icfolson.sling.translate.api.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The model used for decomposing an annotated dictionary class into a series of entries defined by methods defined in
 * the class
 */
public class DictionaryModel {

    private final Class<?> dictionaryClass;
    private final Map<Method, TranslationModel> entries = new HashMap<Method, TranslationModel>();

    public DictionaryModel(final Class<?> dictionaryClass) {
        this.dictionaryClass = dictionaryClass;
    }

    public Class<?> getDictionaryClass() {
        return dictionaryClass;
    }

    public TranslationModel addEntry(TranslationModel model) {
        entries.put(model.getEntryMethod(), model);
        return model;
    }

    public TranslationModel getEntry(Method method) {
        return entries.get(method);
    }

    public TranslationModel removeEntry(Method method) {
        return entries.remove(method);
    }

    public boolean hasEntries() {
        return !entries.isEmpty();
    }

    public List<TranslationModel> getEntries() {
        return new ArrayList<TranslationModel>(entries.values());
    }
}
