package net.oceanias.ocean.module;

@SuppressWarnings("unused")
public interface ORegistrable {
    void onRegister();

    default void onUnregister() {}
}