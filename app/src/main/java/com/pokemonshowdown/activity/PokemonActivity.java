package com.pokemonshowdown.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokemonshowdown.R;
import com.pokemonshowdown.data.ItemDex;
import com.pokemonshowdown.data.MoveDex;
import com.pokemonshowdown.data.Pokemon;
import com.pokemonshowdown.dialog.StatsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import me.grantland.widget.AutofitTextView;

/**
 * Created by McBeengs on 01/11/2016.
 */

public class PokemonActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Pokemon mPokemon;
    private int position;
    private int mSelectedMove;
    private AutofitTextView pokeName;
    private AutofitTextView itemLabel;
    private AutofitTextView nature;
    private View hpBar, atkBar, defBar, spAtkBar, spDefBar, speBar;
    private AutofitTextView hpIvs, atkIvs, defIvs, spAtkIvs, spDefIvs, speIvs;
    private AutofitTextView hpStats, atkStats, defStats, spAtkStats, spDefStats, speStats;
    private LinearLayout move1, move2, move3, move4;
    private ImageView move1Type, move2Type, move3Type, move4Type;
    private ImageView move1Damage, move2Damage, move3Damage, move4Damage;
    private AutofitTextView move1Name, move2Name, move3Name, move4Name;
    public static SetPokemonStats POKEMON_STATS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        POKEMON_STATS = new SetPokemonStats();
        setContentView(R.layout.abc_123_temp);
        setupToolbar();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("pokemon");
        }

        if (!jsonMyObject.isEmpty()) {
            mPokemon = new Gson().fromJson(jsonMyObject, Pokemon.class);
        }

        position = extras.getInt("position");

        ImageView pokeIcon = (ImageView) findViewById(R.id.poke_icon);
        pokeIcon.setImageResource(mPokemon.getFrontSprite());

        pokeName = (AutofitTextView) findViewById(R.id.poke_name);
        pokeName.setText(mPokemon.getNickName());
        pokeName.setOnClickListener(this);

        SeekBar levelSeek = (SeekBar) findViewById(R.id.poke_level_seek);
        TextView levelLabel = (TextView) findViewById(R.id.poke_level_label);
        levelSeek.setProgress(mPokemon.getLevel());
        levelLabel.setText("" + mPokemon.getLevel());
        levelSeek.setOnSeekBarChangeListener(this);

        SeekBar happySeek = (SeekBar) findViewById(R.id.poke_happy_seek);
        TextView happyLabel = (TextView) findViewById(R.id.poke_happy_label);
        happySeek.setProgress(mPokemon.getHappiness());
        happyLabel.setText("" + mPokemon.getHappiness());
        happySeek.setOnSeekBarChangeListener(this);

        itemLabel = (AutofitTextView) findViewById(R.id.item_label);
        itemLabel.setText(mPokemon.getItem().isEmpty() ? "No item" : mPokemon.getItem());
        if (!mPokemon.getItem().isEmpty()) {
            JSONObject itemJson = ItemDex.get(getApplicationContext()).getItemJsonObject(mPokemon.getItem().toLowerCase().trim());
            try {
                mPokemon.setItem(itemJson.getString("name"));
                itemLabel.setText(itemJson.getString("name"));
                itemLabel.setCompoundDrawablesWithIntrinsicBounds(ItemDex.getItemIcon(getApplicationContext(), mPokemon.getItem().toLowerCase().trim()), 0, 0, 0);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        itemLabel.setOnClickListener(this);

        ArrayList<String> abilities = new ArrayList<>();
        for (String key : mPokemon.getAbilityList().keySet()) {
            abilities.add(mPokemon.getAbilityList().get(key));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.fragment_user_list, abilities);
        final Spinner abilitiesSpinner = (Spinner) findViewById(R.id.poke_ability_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        abilitiesSpinner.setAdapter(adapter);

        abilitiesSpinner.setSelection(abilities.indexOf(mPokemon.getAbility()));

        abilitiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String k = "";
                for (String key : mPokemon.getAbilityList().keySet()) {
                    if (mPokemon.getAbilityList().get(key).equals(abilitiesSpinner.getSelectedItem().toString())) {
                        k = key;
                        break;
                    }
                }

                mPokemon.setAbilityTag(k);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nature = (AutofitTextView) findViewById(R.id.nature);
        nature.setText(mPokemon.getNature());
        nature.setOnClickListener(this);

        final String[] hps = new String[]{"Nothing", "HP Bug", "HP Dark", "HP Dragon", "HP Electric", "HP Fighting", "HP Fire", "HP Flying",
                "HP Ghost", "HP Grass", "HP Ground", "HP Ice", "HP Poison", "HP Psychic", "HP Rock", "HP Steel", "HP Water"};

        adapter = new ArrayAdapter<>(getContext(), R.layout.fragment_user_list, hps);
        final Spinner hpsSpinner = (Spinner) findViewById(R.id.hp_ivs_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hpsSpinner.setAdapter(adapter);

        hpsSpinner.setSelection(getHPIndex());

        hpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 1:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(31);
                        break;

                    case 2:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    case 3:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    case 4:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    case 5:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(30);
                        break;

                    case 6:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(30);
                        break;

                    case 7:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(30);
                        break;

                    case 8:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(31);
                        break;

                    case 9:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    case 10:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(31);
                        break;

                    case 11:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    case 12:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(31);
                        break;

                    case 13:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(30);
                        break;

                    case 14:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(30);
                        break;

                    case 15:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(30);
                        mPokemon.setSpdIV(31);
                        break;

                    case 16:
                        mPokemon.setAtkIV(30);
                        mPokemon.setDefIV(30);
                        mPokemon.setSpAtkIV(30);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;

                    default:
                        mPokemon.setAtkIV(31);
                        mPokemon.setDefIV(31);
                        mPokemon.setSpAtkIV(31);
                        mPokemon.setSpDefIV(31);
                        mPokemon.setSpdIV(31);
                        break;
                }

                hpIvs.setText("" + mPokemon.getHPIV());
                atkIvs.setText("" + mPokemon.getAtkIV());
                defIvs.setText("" + mPokemon.getDefIV());
                spAtkIvs.setText("" + mPokemon.getSpAtkIV());
                spDefIvs.setText("" + mPokemon.getSpDefIV());
                speIvs.setText("" + mPokemon.getSpdIV());
                hpIvs.invalidate();
                atkIvs.invalidate();
                defIvs.invalidate();
                spAtkIvs.invalidate();
                spDefIvs.invalidate();
                speIvs.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        move1 = (LinearLayout) findViewById(R.id.move_1);
        move2 = (LinearLayout) findViewById(R.id.move_2);
        move3 = (LinearLayout) findViewById(R.id.move_3);
        move4 = (LinearLayout) findViewById(R.id.move_4);
        move1.setOnClickListener(this);
        move2.setOnClickListener(this);
        move3.setOnClickListener(this);
        move4.setOnClickListener(this);

        move1Type = (ImageView) findViewById(R.id.move1_type);
        move1Damage = (ImageView) findViewById(R.id.move1_damage);
        move2Type = (ImageView) findViewById(R.id.move2_type);
        move2Damage = (ImageView) findViewById(R.id.move2_damage);
        move3Type = (ImageView) findViewById(R.id.move3_type);
        move3Damage = (ImageView) findViewById(R.id.move3_damage);
        move4Type = (ImageView) findViewById(R.id.move4_type);
        move4Damage = (ImageView) findViewById(R.id.move4_damage);

        move1Name = (AutofitTextView) findViewById(R.id.move1_name);
        String m1 = mPokemon.getMove1().isEmpty() ? "Empty" : mPokemon.getMove1();
        move2Name = (AutofitTextView) findViewById(R.id.move2_name);
        String m2 = mPokemon.getMove2().isEmpty() ? "Empty" : mPokemon.getMove2();
        move3Name = (AutofitTextView) findViewById(R.id.move3_name);
        String m3 = mPokemon.getMove3().isEmpty() ? "Empty" : mPokemon.getMove3();
        move4Name = (AutofitTextView) findViewById(R.id.move4_name);
        String m4 = mPokemon.getMove4().isEmpty() ? "Empty" : mPokemon.getMove4();

        try {
            if (!m1.equals("Empty")) {
                JSONObject move1Json = MoveDex.get(getApplicationContext()).getMoveJsonObject(m1.replace("-", "").toLowerCase().trim());
                move1Name.setText(move1Json.getString("name"));
                move1Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), move1Json.getString("type")));
                move1Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), move1Json.getString("category")));
            } else {
                move1Name.setText(m1);
            }

            if (!m2.equals("Empty")) {
                JSONObject move2Json = MoveDex.get(getApplicationContext()).getMoveJsonObject(m2.replace("-", "").toLowerCase().trim());
                move2Name.setText(move2Json.getString("name"));
                move2Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), move2Json.getString("type")));
                move2Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), move2Json.getString("category")));
            } else {
                move2Name.setText(m2);
            }

            if (!m3.equals("Empty")) {
                JSONObject move3Json = MoveDex.get(getApplicationContext()).getMoveJsonObject(m3.replace("-", "").toLowerCase().trim());
                move3Name.setText(move3Json.getString("name"));
                move3Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), move3Json.getString("type")));
                move3Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), move3Json.getString("category")));
            } else {
                move3Name.setText(m3);
            }

            if (!m4.equals("Empty")) {
                JSONObject move4Json = MoveDex.get(getApplicationContext()).getMoveJsonObject(m4.replace("-", "").toLowerCase().trim());
                move4Name.setText(move4Json.getString("name"));
                move4Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), move4Json.getString("type")));
                move4Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), move4Json.getString("category")));
            } else {
                move4Name.setText(m4);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        LinearLayout.LayoutParams hpP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        hpP.weight = (float) mPokemon.getBaseHP() + (mPokemon.getHPEV() / 4);
        hpP.gravity = Gravity.CENTER;
        hpP.leftMargin = dipToPixels(5);
        hpP.rightMargin = dipToPixels(5);

        TextView hpLabel = (TextView) findViewById(R.id.base_hp);
        hpLabel.setText("" + mPokemon.getBaseHP());
        hpBar = findViewById(R.id.hp_bar);
        hpBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseHP()));
        hpBar.setLayoutParams(hpP);
        hpBar.setOnClickListener(this);
        hpIvs = (AutofitTextView) findViewById(R.id.hp_ivs_label);
        hpIvs.setText("" + mPokemon.getHPIV());
        hpIvs.setOnClickListener(this);
        hpStats = (AutofitTextView) findViewById(R.id.hp_stats);
        hpStats.setText("" + mPokemon.getHP());

        LinearLayout.LayoutParams atkP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        atkP.weight = (float) mPokemon.getBaseAtk() + (mPokemon.getAtkEV() / 4);
        atkP.gravity = Gravity.CENTER;
        atkP.leftMargin = dipToPixels(5);
        atkP.rightMargin = dipToPixels(5);
        TextView atkLabel = (TextView) findViewById(R.id.base_atk);
        atkLabel.setText("" + mPokemon.getBaseAtk());
        atkBar = findViewById(R.id.atk_bar);
        atkBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseAtk()));
        atkBar.setLayoutParams(atkP);
        atkBar.setOnClickListener(this);
        atkIvs = (AutofitTextView) findViewById(R.id.atk_ivs_label);
        atkIvs.setText("" + mPokemon.getAtkIV());
        atkIvs.setOnClickListener(this);
        atkStats = (AutofitTextView) findViewById(R.id.atk_stats);
        atkStats.setText("" + mPokemon.getAtk());

        LinearLayout.LayoutParams defP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        defP.weight = (float) mPokemon.getBaseDef() + (mPokemon.getDefEV() / 4);
        defP.gravity = Gravity.CENTER;
        defP.leftMargin = dipToPixels(5);
        defP.rightMargin = dipToPixels(5);
        TextView defLabel = (TextView) findViewById(R.id.base_def);
        defLabel.setText("" + mPokemon.getBaseDef());
        defBar = findViewById(R.id.def_bar);
        defBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseDef()));
        defBar.setLayoutParams(defP);
        defBar.setOnClickListener(this);
        defIvs = (AutofitTextView) findViewById(R.id.def_ivs_label);
        defIvs.setText("" + mPokemon.getDefIV());
        defIvs.setOnClickListener(this);
        defStats = (AutofitTextView) findViewById(R.id.def_stats);
        defStats.setText("" + mPokemon.getDef());

        LinearLayout.LayoutParams spAtkP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        spAtkP.weight = (float) mPokemon.getBaseSpAtk() + (mPokemon.getSpAtkEV() / 4);
        spAtkP.gravity = Gravity.CENTER;
        spAtkP.leftMargin = dipToPixels(5);
        spAtkP.rightMargin = dipToPixels(5);
        TextView spAtkLabel = (TextView) findViewById(R.id.base_sp_atk);
        spAtkLabel.setText("" + mPokemon.getBaseSpAtk());
        spAtkBar = findViewById(R.id.sp_atk_bar);
        spAtkBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseSpAtk()));
        spAtkBar.setLayoutParams(spAtkP);
        spAtkBar.setOnClickListener(this);
        spAtkIvs = (AutofitTextView) findViewById(R.id.sp_atk_ivs_label);
        spAtkIvs.setText("" + mPokemon.getSpAtkIV());
        spAtkIvs.setOnClickListener(this);
        spAtkStats = (AutofitTextView) findViewById(R.id.sp_atk_stats);
        spAtkStats.setText("" + mPokemon.getSpAtk());

        LinearLayout.LayoutParams spDefP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        spDefP.weight = (float) mPokemon.getBaseSpDef() + (mPokemon.getSpDefEV() / 4);
        spDefP.gravity = Gravity.CENTER;
        spDefP.leftMargin = dipToPixels(5);
        spDefP.rightMargin = dipToPixels(5);
        TextView spDefLabel = (TextView) findViewById(R.id.base_sp_def);
        spDefLabel.setText("" + mPokemon.getBaseSpDef());
        spDefBar = findViewById(R.id.sp_def_bar);
        spDefBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseSpDef()));
        spDefBar.setLayoutParams(spDefP);
        spDefBar.setOnClickListener(this);
        spDefIvs = (AutofitTextView) findViewById(R.id.sp_def_ivs_label);
        spDefIvs.setText("" + mPokemon.getSpDefIV());
        spDefIvs.setOnClickListener(this);
        spDefStats = (AutofitTextView) findViewById(R.id.sp_def_stats);
        spDefStats.setText("" + mPokemon.getSpDef());

        LinearLayout.LayoutParams speP = new LinearLayout.LayoutParams(0, dipToPixels(15), 0);
        speP.weight = (float) mPokemon.getBaseSpd() + (mPokemon.getSpd() / 4);
        speP.gravity = Gravity.CENTER;
        speP.leftMargin = dipToPixels(5);
        speP.rightMargin = dipToPixels(5);
        TextView speLabel = (TextView) findViewById(R.id.base_spe);
        speLabel.setText("" + mPokemon.getBaseSpd());
        speBar = findViewById(R.id.spe_bar);
        speBar.setBackgroundColor(getEVBarColor(mPokemon.getBaseSpd()));
        speBar.setLayoutParams(speP);
        speBar.setOnClickListener(this);
        speIvs = (AutofitTextView) findViewById(R.id.spe_ivs_label);
        speIvs.setText("" + mPokemon.getSpdIV());
        speIvs.setOnClickListener(this);
        speStats = (AutofitTextView) findViewById(R.id.spe_stats);
        speStats.setText("" + mPokemon.getSpd());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_ITEM) {
                String item = data.getExtras().getString("Search");
                mPokemon.setItem(item);

            } else if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_ITEM) {
                String item = data.getExtras().getString("Search");
                JSONObject itemJson = ItemDex.get(getApplicationContext()).getItemJsonObject(item);
                try {
                    mPokemon.setItem(itemJson.getString("name"));
                    itemLabel.setText(itemJson.getString("name"));
                    itemLabel.setCompoundDrawablesWithIntrinsicBounds(ItemDex.getItemIcon(getApplicationContext(), item), 0, 0, 0);
                    itemLabel.requestLayout();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                TeamBuildingActivity.firePokemonSwapping(mPokemon, position);
            } else if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_MOVES) {
                String move = data.getExtras().getString("Search");
                JSONObject moveJson = MoveDex.get(getApplicationContext()).getMoveJsonObject(move);
                try {
                    switch (mSelectedMove) {
                        case 1:
                            mPokemon.setMove1(moveJson.getString("name"));
                            move1Name.setText(moveJson.getString("name"));
                            move1Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), moveJson.getString("type")));
                            move1Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), moveJson.getString("category")));
                            move1.invalidate();
                            break;
                        case 2:
                            mPokemon.setMove2(moveJson.getString("name"));
                            move2Name.setText(moveJson.getString("name"));
                            move2Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), moveJson.getString("type")));
                            move2Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), moveJson.getString("category")));
                            move2.invalidate();
                            break;
                        case 3:
                            mPokemon.setMove3(moveJson.getString("name"));
                            move3Name.setText(moveJson.getString("name"));
                            move3Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), moveJson.getString("type")));
                            move3Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), moveJson.getString("category")));
                            move3.invalidate();
                            break;
                        case 4:
                            mPokemon.setMove4(moveJson.getString("name"));
                            move4Name.setText(moveJson.getString("name"));
                            move4Type.setImageResource(MoveDex.getTypeIcon(getApplicationContext(), moveJson.getString("type")));
                            move4Damage.setImageResource(MoveDex.getCategoryIcon(getApplicationContext(), moveJson.getString("category")));
                            move4.invalidate();
                            break;
                        default:
                            break;
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                TeamBuildingActivity.firePokemonSwapping(mPokemon, position);

                // hiddenpower X : change IV'
                // furstration : set 0 happiness
                // return get max happinnes

                if ("frustration".equals(move)) {
                    mPokemon.setHappiness(0);
                    Toast.makeText(getContext(), getText(R.string.happiness_min), Toast.LENGTH_SHORT).show();
                } else if ("return".equals(move)) {
                    mPokemon.setHappiness(255);
                    Toast.makeText(getContext(), getText(R.string.happiness_max), Toast.LENGTH_SHORT).show();
                } else if ("gyroball".equals(move) || "trickroom".equals(move)) {
                    boolean hasHP = false;
                    for (int i = 1; i <= 4; i++) {
                        if (mPokemon.getMove(i) != null && (mPokemon.getMove(i).contains("hiddenpower"))) {
                            hasHP = true;
                            break;
                        }
                    }

                    if (hasHP) {
                        mPokemon.setSpdIV(mPokemon.getSpdIV() % 4);
                    } else {
                        mPokemon.setSpdIV(0);
                    }

                    Toast.makeText(getContext(), getText(R.string.speediv_modified), Toast.LENGTH_SHORT).show();
                } else if (move != null && move.contains("hiddenpower")) {
                    if (move.length() > "hiddenpower".length()) {
                        boolean needsLowSpeed = false;
                        for (int i = 1; i <= 4; i++) {
                            if (mPokemon.getMove(i) != null && (mPokemon.getMove(i).equals("gyroball") || mPokemon.getMove(i).equals("trickroom"))) {
                                needsLowSpeed = true;
                                break;
                            }
                        }
                        String hpType = move.substring("hiddenpower".length());
                        switch (hpType) {

                        }
                        if (needsLowSpeed) {
                            mPokemon.setSpdIV(mPokemon.getSpdIV() % 4);
                        }
                        Toast.makeText(getContext(), getText(R.string.ivs_modified), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        TeamBuildingActivity.firePokemonSwapping(mPokemon, position);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.poke_name:
                final EditText dialogView = new EditText(getContext());
                dialogView.setText(mPokemon.getNickName());
                new AlertDialog.Builder(getContext()).setTitle("Set Pokemon Nickname")
                        .setView(dialogView)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mPokemon.setNickName(dialogView.getText().toString());
                                pokeName.setText(dialogView.getText());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
            case R.id.gender_male:
                break;
            case R.id.gender_female:
                break;
            case R.id.item_label:
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_ITEM);
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_ITEM);
                break;
            case R.id.hp_bar:
                showEVsDialog();
                break;
            case R.id.atk_bar:
                showEVsDialog();
                break;
            case R.id.def_bar:
                showEVsDialog();
                break;
            case R.id.sp_atk_bar:
                showEVsDialog();
                break;
            case R.id.sp_def_bar:
                showEVsDialog();
                break;
            case R.id.spe_bar:
                showEVsDialog();
                break;
            case R.id.hp_ivs_label:
                showIVsDialog(0);
                break;
            case R.id.atk_ivs_label:
                showIVsDialog(1);
                break;
            case R.id.def_ivs_label:
                showIVsDialog(2);
                break;
            case R.id.sp_atk_ivs_label:
                showIVsDialog(3);
                break;
            case R.id.sp_def_ivs_label:
                showIVsDialog(4);
                break;
            case R.id.spe_ivs_label:
                showIVsDialog(5);
                break;
            case R.id.nature:
                int selectedNature = Arrays.binarySearch(Pokemon.NATURES, mPokemon.getNature());
                Dialog dialog = new AlertDialog.Builder(getContext())
                        .setSingleChoiceItems(Pokemon.NATURES_DETAILS, selectedNature, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newNature = Pokemon.NATURES[which];
                                nature.setText(newNature);
                                mPokemon.setNature(newNature);
                                mPokemon.setStats(mPokemon.calculateStats());
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.move_1:
                intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                intent.putExtra(SearchableActivity.POKEMON_LEARNSET, mPokemon.getName());
                mSelectedMove = 1;
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                break;
            case R.id.move_2:
                intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                intent.putExtra(SearchableActivity.POKEMON_LEARNSET, mPokemon.getName());
                mSelectedMove = 2;
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                break;
            case R.id.move_3:
                intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                intent.putExtra(SearchableActivity.POKEMON_LEARNSET, mPokemon.getName());
                mSelectedMove = 3;
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                break;
            case R.id.move_4:
                intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                intent.putExtra(SearchableActivity.POKEMON_LEARNSET, mPokemon.getName());
                mSelectedMove = 4;
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_MOVES);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView label;
        switch (seekBar.getId()) {
            case R.id.poke_level_seek:
                if (i == 0)
                    i = 1;
                label = (TextView) findViewById(R.id.poke_level_label);
                label.setText("" + i);
                mPokemon.setLevel(i);
                break;
            case R.id.poke_happy_seek:
                if (i == 0)
                    i = 1;
                label = (TextView) findViewById(R.id.poke_happy_label);
                label.setText("" + i);
                mPokemon.setHappiness(i);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void showEVsDialog() {
        Bundle bundle = new Bundle();
        bundle.putIntArray(StatsDialog.ARGUMENT_STATS, mPokemon.getStats());
        bundle.putIntArray(StatsDialog.ARGUMENT_BASE_STATS, mPokemon.getBaseStats());
        bundle.putIntArray(StatsDialog.ARGUMENT_EV, mPokemon.getEVs());
        bundle.putIntArray(StatsDialog.ARGUMENT_IV, mPokemon.getIVs());
        bundle.putInt(StatsDialog.ARGUMENT_LEVEL, mPokemon.getLevel());
        bundle.putFloatArray(StatsDialog.ARGUMENT_NATURE_MULTIPLIER, mPokemon.getNatureMultiplier());
        bundle.putIntArray(StatsDialog.ARGUMENT_STAGES, mPokemon.getStages());

        StatsDialog statsDialog = new StatsDialog();
        statsDialog.setArguments(bundle);
        statsDialog.show(getSupportFragmentManager(), StatsDialog.STAG);
    }

    private void showIVsDialog(final int num) {
        String stat = "";
        switch (num) {
            case 0:
                stat = getResources().getString(R.string.stats_HP);
                break;
            case 1:
                stat = getResources().getString(R.string.stats_Atk);
                break;
            case 2:
                stat = getResources().getString(R.string.stats_Def);
                break;
            case 3:
                stat = getResources().getString(R.string.stats_SpAtk);
                break;
            case 4:
                stat = getResources().getString(R.string.stats_SpDef);
                break;
            case 5:
                stat = getResources().getString(R.string.stats_Spd);
                break;
        }

        final EditText edit = new EditText(getContext());
        edit.setEms(2);
        edit.setText("" + mPokemon.getIVs()[num]);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);

        new AlertDialog.Builder(getContext()).setTitle("Set IV's for the " + stat + " stat")
                .setView(edit)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (num) {
                            case 0:
                                mPokemon.setHPIV(Integer.parseInt(edit.getText().toString()));
                                hpIvs.setText(edit.getText());
                                hpIvs.invalidate();
                                break;
                            case 1:
                                mPokemon.setAtkIV(Integer.parseInt(edit.getText().toString()));
                                atkIvs.setText(edit.getText());
                                atkIvs.invalidate();
                                break;
                            case 2:
                                mPokemon.setDefIV(Integer.parseInt(edit.getText().toString()));
                                defIvs.setText(edit.getText());
                                defIvs.invalidate();
                                break;
                            case 3:
                                mPokemon.setSpAtkIV(Integer.parseInt(edit.getText().toString()));
                                spAtkIvs.setText(edit.getText());
                                spAtkIvs.invalidate();
                                break;
                            case 4:
                                mPokemon.setSpDefIV(Integer.parseInt(edit.getText().toString()));
                                spDefIvs.setText(edit.getText());
                                spDefIvs.invalidate();
                                break;
                            case 5:
                                mPokemon.setSpdIV(Integer.parseInt(edit.getText().toString()));
                                speIvs.setText(edit.getText());
                                speIvs.invalidate();
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private int getHPIndex() {
        return 0;
    }

    private int dipToPixels(float dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private int getEVBarColor(int value) {
        return Color.HSVToColor(new float[]{(float) value, 0.91f, 0.83f});
    }

    public class SetPokemonStats {

        public void setPokemonStats(int[] stats) {
            mPokemon.setHP(stats[0]);
            mPokemon.setAtk(stats[1]);
            mPokemon.setDef(stats[2]);
            mPokemon.setSpAtk(stats[3]);
            mPokemon.setSpDef(stats[4]);
            mPokemon.setSpd(stats[5]);
            hpStats.setText("" + stats[0]);
            hpStats.invalidate();
            atkStats.setText("" + stats[1]);
            atkStats.invalidate();
            defStats.setText("" + stats[2]);
            defStats.invalidate();
            spAtkStats.setText("" + stats[3]);
            spAtkStats.invalidate();
            spDefStats.setText("" + stats[4]);
            spDefStats.invalidate();
            speStats.setText("" + stats[5]);
            speStats.invalidate();
            TeamBuildingActivity.firePokemonSwapping(mPokemon, position);
        }

        public void setPokemonEVs(int[] evs) {
            mPokemon.setEVs(evs);
            TeamBuildingActivity.firePokemonSwapping(mPokemon, position);
        }
    }
}
