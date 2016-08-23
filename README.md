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

## Requirements

* AEM 6.2
