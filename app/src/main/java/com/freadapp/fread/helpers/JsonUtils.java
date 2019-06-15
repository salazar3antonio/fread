package com.freadapp.fread.helpers;

import android.util.Log;

import com.freadapp.fread.data.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static final String TAG = JsonUtils.class.getName();

    private static final String RESULTS = "results";
    private static final String WORD = "word";
    private static final String LEXICAL_ENTRIES = "lexicalEntries";
    private static final String LEXICAL_CATEGORY = "lexicalCategory";
    private static final String ENTRIES = "entries";
    private static final String SENSES = "senses";
    private static final String DEFINITIONS = "definitions";
    private static final String ID = "id";

    public static Word getWordFromJSON(String responseString) throws JSONException {

        JSONObject topLevelObject = new JSONObject(responseString);
        JSONArray results = topLevelObject.getJSONArray(RESULTS);

        Word word = new Word();

        if (results.length() > 0) {
            JSONObject firstResult = results.getJSONObject(0);

            JSONArray lexicalEntriesArray = firstResult.getJSONArray(LEXICAL_ENTRIES);
            ArrayList<String> lexicalCategoriesArrayList = new ArrayList<>();
            ArrayList<String> definitionsArrayList = new ArrayList<>();

            for (int i = 0; i < lexicalEntriesArray.length(); i++) {

                JSONObject lexicalEntry = lexicalEntriesArray.getJSONObject(i);
                JSONObject lexicalCategory = lexicalEntry.getJSONObject(LEXICAL_CATEGORY);

                String lexicalId = lexicalCategory.optString(ID);
                lexicalCategoriesArrayList.add(lexicalId);

                JSONArray entriesArray = lexicalEntry.getJSONArray(ENTRIES);

                if (entriesArray.length() > 0) {

                    JSONObject firstEntry = entriesArray.getJSONObject(0);
                    JSONArray sensesArray = firstEntry.getJSONArray(SENSES);

                    if (sensesArray.length() > 0) {
                        JSONObject firstSense = sensesArray.getJSONObject(0);
                        String defs = firstSense.optString(DEFINITIONS);
                        if (!defs.isEmpty()) {
                            JSONArray definitionsArray = firstSense.getJSONArray(DEFINITIONS);
                            String firstDefinition = definitionsArray.optString(0);
                            definitionsArrayList.add(firstDefinition);
                        }

                    }

                }
            }

            word.setLexicalCategories(lexicalCategoriesArrayList);
            String wordName = firstResult.optString(WORD);
            word.setDefinitions(definitionsArrayList);
            word.setWord(wordName);

        }

        return word;

    }


}
