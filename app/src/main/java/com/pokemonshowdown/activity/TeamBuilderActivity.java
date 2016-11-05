package com.pokemonshowdown.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pokemonshowdown.R;
import com.pokemonshowdown.adapter.PokemonTeamsAdapter;
import com.pokemonshowdown.adapter.SimpleStringAdapter;
import com.pokemonshowdown.application.MyApplication;
import com.pokemonshowdown.data.PokemonTeam;
import com.pokemonshowdown.data.Tiering;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TeamBuilderActivity extends BaseActivity {
    private final static String TAG = TeamBuilderActivity.class.getName();
    private final static int CLIPBOARD = 0;
    private final static int PASTEBIN = 1;
    private final static int QR = 2;

    private static List<PokemonTeam> mPokemonTeamList;
    private RecyclerView mRecyclerTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_team_builder);
        setupToolbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        PokemonTeam.loadPokemonTeams(getContext());
        setupRecyclerTeams();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupRecyclerTeams();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.team_building, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int position;
        PokemonTeam pt;
        final PokemonTeam pt2;
        AlertDialog.Builder builder;
        AlertDialog alert;

        switch (item.getItemId()) {
            case R.id.action_create_team:
                pt = new PokemonTeam();
                pt.setNickname("Team nº" + (mPokemonTeamList.size() + 1));
                pt.setTier(Tiering.TIER_ORDER.get(1));
                saveOrUpdateTeam(pt);
//                if (mPokemonTierSpinner.getSelectedItem() != null) {
//                    pt.setTier((String) mPokemonTierSpinner.getSelectedItem());
//                }
//                pt.setNickname("Team #" + mPokemonTeamList.size());
//                mPokemonTeamListArrayAdapter.notifyDataSetChanged();
//                mPokemonTeamSpinner.setSelection(mPokemonTeamList.size() - 1);
                mRecyclerTeams.setAdapter(new PokemonTeamsAdapter(getContext(), mPokemonTeamList));
                Toast.makeText(getApplicationContext(), R.string.team_created, Toast.LENGTH_SHORT).show();
                return true;
//            case R.id.action_remove_team:
//                position = mPokemonTeamSpinner.getSelectedItemPosition();
//                if (position != AdapterView.INVALID_POSITION) {
//                    mPokemonTeamList.remove(position);
//                    mPokemonTeamListArrayAdapter.notifyDataSetChanged();
//
//                    if (mPokemonTeamList.size() > 0) {
//                        mPokemonTeamSpinner.setSelection(0, false);
//                        PokemonTeam pt3 = mPokemonTeamList.get(0);
//                        TeamBuildingFragment fragment = TeamBuildingFragment.newInstance(pt3);
//                        getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.teambuilding_fragmentcontainer, fragment, "")
//                                .commit();
//                    } else {
//                        getSupportFragmentManager().beginTransaction().
//                                remove(getSupportFragmentManager().findFragmentById(R.id.teambuilding_fragmentcontainer)).commit();
//                    }
//                    Toast.makeText(getApplicationContext(), R.string.team_removed, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.team_removed_none, Toast.LENGTH_SHORT).show();
//                }
//                return true;
            case R.id.action_export_team:
//                builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.export_title);
//                builder.setItems(getResources().getStringArray(R.array.export_import_sources),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                if (item == CLIPBOARD) {
//                                    int position = mPokemonTeamSpinner.getSelectedItemPosition();
//                                    if (position != AdapterView.INVALID_POSITION) {
//                                        PokemonTeam pt = mPokemonTeamList.get(position);
//                                        ClipboardManager clipboard = (ClipboardManager)
//                                                getSystemService(Context.CLIPBOARD_SERVICE);
//                                        ClipData clip = ClipData.newPlainText(pt.getNickname(), pt.exportPokemonTeam(getApplicationContext()));
//                                        clipboard.setPrimaryClip(clip);
//                                        Toast.makeText(getApplicationContext(), R.string.team_exported, Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), R.string.team_exported_none, Toast.LENGTH_SHORT).show();
//                                    }
//                                } else if (item == PASTEBIN) {
//                                    int position = mPokemonTeamSpinner.getSelectedItemPosition();
//                                    if (position != AdapterView.INVALID_POSITION) {
//                                        PokemonTeam pt = mPokemonTeamList.get(position);
//                                        String exportData = pt.exportPokemonTeam(getApplicationContext());
//                                        new PastebinTask(PastebinTaskId.EXPORT).execute(exportData);
//                                    }
//                                } else if (item == QR) {
//                                    int position = mPokemonTeamSpinner.getSelectedItemPosition();
//                                    if (position != AdapterView.INVALID_POSITION) {
//                                        PokemonTeam pt = mPokemonTeamList.get(position);
//                                        String exportData = pt.exportPokemonTeam(getApplicationContext());
//                                        PastebinTask pastebinTask = new PastebinTask(PastebinTaskId.EXPORT_FOR_QR);
//                                        pastebinTask.execute(exportData);
//                                    }
//                                }
//                            }
//                        }
//
//                );
//                alert = builder.create();
//                alert.show();
                return true;
            case R.id.action_import_team:
//                builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.import_title);
//                builder.setItems(getResources().getStringArray(R.array.export_import_sources),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int item) {
//                                if (item == CLIPBOARD) {
//                                    ClipboardManager clipboard = (ClipboardManager)
//                                            getSystemService(Context.CLIPBOARD_SERVICE);
//                                    ClipData importClip = clipboard.getPrimaryClip();
//                                    if (importClip != null) {
//                                        ClipData.Item clipItem = importClip.getItemAt(0);
//                                        // Gets the clipboard as text.
//                                        String pasteData = clipItem.getText().toString();
//                                        PokemonTeam pt = PokemonTeam.importPokemonTeam(pasteData, getApplicationContext(), true);
//                                        if (pt != null && pt.getTeamSize() > 0) {
//                                            mPokemonTeamList.add(pt);
//                                            pt.setNickname("Imported Team");
//                                            mPokemonTeamSpinner.setSelection(mPokemonTeamList.size() - 1);
//                                            Toast.makeText(getApplicationContext(), R.string.team_imported, Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), R.string.team_imported_invalid_data, Toast.LENGTH_SHORT).show();
//                                        }
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), R.string.team_imported_empty, Toast.LENGTH_SHORT).show();
//                                    }
//                                } else if (item == PASTEBIN) {
//                                    final AlertDialog.Builder urlDialog = new AlertDialog.Builder(TeamBuilderActivity.this);
//                                    urlDialog.setTitle(R.string.url_dialog_title);
//                                    final EditText urlEditText = new EditText(TeamBuilderActivity.this);
//                                    urlEditText.setText(R.string.pastebin_url);
//                                    urlEditText.setSelection(urlEditText.getText().length());
//                                    urlDialog.setView(urlEditText);
//
//                                    urlDialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            new PastebinTask(PastebinTaskId.IMPORT).execute(urlEditText.getText().toString());
//                                        }
//                                    });
//
//                                    urlDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface arg0, int arg1) {
//                                            arg0.dismiss();
//                                        }
//                                    });
//
//                                    urlDialog.show();
//                                } else if (item == QR) {
//                                    IntentIntegrator integrator = new IntentIntegrator(TeamBuilderActivity.this);
//                                    integrator.initiateScan();
//                                }
//                            }
//                        }
//
//                );
//                alert = builder.create();
//                alert.show();
                return true;
