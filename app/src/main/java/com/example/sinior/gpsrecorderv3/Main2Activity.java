package com.example.sinior.gpsrecorderv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    View saveBtn;
    EditText edName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);
        SharedPreferences sharedPref = getSharedPreferences("myStoredName", MODE_PRIVATE);

        String myStoredName = sharedPref.getString("myStoredName", null);
        if(myStoredName != null ){
            Intent myIntent = new Intent(this, MainActivity.class);
            this.startActivity(myIntent);
        }

        saveBtn = (View) findViewById(R.id.btnRegister);
        edName = (EditText) findViewById(R.id.edName);
        saveBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                System.out.println(edName.getText());
        }
    }
}
