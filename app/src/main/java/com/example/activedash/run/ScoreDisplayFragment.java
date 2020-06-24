package com.example.activedash.run;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.activedash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreDisplayFragment extends Fragment {
    View rootView;

    public ScoreDisplayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ScoreActivityViewModel.scoreCalculatorDisplay = false;
        rootView = inflater.inflate(R.layout.fragment_score_display, container, false);
        return rootView;
    }
}
