package com.icfolson.sling.translate.runtime.repository;

import com.icfolson.sling.translate.api.model.DictionaryModel;

public interface DictionaryWriter {

    void writeDictionary(final String i18nPath, final DictionaryModel dictionary);

}
