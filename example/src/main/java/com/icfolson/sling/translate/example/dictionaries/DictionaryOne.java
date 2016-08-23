package com.icfolson.sling.translate.example.dictionaries;

import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.api.annotation.Translation;

@TranslationDictionary
public interface DictionaryOne {

    String getTestTranslationOne();

    @Translation("testTranslation2")
    String getTestTranslationTwo();

}
