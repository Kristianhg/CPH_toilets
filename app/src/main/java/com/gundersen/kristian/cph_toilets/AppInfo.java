package com.gundersen.kristian.cph_toilets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AppInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.info_toolbar);
        setSupportActionBar(myToolbar);
    }
}
