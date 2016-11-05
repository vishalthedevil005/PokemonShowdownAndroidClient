package com.pokemonshowdown.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pokemonshowdown.R;
import com.pokemonshowdown.application.MyApplication;
import com.pokemonshowdown.data.BattleFieldData;
import com.pokemonshowdown.data.Onboarding;
import com.pokemonshowdown.dialog.OnboardingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by McBeengs on 23/10/2016.
 */

public class WatchBattleFragment {

    private static View mView;
    private static ProgressDialog mWaitingDialog;
    private static ListView mListView;
    private volatile static String selectedKey;
    private volatile static String selectedValue;

    public static View newInstance(final LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.fragment_watch_battle_lobby, null);

        TextView roomTitle = (TextView) mView.findViewById(R.id.room_title);
        TextView forgetButton = (TextView) mView.findViewById(R.id.forget_button);
        final Spinner formatsSpinner = (Spinner) mView.findViewById(R.id.formats_spinner);
        mListView = (ListView) mView.findViewById(R.id.battles_list_view);
        final Button findButton = (Button) mView.findViewById(R.id.find_button);

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

        ArrayAdapter<String> formatsAdapter = new ArrayAdapter<>(mView.getContext(), R.layout.fragment_user_list, mFormatList);
        formatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatsSpinner.setAdapter(formatsAdapter);

        formatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.getMyApplication().sendClientMessage("|/cmd roomlist");

                //Alert dialog that hides the loading
                mWaitingDialog = new ProgressDialog(mView.getContext());
                mWaitingDialog.setMessage(mView.getResources().getString(R.string.download_matches_inprogress));
                mWaitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mWaitingDialog.setCancelable(true);

                ((Activity) mView.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWaitingDialog.show();
                    }
                });
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

                Toast.makeText(mView.getContext(), "Key: " + selectedKey + " | Value: " + selectedValue, Toast.LENGTH_SHORT).show();
                MainScreenFragment.TABS_HOLDER_ACCESSOR.changeTab(BattleFieldFragment.newInstance(inflater));
            }
        });

        return mView;
    }

    public static void fireBattlesListViewUpdate() {
        if (!WatchBattleFragment.dismissWaitingDialog()) {
            return;
        }

        HashMap<String, String> battleList = BattleFieldData.get(mView.getContext()).getAvailableWatchBattleList();
        if (battleList.isEmpty()) {
            new AlertDialog.Builder(mView.getContext())
                    .setMessage(R.string.no_available_battle)
                    .create()
                    .show();
            return;
        }
        final String[] key = new String[battleList.size()];
        final String[] value = new String[battleList.size()];
        int count = 0;
        Set<String> iterators = battleList.keySet();
        for (String iterator : iterators) {
            key[count] = iterator;
            value[count] = battleList.get(iterator);
            count++;
        }
//        new AlertDialog.Builder(mView.getContext())
//                .setItems(value, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        BattleFieldData.get(mView.getContext()).joinRoom(key[which], true);
//                        dialog.dismiss();
//                    }
//                })
//                .show();

        mListView.setAdapter(new ArrayAdapter<String>(mListView.getContext(), R.layout.fragment_simple_list_row,value));
        mListView.setItemChecked(0, true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedKey = key[i];
                selectedValue = value[i];
                mView.findViewById(R.id.find_button).setEnabled(true);
            }
        });
    }

    public static boolean dismissWaitingDialog() {
        if (mWaitingDialog.isShowing()) {
            ((Activity) mView.getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWaitingDialog.dismiss();
                }
            });
            return true;
        } else {
            return false;
        }
    }
}
