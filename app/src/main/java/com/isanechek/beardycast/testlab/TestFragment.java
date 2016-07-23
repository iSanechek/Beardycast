package com.isanechek.beardycast.testlab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static trikita.anvil.DSL.*;

import trikita.anvil.RenderableView;

/**
 * Created by isanechek on 24.07.16.
 */

public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert container != null;
        return new RenderableView(container.getContext()) {
            @Override
            public void view() {
                linearLayout(() -> {
                    textView(() -> {
                        text("Test");
                    });
                });
            }
        };
    }
}
