package com.pokemonshowdown.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pokemonshowdown.R;
import com.pokemonshowdown.adapter.PokemonTeamSpinnerAdapter;
import com.pokemonshowdown.data.BattleFieldData;
import com.pokemonshowdown.data.Onboarding;
import com.pokemonshowdown.data.PokemonTeam;
import com.pokemonshowdown.dialog.ChallengeDialog;
import com.pokemonshowdown.dialog.OnboardingDialog;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by McBeengs on 23/10/2016.
 */

public class BattleLobbyFragment {

    private static View mView;

    public static View newInstance(LayoutInflater inflater, final String format) {
        mView = inflater.inflate(R.layout.fragment_battle_lobby, null);

        LinearLayout userLayout = (LinearLayout) mView.findViewById(R.id.user_layout);
        final EditText userText = (EditText) mView.findViewById(R.id.user_text);
        TextView roomTitle = (TextView) mView.findViewById(R.id.room_title);
        TextView forgetButton = (TextView) mView.findViewById(R.id.forget_button);
        final Spinner formatsSpinner = (Spinner) mView.findViewById(R.id.formats_spinner);
        final Spinner teamSpinner = (Spinner) mView.findViewById(R.id.teams_spinner);
        final Button findButton = (Button) mView.findViewById(R.id.find_button);

        if (format.equals("normal")) {
            userLayout.setVisibility(View.GONE);
        }

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mView.getContext())
                        .setMessage("Are you sure you want to leave?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainScreenFragment.TABS_HOLDER_ACCESSOR.removeTab();
                            }
                        })
                        .show();
            }
        });

        //Populate formats spinner with webservice response
        final ArrayList<String> mFormatList = new ArrayList<>();

        ArrayList<BattleFieldData.FormatType> formatTypes = BattleFieldData.get(mView.getContext()).getFormatTypes();
        for (BattleFieldData.FormatType formatType : formatTypes) {
            ArrayList<String> result = formatType.getSearchableFormatList();
            for (String name : result) {
                mFormatList.add(name);
            }
        }

        ArrayAdapter<String> formatsAdapter = new ArrayAdapter<>(mView.getContext(), R.layout.fragment_simple_list_row, mFormatList);

        formatsSpinner.setAdapter(formatsAdapter);

        //Create and set custom adapter to teams spinner
        final PokemonTeamSpinnerAdapter mRandomTeamAdapter = new PokemonTeamSpinnerAdapter(mView.getContext(),
                Arrays.asList(new PokemonTeam(ChallengeDialog.RANDOM_TEAM_NAME)));
        teamSpinner.setAdapter(mRandomTeamAdapter);

        formatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mFormatList.size() == 0) {
                    //can happen when no internet
                    return;
                }
                String currentFormatString = (String) formatsSpinner.getItemAtPosition(position);
                BattleFieldData.Format currentFormat;
                if (currentFormatString != null) {
                    currentFormat = BattleFieldData.get(mView.getContext()).getFormat(currentFormatString);
                    if (currentFormat != null) {
                        if (currentFormat.isRandomFormat()) {
                            teamSpinner.setAdapter(mRandomTeamAdapter);
                            teamSpinner.setEnabled(false);
                            if (format.equals("challenge") && !userText.getText().toString().isEmpty()) {
                                findButton.setEnabled(true);
                            } else if (format.equals("normal")) {
                                findButton.setEnabled(true);
                            } else {
                                findButton.setEnabled(false);
                            }
                        } else {
                            if (PokemonTeam.getPokemonTeamList() != null && PokemonTeam.getPokemonTeamList().size() > 0) {
                                int currentSelectedTeam = teamSpinner.getSelectedItemPosition();
                                teamSpinner.setAdapter(new PokemonTeamSpinnerAdapter(mView.getContext(), PokemonTeam.getPokemonTeamList()));
                                teamSpinner.setEnabled(true);
                                int newSelectedTeam = -1;
                                for (int i = 0; i < PokemonTeam.getPokemonTeamList().size(); i++) {
                                    if (PokemonTeam.getPokemonTeamList().get(i).getTier().equals(currentFormatString)) {
                                        newSelectedTeam = i;
                                        break;
                                    }
                                }
                                if (newSelectedTeam > -1) {
                                    teamSpinner.setSelection(newSelectedTeam);
                                } else {
                                    teamSpinner.setSelection(currentSelectedTeam);
                                }

                                if (format.equals("challenge") && !userText.getText().toString().isEmpty()) {
                                    findButton.setEnabled(true);
                                } else if (format.equals("normal")) {
                                    findButton.setEnabled(true);
                                } else {
                                    findButton.setEnabled(false);
                                }
                            } else {
                                //there are no teams, we fill the spinner with a filler item an disable it
                                teamSpinner.setAdapter(new ArrayAdapter<>(mView.getContext(), android.R.layout.simple_spinner_item,
                                        mView.getResources().getStringArray(R.array.empty_team_list_filler)));
                                teamSpinner.setEnabled(false);
                                findButton.setEnabled(false);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first need to check if the user is logged in
                Onboarding onboarding = Onboarding.get(mView.getContext().getApplicationContext());
                if (!onboarding.isSignedIn()) {
                    FragmentManager fm = ((FragmentActivity) mView.getContext()).getSupportFragmentManager();
                    OnboardingDialog fragment = new OnboardingDialog();
                    fragment.show(fm, OnboardingDialog.OTAG);
                    return;
                }

                findButton.setText(R.string.cancel_search);

            }
        });

        return mView;
    }
}
