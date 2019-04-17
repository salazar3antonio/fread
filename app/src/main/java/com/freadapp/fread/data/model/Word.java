package com.freadapp.fread.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Word implements Parcelable {

    private String word;
    private ArrayList<String> definitions;
    private ArrayList<String> lexicalCategories;

    public Word() {
    }

    public Word(Parcel in) {

        word = in.readString();
        definitions = new ArrayList<>();
        in.readList(definitions, null);
        lexicalCategories = new ArrayList<>();
        in.readList(lexicalCategories, null);

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(word);
        parcel.writeList(definitions);
        parcel.writeList(lexicalCategories);

    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };
}
