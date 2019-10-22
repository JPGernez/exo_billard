package com.example.exo_billard;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Preferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

    }

}
