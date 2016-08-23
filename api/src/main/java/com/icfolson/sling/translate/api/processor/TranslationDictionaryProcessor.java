package com.icfolson.sling.translate.api.processor;

import com.icfolson.sling.translate.api.model.DictionaryModel;

/**
 * Interface used to configure {@link DictionaryModel} instances from annotated classes.  The runtime bundle supplies
 * its own default implementation leveraging the OOTB annotations.  Bundles can supply their own implementations for
 * additional processing (e.g., an @En annotation might be used to define a default english translation), by using the
 * {@code Sling-Translate-Processors} bundle header with a comma-separated list of processor class names.
 */
public interface TranslationDictionaryProcessor {

    /**
     * Process the provided model, based on the annotated class exposed by {@link DictionaryModel#getDictionaryClass()}.
     * @param model
     */
    void process(DictionaryModel model);

}
