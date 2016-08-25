package com.icfolson.sling.translate.example.dictionaries;

import com.icfolson.sling.translate.api.annotation.Translation;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.example.annotations.En;

@TranslationDictionary
public interface DialogTranslations {

    @Translation("OK")
    @En("OK")
    String getOK();

    @En("Cancel")
    String getCancel();

}
