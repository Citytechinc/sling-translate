package com.icfolson.sling.translate.example.processor;

import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.model.TranslationModel;
import com.icfolson.sling.translate.api.processor.TranslationDictionaryProcessor;

public class ExampleProcessor implements TranslationDictionaryProcessor {

    @Override
    public void process(final DictionaryModel model) {
        for (final TranslationModel translationModel : model.getEntries()) {
            translationModel.setComment("Example comment.");
        }
    }
}
