# Sling Translate

[ICF Olson](http://www.icfolson.com)

## Overview

Sling Translate provides a simple way to provide translations in Sling/AEM.  It allows users to define translation "dictionaries" as annotated interfaces:

```java
@TranslationDictionary
public interface ExampleDictionary {

    @Translation("translation")
    public String getTranslation();

    // By default, generates a translation with key "translationWithInferredKey"
    public String getTranslationWithInferredKey();

    @Translation(value = "parameterizedTranslation", defaults = @DefaultTranslation(locale = "en", value = "Hi, {0}!"))
    public String getParameterizedTranslation(final String name);

}

```

At runtime, servlet requests (and other entities) can be adapted to your target interface, which will return translated values in the target locale.

```java
    public void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
        ExampleDictionary exampleDictionary = request.adaptTo(ExampleDictionary.class);
        LOG.info(exampleDictionary.getParameterizedTranslation("Bob")); // outputs "Hi, Bob!" for a request in the "en" locale
    }
```

This has some benefits over standard methodologies:

* Fewer (or no) strings.  There's no need to maintain a separate translation XML file, referencing the keys in your code wherever the translation is needed.  The key is defined once in your annotation (or inferred from your method name), and most other uses can be achieved via a method reference (IDE-support!).
* Translation data is stored in your code at its point-of-use.  There's no giant translation file shared across all components (and the merge conflicts that go with it), and there's no orphaned translations after a component is deleted.
* Translations can be easily shared between contexts, while maintaining a single point where the data is edited.  For example:

```java
@TranslationDictionary
public interface DialogTranslations {

  String getOK();
  
  String getCancel();
  
}

@TranslationDictionary
public interface EnthusiasticDialogTranslation extends DialogTranslations {

  // Adds a new translation compared to DialogTranslations
  String getCloseThisNow();

  // Changes the key for a translation existing in DialogTranslations
  @Translation("excellent!")
  String getOK();
  
}
```

## Features

### Write default translations to JCR
The simplest place to add default translations is at the point of specification -- in the code.  But that alone doesn't provide a way to export an XLIFF or other file format by use for translators.  Sling Translate provides a TranslationWriter service that writes the default translations, along with their keys and comments, to the JCR.  The OOTB can be configured to overwrite existing translations, or to add new translations while maintaining the state of existing ones.  Bundles can specify their i18n repository paths using the `Sling-Translate-I18n-Root-Path` bundle header.

### Extend translation specifications with custom processors
Bundles can create TranslationDictionaryProcessor implementations to allow custom annotation processing logic, including handling for new annotations.  Custom processors can be specified using the `Sling-Translate-Processors` bundle header.  The example bundle uses a custom @En annotation to easily add a default english translation.

### Adapt your dictionary objects from any locale-specific entity
Your custom dictionary classes can be adapted from any adaptable from which a Locale can be inferred.  OOTB support is provided for [SlingHttpServletRequest](https://sling.apache.org/apidocs/sling7/org/apache/sling/api/SlingHttpServletRequest.html) (via the `getLocale()` method), but any adaptable can potentially be used.  In order to make an adaptable eligible for Sling Translate dictionaries, first register an [AdapterFactory](https://sling.apache.org/apidocs/sling8/org/apache/sling/api/adapter/AdapterFactory.html) implementation capable of adapting to [Locale](https://docs.oracle.com/javase/8/docs/api/java/util/Locale.html) from your target adaptable.  Once that's in place, register your adaptable class using the `Sling-Translate-Locale-Adaptables` bundle header.  At runtime, Sling Translate will adapt your target class to a Locale, and use that to back translation lookup.

## Requirements

* AEM 6.2


