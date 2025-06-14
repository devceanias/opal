package net.oceanias.opal.component;

import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
public interface OComponent {
    default void onRegister() {}

    default void onUnregister() {}

    @ApiStatus.Internal
    default void registerInternally() {
        onRegister();
    }

    @ApiStatus.Internal
    default void unregisterInternally() {
        onUnregister();
    }
}