package com.icfolson.sling.translate.example.dictionaries;

import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.example.annotations.En;

@TranslationDictionary
public interface PersonalizedTranslations {

    @En("Hello, {0}")
    String getHelloUser(final String userName);

}
