package com.icfolson.sling.translate.api.constants;

import org.apache.sling.api.resource.Resource;

import java.util.Locale;

/**
 * A listing of headers that can be applied to OSGi bundles to control Sling Translate capabilities.
 */
public final class BundleHeaders {

    /**
     * Packages to be scanned for translation dictionaries.  The value should be a comma-separated list of packages.
     * Packages can include wildcards (e.g., "com.icfolson.sling.translate.*")
     */
    public static final String SLING_TRANSLATE_PACKAGES = "Sling-Translate-Packages";

    /**
     * Additional processors to be executed when generating the translation dictionary models from the annotated
     * classes.  The value should be a comma-separated list of fully-qualified class names.
     */
    public static final String SLING_TRANSLATE_PROCESSORS = "Sling-Translate-Processors";

    /**
     * Bundles can use this header to notify of adapter factories capable of adapting the listed classes to instances
     * of java.util.{@link Locale}.  For instance, a bundle might provide an adapter capable of adapting a
     * {@link Resource} to a Locale (perhaps based on a project-specific content layout). The bundle could advertise
     * that fact by setting this header value to "org.apache.sling.api.resource.Resource", allowing resources to be
     * adapted directly to their custom dictionary classes.
     */
    public static final String SLING_TRANSLATE_LOCALE_ADAPTABLES = "Sling-Translate-Locale-Adaptables";

    private BundleHeaders() { }
}
