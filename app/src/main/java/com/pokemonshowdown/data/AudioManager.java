package com.pokemonshowdown.data;

import android.media.MediaPlayer;
import android.widget.Toast;

import com.pokemonshowdown.application.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nunom on 28/01/2017.
 */

public class AudioManager {

    private static ExecutorService executor = Executors.newFixedThreadPool(6);

    public static void playPokemonCry(final String name) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String pokemon = name;
                    if (pokemon.contains("arceus")) {
                        pokemon = pokemon.substring(0, pokemon.indexOf("-"));
                    } else if (pokemon.contains("porygon")) {
                        pokemon = pokemon.replace("-", "");
                    }

                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource("http://play.pokemonshowdown.com/audio/cries/" + pokemon.replace("-alola", "") + ".mp3");
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mp.prepareAsync();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
