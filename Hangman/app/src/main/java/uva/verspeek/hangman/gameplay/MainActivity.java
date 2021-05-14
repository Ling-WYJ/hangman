package uva.verspeek.hangman.gameplay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

public class MainActivity extends Activity implements OnClickListener {
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
	ControlScore cls;
	ControlWords clw;
	GamePlay gameplay;
	public SharedPreferences gamePrefs;//SharedPreferences是一种轻型的数据存储方式，它的本质是基于XML文件存储key-value键值对数据，通常用来存储一些简单的配置信息。
	public static final String GAME_PREFS = "ArithmeticFile";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);//需要软件全屏显示、自定义标题（使用按钮等控件）和其他的需求,设置没有标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);///设置窗体全屏

		setContentView(R.layout.activity_main);

		soundMap.put(1, soundPool.load(this, R.raw.click, 1));
		soundMap.put(2, soundPool.load(this, R.raw.correct, 1));
		soundMap.put(3, soundPool.load(this, R.raw.lose, 1));
		soundMap.put(4, soundPool.load(this, R.raw.win, 1));
		soundMap.put(5, soundPool.load(this, R.raw.wrong, 1));

		ImageButton settingsButton = (ImageButton) findViewById(R.id.settings);

		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent SettingsIntent = new Intent(MainActivity.this,
						SettingsActivity.class);
				MainActivity.this.startActivity(SettingsIntent);
				MainActivity.this.finish();
			}
		});
		cls = new ControlScore();
		clw = new ControlWords();
		gameplay = new GamePlay(this);
		gamePrefs = getSharedPreferences(GAME_PREFS, 0);//第一个参数是preferece的名称(比如：MyPref),第二个参数是打开的方式（一般选择private方式）
		//第一个参数为本组件的配置文件名(自定义)，当这个文件不存在时，直接创建，如果已经存在，则直接使用
		firstmove = true;
		try {
			words = clw.populateWords(getAssets().open("words.xmf"));
			//得到android项目assets目录中的内容，将文件中的所有单词存入words数组中
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bundle newGame = getIntent().getExtras();
		//GetIntent得到一个Intent，是指上一个activity启动的intent，这个方法返回intent对象，然后调用intent.getExtras（）得到intent所附带的额外数据
		if (newGame != null) {
			gameplay.newGame();
		} else {
			guessedLetters = new ArrayList<String>(Arrays.asList(gamePrefs
					.getString("guessedLetters", "[]").replace("[", "")
					.replace("]", "").replace(" ", "").split(",")));
			// get the prefs object.
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			//每个应用都有一个默认的配置文件preferences.xml，使用getDefaultSharedPreferences获取。
			wordLength = settings.getInt("wordLength", 9);

			randomWord = gamePrefs.getString("word",
					clw.getWord(wordLength, words).toUpperCase());  //通过调用controlwords中的getword方法随机获取一个长度指定的单词
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

			cb.setTextColor(Color.parseColor("black"));
			cb.setTextSize(25);

			cb.setOnClickListener(this);
			//cb.setBackgroundColor(Color.parseColor("red"));
			cb.setBackgroundResource(R.drawable.shape);
			if (guessedLetters.contains("" + buttonChar)) {
				//cb.setBackgroundColor(Color.parseColor("#858585"));
				cb.setBackgroundResource(R.drawable.gussed);
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

			// Commit to storage
			editor.commit();
		}
	}

}
