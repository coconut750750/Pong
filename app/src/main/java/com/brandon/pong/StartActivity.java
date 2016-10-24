package com.brandon.pong;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    Button buttonSingle;
    Button buttonDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setTitle("Pong");

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonSingle = (Button)findViewById(R.id.buttonSingle);
        buttonDouble = (Button)findViewById(R.id.buttonDouble);

        buttonSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.GAME_TYPE, MainActivity.SINGLE_PLAYER);
                startActivity(intent);
            }
        });

        buttonDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment bluetoothFrag = new BluetoothFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, bluetoothFrag);
                fragmentTransaction.commit();
            }
        });
    }

}
