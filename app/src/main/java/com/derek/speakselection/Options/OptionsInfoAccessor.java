package com.derek.speakselection.Options;

/**
 * Created by Derek Leung on 2017/10/11.
 */

public interface OptionsInfoAccessor<T,S> {
    T get();
    void set(S value);
}
