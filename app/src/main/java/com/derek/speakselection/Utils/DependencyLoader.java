package com.derek.speakselection.Utils;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Derek Leung on 2017/10/11.
 */

public class DependencyLoader {
    private boolean resolved = false;
    private HashMap<String, DependenciesDescription> dependencies;
    private Callback readyCallback;

    private Set<String> dependenciesLeft;
    private int currentDependenciesLeft;

    public DependencyLoader(HashMap<String, DependenciesDescription> dependencies, Callback cb) {
        resolved = false;
        this.dependencies = dependencies;
        readyCallback = cb;

        currentDependenciesLeft = dependencies.keySet().size();
        dependenciesLeft = new TreeSet<>(dependencies.keySet());

        initDependencies();
    }

    private void initDependencies() {
        // Check if any dependencies left to load
        if(dependenciesLeft.size() < 1){
            resultReady();
            return;
        }

        // load new set of dependencies
        List<Pair<String, DependenciesDescription>> dependenciesToLoad = new ArrayList<>();
        for(String dependencyName : dependenciesLeft){
            final DependenciesDescription dependency = dependencies.get(dependencyName);

            boolean allDependenciesLoaded = true;
            for(String requiredDependencyName : dependency.dependencies){
                if(dependenciesLeft.contains(requiredDependencyName)){
                    allDependenciesLoaded = false;
                    break;
                }
            }
            if(allDependenciesLoaded){
                dependenciesToLoad.add(new Pair<>(dependencyName, dependency));
            }
        }

        currentDependenciesLeft = dependenciesToLoad.size();
        for(Pair<String, DependenciesDescription> dependencyPair : dependenciesToLoad){
            final String dependencyName = dependencyPair.first;
            final DependenciesDescription dependency = dependencyPair.second;

            dependency.init(this, new DependencySuccessCallback() {
                @Override
                public void call(Object initializedObj) {
                    handleResult(dependency, dependencyName, initializedObj);
                }
            });
        }
    }

    private void handleResult(DependenciesDescription dependency, String dependencyName, Object initializedObj){
        dependency.initializedObj = initializedObj;
        dependenciesLeft.remove(dependencyName);

        currentDependenciesLeft--;
        if(currentDependenciesLeft < 1){
            // Load next sets of dependencies
            initDependencies();
        }
    }

    private void resultReady(){
        resolved = true;
        readyCallback.call(this);
    }

    public Object get(String dependencyName){
        if(dependencies.containsKey(dependencyName)){
            return dependencies.get(dependencyName).initializedObj;
        }else{
            return null;
        }
    }

    public boolean isResolved(){
        return resolved;
    }
}