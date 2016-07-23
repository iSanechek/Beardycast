package com.isanechek.beardycast.testlab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.isanechek.beardycast.R;

/**
 * Created by isanechek on 24.07.16.
 */

public class TestViewBind extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_two);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TestFragment())
                    .commit();
        }
    }
}
