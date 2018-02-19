package com.novoda.buildproperties.internal

import com.novoda.buildproperties.Entries
import com.novoda.buildproperties.Entry
import com.novoda.buildproperties.ExceptionFactory

class FilePropertiesEntries extends Entries {

    private final Entries entries

    static FilePropertiesEntries create(File file,
                                        ExceptionFactory exceptionFactory) {
        new FilePropertiesEntries(PropertiesProvider.create(file, exceptionFactory))
    }

    private FilePropertiesEntries(Entries entries) {
        this.entries = entries
    }

    @Override
    boolean contains(String key) {
        entries.contains(key)
    }

    @Override
    Entry getAt(String key) {
        entries.getAt(key)
    }

    @Override
    Enumeration<String> getKeys() {
        entries.getKeys()
    }

    private static class PropertiesProvider extends Entries {
        final File file
        final Properties properties
        final PropertiesProvider defaults
        final ExceptionFactory exceptionFactory
        final Set<String> keys

        static PropertiesProvider create(File file, ExceptionFactory exceptionFactory) {
            if (!file.exists()) {
                throw exceptionFactory.fileNotFound(file)
            }

            Properties properties = new Properties()
            properties.load(new FileInputStream(file))

            PropertiesProvider defaults = null
            String include = properties['include']
            if (include != null) {
                defaults = create(new File(file.parentFile, include), exceptionFactory)
            }
            new PropertiesProvider(file, properties, defaults, exceptionFactory)
        }

        private PropertiesProvider(File file,
                                   Properties properties,
                                   PropertiesProvider defaults,
                                   ExceptionFactory exceptionFactory) {
            this.file = file
            this.properties = properties
            this.defaults = defaults
            this.exceptionFactory = exceptionFactory
            this.keys = new HashSet<>(properties.stringPropertyNames())
            if (defaults != null) {
                this.keys.addAll(defaults.keys)
            }
        }

        @Override
        boolean contains(String key) {
            properties[key] != null || defaults?.contains(key)
        }

        @Override
        Entry getAt(String key) {
            return new Entry(key, {
                Object value = properties[key]
                if (value != null) {
                    return value
                }
                if (defaults?.contains(key)) {
                    return defaults.getAt(key).getValue()
                }
                throw exceptionFactory.propertyNotFound(key)
            })
        }

        @Override
        Enumeration<String> getKeys() {
            Collections.enumeration(keys)
        }
    }
}
