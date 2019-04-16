package com.freadapp.fread.dictionary;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.freadapp.fread.R;
import com.freadapp.fread.data.model.Word;
import com.freadapp.fread.helpers.Constants;
import com.freadapp.fread.helpers.JsonUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DictionaryMainFragment extends Fragment {

    public static final String TAG = DictionaryMainFragment.class.getName();

    private static final String LANGUAGE_PARAM = "en";

    private ImageButton mWordSearchButton;
    private EditText mWordToSearch;

    public static DictionaryMainFragment newInstance() {

        Bundle args = new Bundle();

        DictionaryMainFragment fragment = new DictionaryMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_dictionary_fragment, container, false);

        mWordSearchButton = view.findViewById(R.id.ib_word_search);
        mWordToSearch = view.findViewById(R.id.et_word_to_search);

        mWordSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String wordToSearch = mWordToSearch.getText().toString();
                if (!wordToSearch.isEmpty()) {
                    String urlAsString = buildWordSearchURL(wordToSearch);
                    DictionaryTask dictionaryTask = new DictionaryTask();
                    dictionaryTask.execute(urlAsString);
                } else {
                    Toast.makeText(getContext(), "Enter a word to define.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;

    }

    private String buildWordSearchURL(String wordToSearch) {

        return Constants.OXFORD_API_ENDPOINT_URL + LANGUAGE_PARAM + "/" + wordToSearch;

    }


    private class DictionaryTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", Constants.OXFORD_APP_ID);
                urlConnection.setRequestProperty("app_key", Constants.OXFORD_APP_KEY);

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    bufferedReader.close();
                    urlConnection.disconnect();
                    return stringBuilder.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: " + e.getMessage(), e.getCause());
                return e.toString();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    Word word = JsonUtils.getWordFromJSON(result);
                    String wordName = word.getWord();
                    Log.i(TAG, "onPostExecute: " + wordName);
                    Log.i(TAG, "onPostExecute: " + word.getLexicalCategories());
                    Log.i(TAG, "onPostExecute: " + word.getDefinitions());

                } catch (JSONException e) {
                    Log.e(TAG, "onPostExecute: " + e.getMessage(), e.getCause());
                }
            } else {
                Toast.makeText(getContext(), "Sorry, please try another word.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
