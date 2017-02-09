package com.pokemonshowdown.data;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.widget.Toast;

import com.pokemonshowdown.R;
import com.pokemonshowdown.application.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nunom on 28/01/2017.
 */

public class AudioManager {

    // Allows more than one cry at any time. Helps prevents cries that are too long from being cut
    // 7 because 6 mons (Triples) + music
    private static ExecutorService executor = Executors.newFixedThreadPool(7);

    private static boolean isBgPlaying = false;
    private static MediaPlayer bg;
    private static ArrayList<String> rooms = new ArrayList<>();

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

    // The roomId is just to keep track of how many rooms are open, so that when the first exits
    // he doesn't stop the music for everybody
    public static void playBackgroundMusic(String roomId, final Context context) {
        if (!rooms.contains(roomId)) {
            rooms.add(roomId);
        }

        if (!isBgPlaying) {
            new Thread() {
                @Override
                public void run() {
                    int[] musics = new int[]{R.raw.sfx_bg_dpp_trainer, R.raw.sfx_bg_bw_wild, R.raw.sfx_bg_hgss_gym_leader,
                            R.raw.sfx_bg_oras_zinnia, R.raw.sfx_bg_rse_gym_leader, R.raw.sfx_bg_sm_trainer, R.raw.sfx_bg_sm_red_blue};
                    final int random = new Random().nextInt(musics.length);

                    bg = MediaPlayer.create(context, musics[random]);
                    bg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                            mp.start();
                        }
                    });
                    isBgPlaying = true;
                }
            }.start();
        }
    }

    public static void stopBackgroundMusic(String roomId) {
        if (rooms.contains(roomId)) {
            rooms.remove(roomId);
        }

        if (rooms.size() > 0) {
            return;
        } else {
            isBgPlaying = false;
            bg.stop();
        }
    }
}
