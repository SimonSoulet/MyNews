package com.soulet.simon.mynews2.controllers.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.soulet.simon.mynews2.R;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    private EditText queryTerm;
    private CheckBox art;
    private CheckBox business;
    private CheckBox entrepreneurs;
    private CheckBox politics;
    private CheckBox sports;
    private CheckBox travel;
    private TextView beginDate;
    private String formatBeginDate;
    private TextView endDate;
    private String formatEndDate;
    private Button search;
    private DatePickerDialog.OnDateSetListener dateSetListenerBegin;
    private DatePickerDialog.OnDateSetListener dateSetListenerEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        queryTerm = (EditText) findViewById(R.id.input_search_query);
        art = (CheckBox) findViewById(R.id.check_art);
        business = (CheckBox) findViewById(R.id.check_business);
        entrepreneurs = (CheckBox) findViewById(R.id.check_entrepreneurs);
        politics = (CheckBox) findViewById(R.id.check_politics);
        sports = (CheckBox) findViewById(R.id.check_sports);
        travel = (CheckBox) findViewById(R.id.check_travel);
        beginDate = (TextView) findViewById(R.id.begin_date);
        endDate = (TextView) findViewById(R.id.end_date);
        search = (Button) findViewById(R.id.search_button);
        this.configureToolbar();
        this.configureDatePickerDialogs();
        this.configureSearchButton();
    }

    private void configureToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void configureDatePickerDialogs(){
        beginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, dateSetListenerBegin, year, month, day);
                dialog.show();
            }
        });

        dateSetListenerBegin = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int lengthMonth = String.valueOf(month).length();
                int lengthDay = String.valueOf(dayOfMonth).length();
                if(lengthMonth == 1){
                    if(lengthDay == 1){
                        beginDate.setText("0"+dayOfMonth+"/0"+(month+1)+"/"+year);
                        formatBeginDate = String.valueOf(year)+"0"+String.valueOf((month+1))+"0"+String.valueOf(dayOfMonth);
                    }else{
                        beginDate.setText(dayOfMonth+"/0"+(month+1)+"/"+year);
                        formatBeginDate = String.valueOf(year)+"0"+String.valueOf((month+1))+String.valueOf(dayOfMonth);
                    }
                }else{
                    if(lengthDay == 1){
                        beginDate.setText("0"+dayOfMonth+"/"+(month+1)+"/"+year);
                        formatBeginDate = String.valueOf(year)+String.valueOf((month+1))+"0"+String.valueOf(dayOfMonth);
                    }else{
                        beginDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        formatBeginDate = String.valueOf(year)+String.valueOf((month+1))+String.valueOf(dayOfMonth);
                    }
                }
            }
        };

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SearchActivity.this, dateSetListenerEnd, year, month, day);
                dialog.show();
            }
        });

        dateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int lengthMonth = String.valueOf(month).length();
                int lengthDay = String.valueOf(dayOfMonth).length();
                if(lengthMonth == 1){
                    if(lengthDay == 1){
                        endDate.setText("0"+dayOfMonth+"/0"+(month+1)+"/"+year);
                        formatEndDate = String.valueOf(year)+"0"+String.valueOf((month+1))+"0"+String.valueOf(dayOfMonth);
                    }else{
                        endDate.setText(dayOfMonth+"/0"+(month+1)+"/"+year);
                        formatEndDate = String.valueOf(year)+"0"+String.valueOf((month+1))+String.valueOf(dayOfMonth);
                    }
                }else{
                    if(lengthDay == 1){
                        endDate.setText("0"+dayOfMonth+"/"+(month+1)+"/"+year);
                        formatEndDate = String.valueOf(year)+String.valueOf((month+1))+"0"+String.valueOf(dayOfMonth);
                    }else{
                        endDate.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        formatEndDate = String.valueOf(year)+String.valueOf((month+1))+String.valueOf(dayOfMonth);
                    }
                }
            }
        };
    }

    private void configureSearchButton(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(queryTerm.getText().toString());
                System.out.println("begin :"+formatBeginDate+" - end :"+formatEndDate);
                if(queryTerm.getText().length() > 0){
                    String query = queryTerm.getText().toString();
                    if(isCheckboxChecked(art)){
                        query += " art";
                    }
                    if(isCheckboxChecked(business)){
                        query += " business";
                    }
                    if(isCheckboxChecked(entrepreneurs)){
                        query += " entrepreneurs";
                    }
                    if(isCheckboxChecked(politics)){
                        query += " politics";
                    }
                    if(isCheckboxChecked(sports)){
                        query += " sport";
                    }
                    if(isCheckboxChecked(travel)){
                        query += " travel";
                    }
                    if(isAllCheckboxChecked()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        builder.setMessage("check at least one category");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create();
                        builder.show();
                    }else{
                        System.out.println(query);
                        Intent searchResultIntent = new Intent(SearchActivity.this, SearchResultActivity.class);
                        searchResultIntent.putExtra(SearchResultActivity.QueryTerm, query);
                        searchResultIntent.putExtra(SearchResultActivity.BeginDate, formatBeginDate);
                        searchResultIntent.putExtra(SearchResultActivity.EndDate, formatEndDate);
                        startActivity(searchResultIntent);
                    }
                }else{
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(SearchActivity.this);
                    builder2.setMessage("fill query term");
                    builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder2.create();
                    builder2.show();
                }
            }
        });
    }

    private boolean isCheckboxChecked(CheckBox checkBox){
        if(checkBox.isChecked()){
            return true;
        }else{
            return false;
        }
    }

    private boolean isAllCheckboxChecked(){
        if(!art.isChecked() && !business.isChecked() && !entrepreneurs.isChecked() && !politics.isChecked() && !sports.isChecked() && !travel.isChecked()){
            return true;
        }else{
            return false;
        }
    }
}
