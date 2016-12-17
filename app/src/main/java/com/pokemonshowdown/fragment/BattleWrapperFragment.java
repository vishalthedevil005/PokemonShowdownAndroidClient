package com.pokemonshowdown.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.pokemonshowdown.R;

/**
 * Created by nunom on 22/01/2017.
 */

public class BattleWrapperFragment {
    
    public static View newInstance(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.abc_123_wrapper, null);
        return view;
    }
}
