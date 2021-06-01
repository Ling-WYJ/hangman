package uva.verspeek.hangman.gameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import uva.verspeek.hangman.R;
import uva.verspeek.hangman.animation.Animation;
import uva.verspeek.hangman.highscore.ControlScore;
import uva.verspeek.hangman.settings.SettingsActivity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.AudioManager;
import android.media.SoundPool;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity implements OnClickListener {
	
	public SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	public HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
	
	public ArrayList<String> words;
	List<String> guessedLetters;
	String randomWord;
	int moves;
	int maxMoves;
	int wordLength;
	int maxLength;
	boolean firstmove;
	int animationGroup;
	String thesaurus;
	ControlScore cls;
	ControlWords clw;
	GamePlay gameplay;
	public SharedPreferences gamePrefs;
	public static final String GAME_PREFS = "ArithmeticFile";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
		boolean isNightMode = getSharedPreferences(GAME_PREFS, 0).getBoolean("mode",false);

		if(isNightMode) {
			getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}

		soundMap.put(1, soundPool.load(this, R.raw.click, 1));
		soundMap.put(2, soundPool.load(this, R.raw.correct, 1));
		soundMap.put(3, soundPool.load(this, R.raw.lose, 1));
		soundMap.put(4, soundPool.load(this, R.raw.win, 1));
		soundMap.put(5, soundPool.load(this, R.raw.wrong, 1));
		
		ImageButton settingsButton = (ImageButton) findViewById(R.id.settings);
		ImageButton tipsButton = (ImageButton) findViewById(R.id.tips);
		ImageButton removeButton = (ImageButton) findViewById(R.id.remove);
		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent SettingsIntent = new Intent(MainActivity.this,
						SettingsActivity.class);
				MainActivity.this.startActivity(SettingsIntent);
				MainActivity.this.finish();
			}
		});
		tipsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				gameplay.giveTips();
				populateButtons();
				gameplay.showLetters();
			}
		});

		removeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				gameplay.removeOne();
				populateButtons();
				gameplay.showLetters();
			}
		});

		cls = new ControlScore();
		clw = new ControlWords();
		gameplay = new GamePlay(this);
		gamePrefs = getSharedPreferences(GAME_PREFS, 0);
		firstmove = true;
		thesaurus = gamePrefs.getString("Thesaurus","default");
		try {
			switch(thesaurus){
				case "CET4" :
					words = clw.populateWords(getAssets().open("CET4.xmf"));
					break;
				case "CET6" :
					words = clw.populateWords(getAssets().open("CET6.xmf"));
					break;
				case "IELTS" :
					words = clw.populateWords(getAssets().open("IELTS.xmf"));
					break;
				case "TOEFL" :
					words = clw.populateWords(getAssets().open("TOEFL.xmf"));
					break;
				case "default" :
					words = clw.populateWords(getAssets().open("words.xmf"));
					break;
				default:
					words = clw.populateWords(getAssets().open("words.xmf"));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("word",thesaurus);
		Bundle newGame = getIntent().getExtras();
		if (newGame != null) {
			gameplay.newGame();
		} else {
			guessedLetters = new ArrayList<String>(Arrays.asList(gamePrefs
					.getString("guessedLetters", "[]").replace("[", "")
					.replace("]", "").replace(" ", "").split(",")));
			// get the prefs object.
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			wordLength = settings.getInt("wordLength", 5);

			randomWord = gamePrefs.getString("word",
					clw.getWord(wordLength, words).toUpperCase());
			maxMoves = gamePrefs.getInt("moves",
					settings.getInt("maxMoves", 10));
			moves = gameplay.getMistakes();

			TextView movesLeft = (TextView) findViewById(R.id.moves);
			movesLeft.setText("Moves left: " + (maxMoves - moves));
			populateButtons();
			gameplay.showLetters();
		}

		if (maxMoves - moves <= 0) {
			cls.startHighscore(MainActivity.this);
		}
	}

	public void populateButtons() {

		GridView keyboard = (GridView) findViewById(R.id.grid);
		/* disable scrolling */
		keyboard.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		});

		/* add buttons to gridview */
		Button cb = null;
		ArrayList<Button> mButtons = new ArrayList<Button>();
		for (char buttonChar = 'A'; buttonChar <= 'Z'; buttonChar++) {
			cb = new Button(this);
			cb.setText("" + buttonChar);
			cb.setPadding(0, 0, 0, 0);
			cb.setId(buttonChar);
			cb.setBackgroundResource(R.drawable.shape);
			cb.setTextColor(Color.parseColor("black"));
			cb.setTextSize(25);
			cb.setOnClickListener(this);
			
			if (guessedLetters.contains("" + buttonChar)) {
				cb.setBackgroundResource(R.drawable.gussed);
				cb.setTextColor(Color.parseColor("#5a6168"));
				cb.setOnClickListener(null);
			}
			mButtons.add(cb);
		}

		keyboard.setAdapter(new CustomAdapter(mButtons));
	}

	@Override
	public void onClick(View v) {
		Button selection = (Button) v;
		selection.setBackgroundResource(R.drawable.gussed);
		selection.setTextColor(Color.parseColor("#5a6168"));
		selection.setOnClickListener(null);
		this.soundPool.play(this.soundMap.get(1), 1, 1, 0, 0, 1);
		gameplay.newGuess((String) selection.getText());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent SettingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(SettingsIntent);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Store values between instances here
		if (gamePrefs != null) {
			SharedPreferences.Editor editor = gamePrefs.edit();

			editor.putInt("moves", maxMoves);
			editor.putString("word", randomWord);
			editor.putString("guessedLetters", "" + guessedLetters);
			editor.putString("Thesaurus",thesaurus);
			editor.putInt("group",animationGroup);

			// Commit to storage
			editor.commit();
		}
	}

}
