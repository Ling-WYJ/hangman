package uva.verspeek.hangman.start;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Toast;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.gameplay.MainActivity;
import uva.verspeek.hangman.settings.SettingsActivity;

import static android.graphics.Color.parseColor;

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);
        final View view = View.inflate(this, R.layout.activity_start, null);
        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(3000);
        view.startAnimation(aa);

        Bundle newGame = getIntent().getExtras();
        if (newGame != null) {

            boolean win = this.getIntent().getExtras().getBoolean("win");
            if (!win) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(StartActivity.this);
                builder.setTitle("Sorry that you didn't succeed in rescuing Li Hua");
                builder.setMessage("Li Hua is in danger again. Try to save him again!");
                builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(StartActivity.this, "Good luck", Toast.LENGTH_SHORT).show();
                        Intent gameIntent = new Intent(StartActivity.this,
                                MainActivity.class);
                        gameIntent.putExtra("newgame", "newgame");
                        StartActivity.this.startActivity(gameIntent);
                        StartActivity.this.finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        }
        else {

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(StartActivity.this);
            builder.setTitle("Your mission");
            builder.setMessage("Type in the letters. Guess the correct English word. Stop the tragedy from happening.");
            builder.setPositiveButton("Get it", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(StartActivity.this, "Good luck.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }

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
                StartActivity.this.finish();
            }
        });

        continueDialogActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);
                StartActivity.this.finish();
            }
        });


    }
}