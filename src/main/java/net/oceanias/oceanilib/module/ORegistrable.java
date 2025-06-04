package net.oceanias.oceanilib.module;

@SuppressWarnings("unused")
public interface ORegistrable {
    void onRegister();

    default void onUnregister() {}
}