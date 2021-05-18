package uva.verspeek.hangman.start;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.gameplay.MainActivity;
import uva.verspeek.hangman.settings.SettingsActivity;

import static android.graphics.Color.parseColor;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        Button startNormalActivity = (Button) findViewById(R.id.start_normal_activity);
        Button startDialogActivity = (Button) findViewById(R.id.start_dialog_activity);
        Button continueDialogActivity = (Button) findViewById(R.id.continue_dialog_activity);
        startNormalActivity.getBackground().setAlpha(75);
        startDialogActivity.getBackground().setAlpha(75);
        continueDialogActivity.getBackground().setAlpha(75);


        startNormalActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        Log.d("start", "2");

        startDialogActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.putExtra("newgame", "newgame");
                startActivity(intent);
            }
        });

        continueDialogActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}