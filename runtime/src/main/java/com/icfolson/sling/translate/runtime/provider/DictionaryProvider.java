package com.icfolson.sling.translate.runtime.provider;

import com.icfolson.sling.translate.api.model.DictionaryModel;

import java.util.List;

public interface DictionaryProvider {

    String getI18nRootPath();

    List<DictionaryModel> getDictionaries();

}
