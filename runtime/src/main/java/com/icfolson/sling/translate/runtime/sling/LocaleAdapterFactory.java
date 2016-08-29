package com.icfolson.sling.translate.runtime.sling;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;

@Service
@Component(immediate = true)
@Properties({
    @Property(name = AdapterFactory.ADAPTABLE_CLASSES, value = "org.apache.sling.api.SlingHttpServletRequest"),
    @Property(name = AdapterFactory.ADAPTER_CLASSES, value = "java.util.Locale")
})
public class LocaleAdapterFactory implements AdapterFactory {

    @Override
    public <AdapterType> AdapterType getAdapter(final Object o, final Class<AdapterType> aClass) {
        if (o instanceof SlingHttpServletRequest) {
            return (AdapterType) ((SlingHttpServletRequest) o).getLocale();
        }
        return null;
    }
    
}
