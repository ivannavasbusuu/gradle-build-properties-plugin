package com.novoda.buildproperties

import com.novoda.buildproperties.internal.DefaultEntriesFactory
import com.novoda.buildproperties.internal.DefaultExceptionFactory

class BuildProperties {

    private final String name
    private final Entries.Factory factory
    private EntriesChain chain

    BuildProperties(String name) {
        this.name = name
        this.factory = new DefaultEntriesFactory(new DefaultExceptionFactory(name))
    }

    String getName() {
        name
    }

    Entries getEntries() {
        chain
    }

    Enumeration<String> getKeys() {
        chain.keys
    }

    ExceptionFactory getExceptionFactory() {
        exceptionFactory
    }

    Entry getAt(String key) {
        chain.getAt(key)
    }

    void setDescription(String description) {
        factory.additionalMessage = description
    }

    EntriesChain using(Map<String, Object> map) {
        newChain(map)
    }

    EntriesChain using(File file) {
        newChain(file)
    }

    EntriesChain using(Entries entries) {
        newChain(entries)
    }

    private EntriesChain newChain(def source) {
        chain = new EntriesChain(factory, source)
        return chain
    }
}
