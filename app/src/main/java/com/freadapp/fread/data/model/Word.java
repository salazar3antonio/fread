package com.freadapp.fread.data.model;

import java.util.ArrayList;

public class Word {

    private String word;
    private ArrayList<String> definitions;
    private ArrayList<String> lexicalCategories;

    public Word() {
    }

    public Word(String word, ArrayList<String> definitions, ArrayList<String> lexicalCategories) {
        this.word = word;
        this.definitions = definitions;
        this.lexicalCategories = lexicalCategories;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<String> definitions) {
        this.definitions = definitions;
    }

    public ArrayList<String> getLexicalCategories() {
        return lexicalCategories;
    }

    public void setLexicalCategories(ArrayList<String> lexicalCategories) {
        this.lexicalCategories = lexicalCategories;
    }
}
