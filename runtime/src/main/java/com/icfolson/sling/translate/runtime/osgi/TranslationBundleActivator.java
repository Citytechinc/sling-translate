package com.icfolson.sling.translate.runtime.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The bundle activator for the translation runtime bundle.
 */
public class TranslationBundleActivator implements BundleActivator {

    private TranslateBundleListener bundleListener;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        bundleListener = new TranslateBundleListener(bundleContext);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        bundleListener.unregisterAll();
    }

}
