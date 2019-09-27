package com.soulet.simon.mynews2.controllers.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.soulet.simon.mynews2.R;
import com.soulet.simon.mynews2.utils.others.SearchArticlesReceiver;

import java.util.Calendar;

public class NotifsActivity extends AppCompatActivity {

    private EditText queryTerm;
    private CheckBox art;
    private CheckBox business;
    private CheckBox entrepreneurs;
    private CheckBox politics;
    private CheckBox sports;
    private CheckBox travel;
    private Switch enableNotif;

    private SharedPreferences preferences;
    public static final String PREF_KEY_QUERY = "PREF_KEY_QUERY";
    public static final String PREF_KEY_ART = "PREF_KEY_ART";
    public static final String PREF_KEY_BUSINESS = "PREF_KEY_BUSINESS";
    public static final String PREF_KEY_ENTREPRENEURS = "PREF_KEY_ENTREPRENEURS";
    public static final String PREF_KEY_POLITICS = "PREF_KEY_POLITICS";
    public static final String PREF_KEY_SPORTS = "PREF_KEY_SPORTS";
    public static final String PREF_KEY_TRAVEL = "PREF_KEY_TRAVEL";
    public static final String PREF_KEY_SWITCH = "PREF_KEY_SWITCH";

    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifs);
        queryTerm = (EditText) findViewById(R.id.input_search_query);
        art = (CheckBox) findViewById(R.id.check_art);
        business = (CheckBox) findViewById(R.id.check_business);
        entrepreneurs = (CheckBox) findViewById(R.id.check_entrepreneurs);
        politics = (CheckBox) findViewById(R.id.check_politics);
        sports = (CheckBox) findViewById(R.id.check_sports);
        travel = (CheckBox) findViewById(R.id.check_travel);
        enableNotif = (Switch) findViewById(R.id.switch_notif);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.displayPreferencesUser();
        this.configureToolbar();
        this.configureSwitch();
    }

    private void displayPreferencesUser(){
        String query = preferences.getString(PREF_KEY_QUERY, "");
        queryTerm.setText(query);
        queryTerm.setSelection(query.length());
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_ART, false), art);
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_BUSINESS, false), business);
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_ENTREPRENEURS, false), entrepreneurs);
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_POLITICS, false), politics);
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_SPORTS, false), sports);
        this.getPreferenceUser(preferences.getBoolean(PREF_KEY_TRAVEL, false), travel);
        boolean checkSwitch = preferences.getBoolean(PREF_KEY_SWITCH, false);
        if(checkSwitch){
            enableNotif.setChecked(true);
        }else{
            enableNotif.setChecked(false);
        }
    }

    private void getPreferenceUser(boolean preferences, CheckBox checkBox){
        if(preferences){
            checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }
    }

    private void configureToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureSwitch(){
        enableNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(queryTerm.getText().length() > 0 && isAllCheckboxChecked()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(NotifsActivity.this);
                        builder.setMessage("fill query term and check at least one category");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create();
                        builder.show();
                        enableNotif.setChecked(false);
                    }else{
                        preferences.edit().putString(PREF_KEY_QUERY, queryTerm.getText().toString()).apply();
                        preferences.edit().putBoolean(PREF_KEY_ART, art.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_BUSINESS, business.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_ENTREPRENEURS, entrepreneurs.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_POLITICS, politics.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_SPORTS, sports.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_TRAVEL, travel.isChecked()).apply();
                        preferences.edit().putBoolean(PREF_KEY_SWITCH, true).apply();
                        configureAlarmManager();
                    }
                }else{
                    preferences.edit().putBoolean(PREF_KEY_SWITCH, false).apply();
                    cancelAlarmManager();
                }

            }
        });
    }

    private boolean isAllCheckboxChecked(){
        if(!art.isChecked() && !business.isChecked() && !entrepreneurs.isChecked() && !politics.isChecked() && !sports.isChecked() && !travel.isChecked()){
            return true;
        }else{
            return false;
        }
    }

    private void configureAlarmManager(){
        Intent alarmIntent = new Intent(NotifsActivity.this, SearchArticlesReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(NotifsActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //calendar.set(Calendar.HOUR_OF_DAY, 7);
        //calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis()+(60*1000), 60*1000, pendingIntent);
    }

    private void cancelAlarmManager(){
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
