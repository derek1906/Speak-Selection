package com.derek.speakselection.Utils;

/**
 * Created by Derek Leung on 2017/10/11.
 */

public abstract class DependenciesDescription {
    Object initializedObj;
    String[] dependencies = {};

    public DependenciesDescription(String[] dependencies) {
        this.dependencies = dependencies;
    }

    public abstract void init(DependencyLoader loader, DependencySuccessCallback success);

    public DependenciesDescription(){}
}
