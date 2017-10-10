package com.derek.speakselection.Options;

public abstract class OptionsInfo {
    String title;
    public String value;
    public abstract void action(OptionsInfo option);

    protected OptionsInfo(String title, String value){
        this.title = title;
        this.value = value;
    }
}
