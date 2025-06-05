package net.oceanias.ocean.module;

import java.util.List;

@SuppressWarnings("unused")
public interface OModule extends ORegistrable {
    List<OProvider> getProviders();

    @Override
    default void onRegister() {
        for (final OProvider provider : getProviders()) {
            provider.onRegister();
        }
    }

    @Override
    default void onUnregister() {
        for (final OProvider provider : getProviders()) {
            provider.onUnregister();
        }
    }
}