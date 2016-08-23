package com.icfolson.sling.translate.example.adapter;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.adapter.AdapterFactory;

import java.util.Locale;

@Service
@Component(immediate = true)
@Properties({
    @Property(name = AdapterFactory.ADAPTABLE_CLASSES, value = "org.apache.sling.api.resource.Resource"),
    @Property(name = AdapterFactory.ADAPTER_CLASSES, value = "java.util.Locale")
})
public class LocaleAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object o, final Class<AdapterType> aClass) {
        return Locale.class.isAssignableFrom(aClass) ? (AdapterType) Locale.ENGLISH : null;
    }
}
