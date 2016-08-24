package com.icfolson.sling.translate.runtime.repository.impl;

import com.day.cq.commons.jcr.JcrUtil;
import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.api.model.TranslationModel;
import com.icfolson.sling.translate.runtime.provider.DictionaryProvider;
import com.icfolson.sling.translate.runtime.repository.DictionaryWriter;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component(immediate = true, label = "Sling Translate: Dictionary Writer")
public class DictionaryWriterImpl implements DictionaryWriter {

    private static final String OVERWRITE = "OVERWRITE";
    private static final String MERGE = "MERGE";
    private static final String NONE = "NONE";

    @Property(label = "Write Mode", description = "How default values should be written. OVERWRITE causes all default "
        + "values associated with bundles to be written, regardless of existence. MERGE will write default values only "
        + "when they don't already exist. NONE will never write default values to the repository.", options = {
        @PropertyOption(value = MERGE, name = MERGE),
        @PropertyOption(value = OVERWRITE, name = OVERWRITE),
        @PropertyOption(value = NONE, name = NONE),
    })
    private static final String WRITE_MODE_PROP = "write.mode";

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryWriterImpl.class);

    private static final String FOLDER_TYPE = "sling:Folder";

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
        referenceInterface = DictionaryProvider.class, bind = "bindDictionaryProvider")
    private List<DictionaryProvider> dictionaryProviders = new ArrayList<>();

    private WriteMode writeMode = WriteMode.MERGE;

    @Override
    public void writeDictionary(final String i18nPath, final DictionaryModel dictionary) {
        if (writeMode == WriteMode.NONE) {
            LOG.info("Skipping dictionary write because write mode = NONE");
            return;
        }
        ResourceResolver resolver = null;
        try {
            resolver = resolverFactory.getAdministrativeResourceResolver(null); //TODO
            final Resource i18NResource = getOrCreateFolder(i18nPath, resolver);
            if (i18NResource == null) {
                LOG.error("Unable to create i18n folder node.  Translations will not be saved.");
            }
            final I18nRoot i18nRoot = new I18nRoot(i18NResource);
            for (final TranslationModel translationModel : dictionary.getEntries()) {
                final String translationKey = translationModel.getTranslationKey();
                for (final Map.Entry<String, String> defaultValue : translationModel.getDefaultValues().entrySet()) {
                    final String localeId = defaultValue.getKey();
                    final String message = defaultValue.getValue();
                    final LanguageRoot languageRoot = i18nRoot.getLanguage(localeId);
                    TranslationNode translationNode = languageRoot.getTranslation(translationKey);
                    if (translationNode == null || writeMode == WriteMode.OVERWRITE) {
                        translationNode = languageRoot.createTranslation(translationKey);
                        translationNode.setMessage(message);
                    }
                }
            }
            resolver.commit();
        } catch (LoginException e) {
            LOG.error("Error logging in to repository", e);
        } catch (PersistenceException e) {
            LOG.error("Error committing changes to repository", e);
        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    @Activate
    @Modified
    protected final void activate(final Map<String, Object> props) {
        writeMode = WriteMode.fromName(PropertiesUtil.toString(props.get(WRITE_MODE_PROP), MERGE));
    }

    protected void bindDictionaryProvider(final DictionaryProvider dictionaryProvider) {
        dictionaryProviders.add(dictionaryProvider);
        final String i18nRootPath = dictionaryProvider.getI18nRootPath();
        final List<DictionaryModel> dictionaries = dictionaryProvider.getDictionaries();
        if (i18nRootPath != null && dictionaries != null) {
            for (final DictionaryModel dictionary : dictionaries) {
                writeDictionary(i18nRootPath, dictionary);
            }
        }
    }

    protected void unbindDictionaryProvider(final DictionaryProvider dictionaryProvider) {
        dictionaryProviders.remove(dictionaryProvider);
    }

    private Resource getOrCreateFolder(final String path, final ResourceResolver resolver) {
        Resource resource = resolver.resolve(path);
        while (ResourceUtil.isNonExistingResource(resource)) {
            resource = getOrCreateFolder(ResourceUtil.getParent(path), resolver);
        }
        if (!path.equals(resource.getPath())) {
            final Session session = resolver.adaptTo(Session.class);
            try {
                JcrUtil.createPath(path, false, FOLDER_TYPE, FOLDER_TYPE, session, true);
            } catch (RepositoryException e) {
                LOG.error("Unable to create folder node", e);
            }
        }
        return resolver.resolve(path);
    }

    private enum WriteMode {
        MERGE,
        OVERWRITE,
        NONE;

        private static final Map<String, WriteMode> BY_NAME = new HashMap<>();
        static {
        	for (WriteMode value: values()) {
        		BY_NAME.put(value.name(), value);
        	}
        }

        public static WriteMode fromName(final String name) {
        	return BY_NAME.get(name);
        }
    }

}
