package com.icfolson.sling.translate.example.dictionaries;

import com.icfolson.sling.translate.api.annotation.Translation;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.example.annotations.En;

@TranslationDictionary
public interface EnthusiasticDialogTranslations extends DialogTranslations {

    @Translation("excellent!")
    @En("That's Excellent!!!!!!!")
    String getOK();

    @En("No Way!!!")
    String getNoWay();

}
