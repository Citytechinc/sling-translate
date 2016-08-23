package com.icfolson.sling.translate.runtime.osgi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.icfolson.sling.translate.api.model.DictionaryModel;
import com.icfolson.sling.translate.runtime.sling.TranslationAdapterFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Listens for bundles exposing translation data and starts/stops corresponding {@link TranslationAdapterFactory}s
 */
public class TranslateBundleListener implements BundleTrackerCustomizer, ServiceTrackerCustomizer {

    private static final Logger LOG = LoggerFactory.getLogger(TranslateBundleListener.class);

    private static final String[] ADAPTABLES = new String[]{ SlingHttpServletRequest.class.getName() };

    private final BundleContext bundleContext;
    private final BundleTracker bundleTracker;
    private final ServiceTracker resourceBundleProviderTracker;
    private final Multimap<ServiceRegistration, String> providedAdaptablesMap = ArrayListMultimap.create();
    private final Map<ServiceRegistration, Dictionary<String, Object>> propertiesMap = new HashMap<>();
    private final List<ResourceBundleProvider> activeProviders = new ArrayList<>();


    @SuppressWarnings("unchecked")
    public TranslateBundleListener(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundleTracker = new BundleTracker(bundleContext, Bundle.ACTIVE, this);
        this.resourceBundleProviderTracker = new ServiceTracker(bundleContext, ResourceBundleProvider.class, this);
        this.bundleTracker.open();
        this.resourceBundleProviderTracker.open();
    }

    public synchronized void unregisterAll() {
        this.bundleTracker.close();
        this.resourceBundleProviderTracker.close();
    }

    public synchronized Object addingBundle(final Bundle bundle, final BundleEvent event) {
        ServiceRegistration out = null;
        TranslationDictionaryBundleHelper helper = new TranslationDictionaryBundleHelper(bundle);
        List<DictionaryModel> models = helper.getModels();
        if (!models.isEmpty()) {
            String[] adapters = new String[models.size()];
            for (int i = 0; i < models.size(); i++) {
                DictionaryModel dictionaryModel = models.get(i);
                adapters[i] = dictionaryModel.getDictionaryClass().getName();
            }
            List<String> providedAdaptables = helper.getLocaleAdaptableClassNames();
            out = registerAdapterFactory(adapters, providedAdaptables,
                new TranslationAdapterFactory(models, activeProviders));
        }
        return out;
    }

    public synchronized void modifiedBundle(final Bundle bundle, final BundleEvent event, final Object object) { }

    public synchronized void removedBundle(final Bundle bundle, final BundleEvent event, final Object object) {
        if (object instanceof ServiceRegistration) {
            ServiceRegistration serviceRegistration = (ServiceRegistration) object;
            providedAdaptablesMap.removeAll(serviceRegistration);
            propertiesMap.remove(serviceRegistration);
            serviceRegistration.unregister();
            updateAdapters();
        }
    }

    @Override
    public Object addingService(final ServiceReference reference) {
        Object service = this.resourceBundleProviderTracker.addingService(reference);
        if (service instanceof ResourceBundleProvider) {
            activeProviders.add((ResourceBundleProvider) service);
        }
        return service;
    }

    @Override
    public void modifiedService(final ServiceReference reference, final Object service) {
        resourceBundleProviderTracker.modifiedService(reference, service);
    }

    @Override
    public void removedService(final ServiceReference reference, final Object service) {
        if (service instanceof ResourceBundleProvider) {
            activeProviders.remove(service);
        }
        resourceBundleProviderTracker.removedService(reference, service);
    }

    private ServiceRegistration registerAdapterFactory(String[] adapters, List<String> providedAdaptables,
        TranslationAdapterFactory factory) {

        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(AdapterFactory.ADAPTER_CLASSES, adapters);
        properties.put(AdapterFactory.ADAPTABLE_CLASSES, getCurrentAdaptables(providedAdaptables));
        ServiceRegistration out = bundleContext.registerService(AdapterFactory.SERVICE_NAME, factory, properties);
        propertiesMap.put(out, properties);
        providedAdaptablesMap.putAll(out, providedAdaptables);
        return out;
    }

    private String[] getCurrentAdaptables() {
        return getCurrentAdaptables(Collections.<String>emptyList());
    }

    private String[] getCurrentAdaptables(List<String> additional) {
        Set<String> adaptablesSet = Sets.newHashSet(ADAPTABLES);
        adaptablesSet.addAll(providedAdaptablesMap.values());
        adaptablesSet.addAll(additional);
        return adaptablesSet.toArray(new String[adaptablesSet.size()]);
    }

    @SuppressWarnings("unchecked")
    private synchronized void updateAdapters() {
        String[] adaptables = getCurrentAdaptables();
        for (final Map.Entry<ServiceRegistration, Dictionary<String, Object>> e : propertiesMap.entrySet()) {
            final ServiceRegistration serviceRegistration = e.getKey();
            final Dictionary<String, Object> properties = e.getValue();
            properties.put(AdapterFactory.ADAPTABLE_CLASSES, adaptables);
            serviceRegistration.setProperties(properties);
        }
    }
}
