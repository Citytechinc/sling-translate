package com.icfolson.sling.translate.example.dictionaries;

import com.icfolson.sling.translate.api.annotation.DefaultTranslation;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.api.annotation.Translation;

@TranslationDictionary
public interface DictionaryTwo {

    String getTestTranslationOne();

    @Translation(value = "testTranslationTWOO", defaults = @DefaultTranslation(locale = "en", value = "2222222"))
    String getTestTranslationTwo();

    @Translation(value = "testTranslation3", defaults = @DefaultTranslation(locale = "en", value = "Variable {0} Variable"))
    String getTestTranslationThree(final String variable);

}
