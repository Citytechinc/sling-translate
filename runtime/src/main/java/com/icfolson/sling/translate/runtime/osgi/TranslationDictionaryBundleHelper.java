package com.icfolson.sling.translate.runtime.osgi;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.icfolson.sling.translate.api.annotation.TranslationDictionary;
import com.icfolson.sling.translate.api.constants.BundleHeaders;
import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.processor.TranslationDictionaryProcessor;
import com.icfolson.sling.translate.runtime.processor.DefaultTranslationDictionaryProcessor;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

/**
 * Helper class for processing bundle translation data
 */
public class TranslationDictionaryBundleHelper {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationDictionaryBundleHelper.class);

    private final Bundle bundle;
    private final List<TranslationDictionaryProcessor> processors = new ArrayList<>();
    private final List<DictionaryModel> models = new ArrayList<>();
    private final List<String> localeAdaptableClassNames = new ArrayList<>();

    public TranslationDictionaryBundleHelper(final Bundle bundle) {
        this.bundle = bundle;
        Dictionary<String, String> headers = bundle.getHeaders();

        // initialize processing queue
        processors.add(new DefaultTranslationDictionaryProcessor()); // always executed before custom processors
        String processorsHeader = headers.get(BundleHeaders.SLING_TRANSLATE_PROCESSORS);
        if (processorsHeader != null) {
            String[] classArray = getEntries(processorsHeader);
            for (final String className : classArray) {
                TranslationDictionaryProcessor processor = loadProcessor(className);
                if (processor != null) {
                    this.processors.add(processor);
                }
            }
        }

        // load dictionaries
        String packagesHeader = headers.get(BundleHeaders.SLING_TRANSLATE_PACKAGES);
        if (packagesHeader != null) {
            String[] packageArray = getEntries(packagesHeader);
            for (final String packageName : packageArray) {
                scanPackage(packageName);
            }
        }

        // process models
        for (final DictionaryModel model : models) {
            for (final TranslationDictionaryProcessor processor : processors) {
                processor.process(model);
            }
        }

        // process exposed adaptables
        String localeAdaptablesHeader = headers.get(BundleHeaders.SLING_TRANSLATE_LOCALE_ADAPTABLES);
        if (StringUtils.isNotBlank(localeAdaptablesHeader)) {
            Iterables.addAll(localeAdaptableClassNames, Splitter.on(',').trimResults().split(localeAdaptablesHeader));
        }
    }

    /**
     * @return a listing of the {@link DictionaryModel}s exposed by the bundle
     */
    public List<DictionaryModel> getModels() {
        return new ArrayList<>(models);
    }

    public List<String> getLocaleAdaptableClassNames() {
        return new ArrayList<>(localeAdaptableClassNames);
    }

    private TranslationDictionaryProcessor loadProcessor(String className) {
        try {
            Class<?> processorClass = bundle.loadClass(className);
            if (TranslationDictionaryProcessor.class.isAssignableFrom(processorClass)) {
                return (TranslationDictionaryProcessor) processorClass.newInstance();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.warn("Error loading TranslationDictionaryProcessor class " + className, e);
        }
        return null;
    }

    private void scanPackage(String packageName) {
        Enumeration<URL> classUrls = bundle.findEntries("/" + packageName.replace('.', '/'), "*.class", true);
        while (classUrls.hasMoreElements()) {
            URL url = classUrls.nextElement();
            String className = toClassName(url);
            try {
                Class<?> implType = bundle.loadClass(className);
                TranslationDictionary annotation = implType.getAnnotation(TranslationDictionary.class);
                if (annotation != null) {
                    DictionaryModel model = new DictionaryModel(implType);
                    models.add(model);
                }
            } catch (ClassNotFoundException e) {
                LOG.warn("Unable to load class", e);
            }
        }
    }

    private static String toClassName(URL url) {
        final String f = url.getFile();
        final String cn = f.substring(1, f.length() - ".class".length());
        return cn.replace('/', '.');
    }

    private static String[] getEntries(String commaSeparated) {
        String list = commaSeparated.replaceAll("\\s", "");
        return list.split(",");
    }

}
