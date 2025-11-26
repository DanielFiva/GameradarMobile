package com.example.gameradarmobile;

import android.text.Editable;
import android.text.TextWatcher;

// SimpleTextWatcher allows you to only override the methods you care about
public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void afterTextChanged(Editable s) { }

    // Abstract method for onTextChanged as String
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onTextChanged(s.toString());
    }

    public abstract void onTextChanged(String s);
}
