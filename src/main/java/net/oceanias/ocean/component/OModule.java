package net.oceanias.ocean.component;

import java.util.List;

@SuppressWarnings("unused")
public interface OModule extends OComponent {
    List<OProvider> getProviders();

    @Override
    default void registerInternally() {
        for (final OProvider provider : getProviders()) {
            provider.registerInternally();
        }

        OComponent.super.registerInternally();
    }

    @Override
    default void unregisterInternally() {
        OComponent.super.unregisterInternally();

        for (final OProvider provider : getProviders()) {
            provider.unregisterInternally();
        }
    }
}