package com.redhead.y14.womandictionary;

public class DictObjectModel {
    String meaning;
    String word;

    public DictObjectModel(String str, String str2) {
        this.word = str;
        this.meaning = str2;
    }

    public String getWord() {
        return this.word;
    }

    public String getMeaning() {
        return this.meaning;
    }
}