//            case R.id.action_rename_team:
//                position = mPokemonTeamSpinner.getSelectedItemPosition();
//                if (position != AdapterView.INVALID_POSITION) {
//                    pt2 = mPokemonTeamList.get(position);
//                    AlertDialog.Builder renameDialog = new AlertDialog.Builder(TeamBuildingActivity.this);
//                    renameDialog.setTitle(R.string.rename_pokemon);
//                    final EditText teamNameEditText = new EditText(TeamBuildingActivity.this);
//                    teamNameEditText.setText(pt2.getNickname());
//                    renameDialog.setView(teamNameEditText);
//
//                    renameDialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            pt2.setNickname(teamNameEditText.getText().toString());
//                            mPokemonTeamListArrayAdapter.notifyDataSetChanged();
//                            Toast.makeText(getApplicationContext(), R.string.team_renamed, Toast.LENGTH_SHORT).show();
//                            arg0.dismiss();
//                        }
//                    });
//
//                    renameDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            arg0.dismiss();
//                        }
//                    });
//
//                    renameDialog.show();
//                }
//
//                return true;
            case R.id.action_share_team:
//                position = mPokemonTeamSpinner.getSelectedItemPosition();
//                if (position != AdapterView.INVALID_POSITION) {
//                    pt2 = mPokemonTeamList.get(position);
//                    Intent sendIntent = new Intent();
//                    sendIntent.setAction(Intent.ACTION_SEND);
//                    sendIntent.putExtra(Intent.EXTRA_TEXT, pt2.exportPokemonTeam(getApplicationContext()));
//                    sendIntent.setType("text/plain");
//                    startActivity(sendIntent);
//                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupRecyclerTeams() {
        mPokemonTeamList = PokemonTeam.getPokemonTeamList();

        mRecyclerTeams = (RecyclerView) findViewById(R.id.recycler_teams);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerTeams.setLayoutManager(layoutManager);
        if (mPokemonTeamList.size() <= 0) {
            List<String> list = new ArrayList<>();
            list.add("There are no teams here...");
            mRecyclerTeams.setAdapter(new SimpleStringAdapter(list));
        } else {
            mRecyclerTeams.setAdapter(new PokemonTeamsAdapter(getContext(), mPokemonTeamList));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            //  here weh ave the url read from the barcode
            String url = scanResult.getContents();
            if (url == null) {
                return;
            }
            new PastebinTask(PastebinTaskId.IMPORT).execute(url);
        } else {
            //passing to the fragment below (for searchable pokemons/moves ...  etc etc)
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    public static void saveOrUpdateTeam(PokemonTeam team) {
        for (PokemonTeam t : mPokemonTeamList) {
            if (t.getNickname().equals(team.getNickname())) {
                mPokemonTeamList.remove(t);
            }
        }
        mPokemonTeamList.add(team);
        PokemonTeam.savePokemonTeams(MyApplication.getMyApplication());
    }

    public static void saveTeam(PokemonTeam team, int position) {
        mPokemonTeamList.add(position, team);
        PokemonTeam.savePokemonTeams(MyApplication.getMyApplication());
    }

    public static int deleteTeam(PokemonTeam team) {
        int i = 0;
        for (PokemonTeam t : mPokemonTeamList) {
            if (t.getNickname().equals(team.getNickname())) {
                break;
            }
            i++;
        }
        mPokemonTeamList.remove(team);
        PokemonTeam.savePokemonTeams(MyApplication.getMyApplication());
        return i;
    }

    private enum PastebinTaskId {
        IMPORT, EXPORT, EXPORT_FOR_QR // for QR
    }

    private class PastebinTask extends AsyncTask<String, Void, String> {
        private final static String PASTEBIN_RAW = "http://pastebin.com/raw.php?i=";
        private final static String PASTEBIN_API = "http://pastebin.com/api/api_post.php";
        private final static String API_DEV_KEY_KEY = "api_dev_key";
        private final static String API_DEV_KEY_VALUE = "027d7160b253fbcae3d91ff407ea82a6";
        private final static String API_OPTION_KEY = "api_option";
        private final static String API_OPTION_VALUE = "paste";
        private final static String PASTE_DATA = "api_paste_code";
        private final static String ENCODING = "UTF-8";

        private PastebinTaskId mTask;
        // TODO use that exception one day
        private Exception mException;
        private boolean success;
        private ProgressDialog waitingDialog;

        public PastebinTask(PastebinTaskId task) {
            mTask = task;
            waitingDialog = new ProgressDialog(TeamBuilderActivity.this);
            waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            waitingDialog.setCancelable(false);
            switch (mTask) {
                case EXPORT_FOR_QR:
                    waitingDialog.setMessage(getResources().getString(R.string.exportQR_inprogress));
                    break;

                case EXPORT:
                    waitingDialog.setMessage(getResources().getString(R.string.export_inprogress));
                    break;

                case IMPORT:
                    waitingDialog.setMessage(getResources().getString(R.string.import_inprogress));
                    break;
            }
        }

        protected String doInBackground(String... strings) {
            String data = strings[0];
            String out = null;
            switch (mTask) {
                case EXPORT_FOR_QR:
                case EXPORT:
                    out = exportToPastebin(data);
                    break;

                case IMPORT:
                    importFromPastebin(data);
                    break;
            }

            return out;
        }

        @Override
        protected void onPreExecute() {
            TeamBuilderActivity.this.runOnUiThread(new java.lang.Runnable() {
                public void run() {
                    waitingDialog.show();
                }
            });
        }

        @Override
        protected void onPostExecute(String aString) {
            if (success) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeamBuilderActivity.this);
                TeamBuilderActivity.this.runOnUiThread(new java.lang.Runnable() {
                    public void run() {
                        waitingDialog.dismiss();
                    }
                });
                switch (mTask) {
                    case EXPORT:
                        builder.setMessage(aString);
                        builder.setTitle(R.string.export_success_dialog_title);
                        break;
                    case IMPORT:
                        builder.setTitle(R.string.import_success_dialog_title);
                        break;
                    case EXPORT_FOR_QR:
                        IntentIntegrator integrator = new IntentIntegrator(TeamBuilderActivity.this);
                        integrator.shareText(aString);
                        return;
                }
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setPositiveButton(R.string.dialog_ok, null);
                final AlertDialog alert = builder.create();
                TeamBuilderActivity.this.runOnUiThread(new java.lang.Runnable() {
                    public void run() {
                        alert.show();
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(TeamBuilderActivity.this);
                builder.setTitle(R.string.error_dialog_title);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                switch (mTask) {
                    case EXPORT_FOR_QR:
                    case EXPORT:
                        if (aString == null) {
                            builder.setMessage(R.string.export_error_dialog_message);
                        } else {
                            builder.setMessage(aString);
                        }
                        break;
                    case IMPORT:
                        builder.setMessage(R.string.import_error_dialog_message);
                        break;
                }
                builder.setPositiveButton(R.string.dialog_ok, null);
                final AlertDialog alert = builder.create();
                TeamBuilderActivity.this.runOnUiThread(new java.lang.Runnable() {
                    public void run() {
                        waitingDialog.dismiss();
                        alert.show();
                    }
                });
            }
        }

        private String exportToPastebin(String data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(PASTEBIN_API);
            String outputURL;
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(API_DEV_KEY_KEY, API_DEV_KEY_VALUE));
                nameValuePairs.add(new BasicNameValuePair(API_OPTION_KEY, API_OPTION_VALUE));
                nameValuePairs.add(new BasicNameValuePair(PASTE_DATA, data));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                outputURL = EntityUtils.toString(entity, ENCODING);
                if (outputURL.startsWith("http://pastebin.com/")) {
                    success = true;
                } else {
                    //export error (post limit reached)
                    success = false;
                }
            } catch (IOException e) {
                outputURL = null;
                mException = e;
                success = false;
            }
            return outputURL;
        }

        private void importFromPastebin(String url) {
            if (url.startsWith("http://")) {
                url = url.substring("http://".length());
            }
            if (!url.startsWith("pastebin.com/")) {
                success = false;
                return;
            }
            String[] split = url.split("/");
            String pastebinId = split[1];

            String finalUrl = PASTEBIN_RAW + pastebinId;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(finalUrl);
            HttpResponse response = null;
            String pastebinOut = null;
            try {
                response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                pastebinOut = EntityUtils.toString(entity, ENCODING);
            } catch (IOException e) {
                mException = e;
                success = false;
                return;
            }
            PokemonTeam pokemonTeam = PokemonTeam.importPokemonTeam(pastebinOut, TeamBuilderActivity.this, false);
            if (pokemonTeam != null && pokemonTeam.getTeamSize() > 0) {
                pokemonTeam.setNickname("Imported Team");
                mPokemonTeamList.add(pokemonTeam);
                success = true;
                return;
            } else {
                success = false;
                return;
            }
        }
    }
}
