package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.os.Bundle;

import java.io.IOException;
import java.util.Calendar;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Intent;
import android.widget.Button;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public void change_activity(View v){
        String body;
        date = date_text.getText().toString();
        //pulse_1 =Integer.parseInt(pulse_1_text.getText().toString());
        //pulse_2 =Integer.parseInt(pulse_2_text.getText().toString());
        new MyTask().execute();
    }
    Button btn;
    String date;
    String[] sex = {"мужской", "женский"};
    EditText date_text, pulse_1_text, pulse_2_text;
    DatePicker picker;
    int mYear, mMonth, mDay, user_sex, pulse_1, pulse_2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        date_text = findViewById(R.id.editTextDate2);
        btn = findViewById(R.id.button4);
        pulse_1_text = findViewById(R.id.editTextNumber3);
        pulse_2_text = findViewById(R.id.editTextNumber4);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sex);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position==0){
                    user_sex=1;
                }
                else {
                    user_sex=2;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });}
    public void callDatePicker(View v) {
        final Calendar cal = Calendar.getInstance();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String editTextDateParam = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        date_text.setText(editTextDateParam);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    class MyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            String body = "";
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:5000")
                    .post(new FormBody.Builder()
                            .add("day", Integer.toString(mDay))
                            .add("month", Integer.toString(mMonth+1))
                            .add("year", Integer.toString(mYear))
                            .add("sex", Integer.toString(user_sex))
                            .add("m1", pulse_1_text.getText().toString())
                            .add("m2", pulse_2_text.getText().toString())
                            .build())
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            body = response.body().string();}
            catch (IOException ex){}
            return body;
        }
        @Override
        protected void onPostExecute(String param){
            if(param.equals("<!DOCTYPE html> \n\r<meta charset=\"utf-8\"> \n\r<html><body> \n\rВведенные значения соответствуют отсутствию переутомления. \n\r</body></html>")){
                Intent intent = new Intent(MainActivity.this, answer.class);
                startActivity(intent);
            }
            else  if(param.equals("<!DOCTYPE html> \n\r<meta charset=\"utf-8\"> \n\r<html><body> \n\rВведенные значения соответствуют небольшому переутомлению. Рекомендуется снижение нагрузки. \n\r</body></html>")){
                Intent intent = new Intent(MainActivity.this, MildAnswerActivity.class);
                startActivity(intent);
            }
            else  if(param.equals("<!DOCTYPE html> \n\r<meta charset=\"utf-8\"> \n\r<html><body> \n\rВведенные значения соответствуют высокому уровню переутомления. Рекомендуется снижение нагрузки или отпуск. \n\r</body></html>")){
                Intent intent = new Intent(MainActivity.this, BadAnswerActivity.class);
                startActivity(intent);
            }
        }
    }
}
