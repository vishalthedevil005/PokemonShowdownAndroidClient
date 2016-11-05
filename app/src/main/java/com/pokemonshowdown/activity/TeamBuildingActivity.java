package com.pokemonshowdown.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokemonshowdown.R;
import com.pokemonshowdown.adapter.PokemonAdapter;
import com.pokemonshowdown.adapter.SimpleStringAdapter;
import com.pokemonshowdown.application.MyApplication;
import com.pokemonshowdown.data.Pokemon;
import com.pokemonshowdown.data.PokemonTeam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by McBeengs on 29/10/2016.
 */

public class TeamBuildingActivity extends BaseActivity implements View.OnClickListener {

    private static PokemonTeam mTeam;
    private static RecyclerView mPokemonsRecycler;
    private FloatingActionButton addFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_building);
        setupToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String jsonMyObject = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("team");
        }

        if (!jsonMyObject.isEmpty()) {
            mTeam = new Gson().fromJson(jsonMyObject, PokemonTeam.class);
        } else {
            mTeam = new PokemonTeam();
        }

        mPokemonsRecycler = (RecyclerView) findViewById(R.id.pokemons_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mPokemonsRecycler.setLayoutManager(layoutManager);
        if (mTeam.getPokemons().size() <= 0) {
            List<String> list = new ArrayList<>();
            list.add("There are no mons here...");
            mPokemonsRecycler.setAdapter(new SimpleStringAdapter(list));
        } else {
            mPokemonsRecycler.setAdapter(new PokemonAdapter(getContext(), mTeam.getPokemons()));
        }

        addFab = (FloatingActionButton) findViewById(R.id.add_fab);
        addFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_fab:
                Intent intent = new Intent(getApplicationContext(), SearchableActivity.class);
                intent.putExtra(SearchableActivity.SEARCH_TYPE, SearchableActivity.REQUEST_CODE_SEARCH_POKEMON);
                intent.putExtra(SearchableActivity.CURRENT_TIER, mTeam.getTier());
                startActivityForResult(intent, SearchableActivity.REQUEST_CODE_SEARCH_POKEMON);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PokemonTeam.savePokemonTeams(getContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_POKEMON) {
                Pokemon pokemon = new Pokemon(getContext(), data.getExtras().getString("Search"));

                mTeam.addPokemon(pokemon);
                mPokemonsRecycler.setAdapter(new PokemonAdapter(getContext(), mTeam.getPokemons()));

                if (mTeam.isFull()) {
                    addFab.setVisibility(View.GONE);
                } else {
                    addFab.setVisibility(View.VISIBLE);
                }

                TeamBuilderActivity.saveOrUpdateTeam(mTeam);
//                if (mSelectedPos != -1) {
//                    mPokemonTeam.replacePokemon(mSelectedPos, pokemon);
//                } else {
//                    mPokemonTeam.addPokemon(pokemon);
//                }
//
//                if (mPokemonTeam.isFull()) {
//                    mFooterButton.setVisibility(View.GONE);
//                } else {
//                    mFooterButton.setVisibility(View.VISIBLE);
//                }
//
//                mPokemonListAdapter.notifyDataSetChanged();
//                ((TeamBuildingActivity) getActivity()).updateList();
//            } else if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_ITEM) {
//                String item = data.getExtras().getString("Search");
//
//                Pokemon pkmn = mPokemonTeam.getPokemon(mSelectedPos);
//                pkmn.setItem(item);
//
//                mPokemonListAdapter.notifyDataSetChanged();
//            } else if (requestCode == SearchableActivity.REQUEST_CODE_SEARCH_MOVES) {
//                String move = data.getExtras().getString("Search");
//
//                Pokemon pkmn = mPokemonTeam.getPokemon(mSelectedPos);
//                switch (mSelectedMove) {
//                    case 1:
//                        pkmn.setMove1(move);
//                        break;
//                    case 2:
//                        pkmn.setMove2(move);
//                        break;
//                    case 3:
//                        pkmn.setMove3(move);
//                        break;
//                    case 4:
//                        pkmn.setMove4(move);
//                        break;
//                    default:
//                        break;
//                }
//
//                // hiddenpower X : change IV'
//                // furstration : set 0 happiness
//                // return get max happinnes
//
//                if ("frustration".equals(move)) {
//                    pkmn.setHappiness(0);
//                    Toast.makeText(getContext(), getText(R.string.happiness_min), Toast.LENGTH_SHORT).show();
//                } else if ("return".equals(move)) {
//                    pkmn.setHappiness(255);
//                    Toast.makeText(getContext(), getText(R.string.happiness_max), Toast.LENGTH_SHORT).show();
//                } else if ("gyroball".equals(move) || "trickroom".equals(move)) {
//                    boolean hasHP = false;
//                    for (int i = 1; i <= 4; i++) {
//                        if (pkmn.getMove(i) != null && (pkmn.getMove(i).contains("hiddenpower"))) {
//                            hasHP = true;
//                            break;
//                        }
//                    }
//
//                    if (hasHP) {
//                        pkmn.setSpdIV(pkmn.getSpdIV() % 4);
//                    } else {
//                        pkmn.setSpdIV(0);
//                    }
//
//                    Toast.makeText(getContext(), getText(R.string.speediv_modified), Toast.LENGTH_SHORT).show();
//                } else if (move != null && move.contains("hiddenpower")) {
//                    if (move.length() > "hiddenpower".length()) {
//                        boolean needsLowSpeed = false;
//                        for (int i = 1; i <= 4; i++) {
//                            if (pkmn.getMove(i) != null && (pkmn.getMove(i).equals("gyroball") || pkmn.getMove(i).equals("trickroom"))) {
//                                needsLowSpeed = true;
//                                break;
//                            }
//                        }
//                        String hpType = move.substring("hiddenpower".length());
//                        switch (hpType) {
//                            case "bug":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "dark":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "dragon":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "electric":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "fighting":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(30);
//                                break;
//
//                            case "fire":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(30);
//                                break;
//
//                            case "flying":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(30);
//                                break;
//
//                            case "ghost":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "grass":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "ground":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "ice":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "poison":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "psychic":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(30);
//                                break;
//
//                            case "rock":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(30);
//                                break;
//
//                            case "steel":
//                                pkmn.setAtkIV(31);
//                                pkmn.setDefIV(31);
//                                pkmn.setSpAtkIV(31);
//                                pkmn.setSpDefIV(30);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            case "water":
//                                pkmn.setAtkIV(30);
//                                pkmn.setDefIV(30);
//                                pkmn.setSpAtkIV(30);
//                                pkmn.setSpDefIV(31);
//                                pkmn.setSpdIV(31);
//                                break;
//
//                            default:
//                                break;
//                        }
//                        if (needsLowSpeed) {
//                            pkmn.setSpdIV(pkmn.getSpdIV() % 4);
//                        }
//                        Toast.makeText(getContext(), getText(R.string.ivs_modified), Toast.LENGTH_SHORT).show();
//                    }
// }
            }
        }
    }

    public static void firePokemonSwapping(Pokemon pokemon, int position) {
        mTeam.removePokemon(position);
        mTeam.addPokemon(pokemon, position);
        mPokemonsRecycler.setAdapter(new PokemonAdapter(mPokemonsRecycler.getContext(), mTeam.getPokemons()));
        TeamBuilderActivity.saveOrUpdateTeam(mTeam);
    }

    public static void fireTeamSaving(int position) {
        mTeam.removePokemon(position);
        mPokemonsRecycler.setAdapter(new PokemonAdapter(mPokemonsRecycler.getContext(), mTeam.getPokemons()));
        TeamBuilderActivity.saveOrUpdateTeam(mTeam);
    }
}
