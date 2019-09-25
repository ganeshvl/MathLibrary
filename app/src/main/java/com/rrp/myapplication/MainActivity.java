package com.rrp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.rrp.mathlibrary.MathLib;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.sumTotal);
        int sum = MathLib.getInstance().addNumbers(15,21);
        Toast.makeText(this, sum + "", Toast.LENGTH_SHORT).show();
        tv.setText(String.valueOf(sum));
    }
}
