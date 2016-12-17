package com.pokemonshowdown.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pokemonshowdown.R;
import com.pokemonshowdown.activity.TeamBuilderActivity;
import com.pokemonshowdown.activity.TeamBuildingActivity;
import com.pokemonshowdown.application.MyApplication;
import com.pokemonshowdown.data.Pokemon;
import com.pokemonshowdown.data.PokemonTeam;
import com.pokemonshowdown.data.Tiering;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.grantland.widget.AutofitTextView;

/**
 * Created by McBeengs on 29/10/2016.
 */

public class PokemonTeamsAdapter extends RecyclerView.Adapter<PokemonTeamsAdapter.ViewHolder> {

    private Context mContext;
    private List<PokemonTeam> mPokemonTeamsList;

    public PokemonTeamsAdapter(Context context, List<PokemonTeam> pokemonTeamsList) {
        mContext = context;
        mPokemonTeamsList = pokemonTeamsList;
        Collections.sort(mPokemonTeamsList, new Comparator<PokemonTeam>() {
            @Override
            public int compare(PokemonTeam t1, PokemonTeam t2) {
                return t1.getNickname().compareTo(t2.getNickname());
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View taskView = inflater.inflate(R.layout.fragment_team_builder_team_row, parent, false);
        return new ViewHolder(taskView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PokemonTeam p = mPokemonTeamsList.get(position);

        if (p.getNickname().isEmpty()) {
            holder.teamNickname.setText("Team nº" + (position + 1));
        } else {
            holder.teamNickname.setText(p.getNickname());
        }

        holder.pokemonsList.removeAllViews();
        for (Pokemon pokemon : p.getPokemons()) {
            if (pokemon != null) {
                ImageView image = new ImageView(mContext);
                int smallIconId = pokemon.getIcon();
                Drawable d = mContext.getResources().getDrawable(smallIconId);
                image.setImageDrawable(d);
                holder.pokemonsList.addView(image);
            }
        }

        holder.tierLabel.setText(p.getTier());

        for (int i = 0; i < 6 - p.getPokemons().size(); i++) {
            ImageView image = new ImageView(mContext);
            Drawable d = mContext.getResources().getDrawable(R.drawable.smallicons_0);
            image.setImageDrawable(d);
            holder.pokemonsList.addView(image);
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(mContext).setTitle("Delete Pokémon")
                        .setMessage("Are you sure you want to delete \"" + holder.teamNickname.getText() + "\"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int uh) {
                                Toast.makeText(mContext, "\"" + holder.teamNickname.getText() + "\" removed", Toast.LENGTH_SHORT).show();
                                TeamBuilderActivity.deleteTeam(p);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return false;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TeamBuildingActivity.class);
                intent.putExtra("team", new Gson().toJson(p));
                mContext.startActivity(intent);
            }
        });

        holder.teamNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText name = new EditText(mContext);
                name.setText(holder.teamNickname.getText());
                new AlertDialog.Builder(mContext).setTitle("Change name")
                        .setView(name)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PokemonTeam temp = p;
                                p.setNickname(name.getText().toString());
                                int pos = TeamBuilderActivity.deleteTeam(temp);
                                TeamBuilderActivity.saveTeam(p, pos);
                                Toast.makeText(mContext, "Team renamed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });

        holder.tierLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Spinner spinner = new Spinner(mContext);
                int dp = 15;
                int pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
                spinner.setPadding(pixels, pixels, pixels, pixels);
                ArrayAdapter<String> formatsAdapter = new ArrayAdapter<>(mContext, R.layout.fragment_user_list, Tiering.PLAYABLE_TIERS);
                formatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(formatsAdapter);

                new AlertDialog.Builder(mContext).setTitle("Set tier")
                        .setView(spinner)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PokemonTeam temp = p;
                                p.setTier(MyApplication.toId((String) spinner.getSelectedItem()));
                                p.setTier((String) spinner.getSelectedItem());
                                holder.tierLabel.setText((String) spinner.getSelectedItem());
                                int pos = TeamBuilderActivity.deleteTeam(temp);
                                TeamBuilderActivity.saveTeam(p, pos);
                                Toast.makeText(mContext, "Tier updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPokemonTeamsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public AutofitTextView teamNickname;
        public TextView tierLabel;
        public LinearLayout pokemonsList;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            itemView.setClickable(true);

            teamNickname = (AutofitTextView) itemView.findViewById(R.id.team_nickname);
            tierLabel = (TextView) itemView.findViewById(R.id.tier_label);
            pokemonsList = (LinearLayout) itemView.findViewById(R.id.pokemon_small_icon_list);
        }
    }
}
