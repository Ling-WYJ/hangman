package uva.verspeek.hangman.gameplay;

import java.util.ArrayList;
import java.util.Random;

import uva.verspeek.hangman.R;
import uva.verspeek.hangman.animation.Animation;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GamePlay {
	protected MainActivity context;
	protected String noDuplicates;
	protected Random r = new Random();
	public GamePlay(Context context) {
		this.context = (MainActivity) context;
	}

	public void win() {

		GridView keyboard = (GridView) context.findViewById(R.id.grid);
		keyboard.setOnItemClickListener(null);
		context.soundPool.play(context.soundMap.get(4), 1, 1, 0, 0, 1);
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("You saved LiHua successfully!");
		alert.setMessage("You have made "
				+ getMistakes()
				+ "mistakes\nCongratulations on your success in the high score list of Lihua rescue！\nYour final score: "
				+ context.cls.getScore(context.randomWord, context.maxMoves,
						getMistakes()) + "\nPlease leave your name：");

		// Set an EditText view to get user input
		final EditText name = new EditText(context);
		int maxLength = 10;
		name.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				maxLength) });
		alert.setView(name);
		alert.setCancelable(false);

		alert.setPositiveButton("submit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String user = "" + name.getText();
						if (user == "")
							user = "Anonymous";
						context.cls.setHighScore(user, context.cls.getScore(
								context.randomWord, context.maxMoves,
								getMistakes()), context.randomWord,
								context.gamePrefs);
						context.cls.startHighscore(context);
						return;
					}
				});

		alert.show();
	}

	public void lose() {
		RelativeLayout layout = (RelativeLayout) context
				.findViewById(R.id.layout);
		for (int i = 0; i < layout.getChildCount(); i++) {

			View child = layout.getChildAt(i);
			child.setEnabled(false);
		}
		GridView keyboard = (GridView) context.findViewById(R.id.grid);
		for (int i = 0; i < keyboard.getChildCount(); i++) {

			View child = keyboard.getChildAt(i);
			child.setEnabled(false);
		}
		context.soundPool.play(context.soundMap.get(3), 1, 1, 0, 0, 1);
		Toast.makeText(context.getApplicationContext(),
				"Unfortunately,\n You didn't make it to save Li Hua.\nThe word is " + context.randomWord,
				Toast.LENGTH_LONG).show();

		Runnable r = new Runnable() {
			@Override
			public void run() {
				context.cls.startInitscore(context);
			}
		};

		Handler h = new Handler();

		// delay time in miliseconds. (before going to highscores, so you can
		// see dead hangman animation)
		h.postDelayed(r, 3500);

	}

	/* Get number of mistakes made */
	public int getMistakes() {
		int mistakes = 0;
		for (int i = 0; i < context.guessedLetters.size(); i++) {
			if (!context.randomWord.contains(context.guessedLetters.get(i)))
				mistakes++;
		}
		return mistakes;
	}

	public void newGame() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(context);
		context.maxMoves = settings.getInt("maxMoves", 10);
		context.wordLength = settings.getInt("wordLength", 9);
		context.randomWord = context.clw.getWord(context.wordLength,
				context.words).toUpperCase();
		context.moves = 0;
		context.guessedLetters = new ArrayList<String>();
		context.animationGroup = r.nextInt(2);
		TextView movesLeft = (TextView) context.findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (context.maxMoves - context.moves));
		context.populateButtons();
		showLetters();
	}

	public static String removeDuplicateChars(final String input) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			String currentChar = input.substring(i, i + 1);
			if (result.indexOf(currentChar) < 0) // if not contained
				result.append(currentChar);
		}
		return "" + result;
	}

	public void newGuess(String letter) {
		context.guessedLetters.add(letter);
		context.moves = getMistakes();
		String displayWord = context.randomWord;
		if (!displayWord.contains(letter)) {
			context.soundPool.play(context.soundMap.get(5), 1, 1, 0, 0, 1);
			Log.d("Input",":false");
		}
		else {
			context.soundPool.play(context.soundMap.get(2), 1, 1, 0, 0, 1);
			Log.d("Input",":true");
		}
		TextView movesLeft = (TextView) context.findViewById(R.id.moves);
		movesLeft.setText("Moves left: " + (context.maxMoves - context.moves));
		showLetters();
	}

	public void showLetters() {
		String displayWord = context.randomWord;
		noDuplicates = removeDuplicateChars(context.randomWord);
		boolean win = true;
		int flag = 0;
		for (int i = 0; i < noDuplicates.length(); i++) {
			Log.d("letter", "" + noDuplicates.charAt(i));
			if (!context.guessedLetters.contains("" + noDuplicates.charAt(i))) {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"_ ");
				win = false;
			} else {
				displayWord = displayWord.replace("" + noDuplicates.charAt(i),
						"" + noDuplicates.charAt(i) + " ");
			}
		}
		TextView t = (TextView) context.findViewById(R.id.word);
		t.setText(displayWord);

		/* get the correct hangman animation */
		int numberHangmans = 9;
		int frame = (int) (((float) (numberHangmans) / (context.maxMoves + 1.0)) * (context.moves));
		Log.d("FRAMES",
				""
						+ (((float) (numberHangmans) / (context.maxMoves + 1.0)) * (context.moves)));
		numberHangmans--;

		// make sure the last animation is always displayed on the last move
		if (frame == numberHangmans && context.moves != context.maxMoves)
			frame = numberHangmans - 1;
		if (frame > numberHangmans)
			frame = numberHangmans;
		if (context.moves == context.maxMoves && frame != numberHangmans) {
			frame = numberHangmans;
		}
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("hangmanid" + context.animationGroup + frame, "drawable",
				context.getPackageName());

		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resourceId);

		int frameCount = 9;
		if (frame == 5)
			frameCount = 13;
		Animation.hangman.setFramePeriod(200);
		Animation.hangman.setSpriteWidth(bitmap.getWidth() / frameCount);
		Animation.hangman.setSpriteHeight(bitmap.getHeight());
		Animation.hangman.setX(300);
		Animation.hangman.setY(150);
		Animation.hangman.setFrameNr(frameCount);
		Animation.hangman.setCurrentFrame(0);
		Animation.hangman.setSourceRect(new Rect(0, 0, Animation.hangman
				.getSpriteWidth(), Animation.hangman.getSpriteHeight()));
		Animation.hangman.setBitmap(bitmap);

		if (win) {
			if (context.firstmove) {
				context.cls.startHighscore(context);
			} else {
				win();
			}
		} else if (context.moves == context.maxMoves) {
			if (context.firstmove) {
				context.cls.startHighscore(context);
			} else {
				lose();
			}
		}

		context.firstmove = false;
	}

	public void giveTips() {
		String tipWord = new String();
		Random rand = new Random();
		for (int i = 0; i < noDuplicates.length(); i++) {
			if (context.guessedLetters.contains("" + noDuplicates.charAt(i))) {
				Log.d("tipGussed",String.valueOf(noDuplicates.charAt(i)));
			}else{
				tipWord = tipWord + noDuplicates.charAt(i);
			}
		}
		Log.d("tipWord", tipWord);//未猜过的正确字母
		int i = rand.nextInt(tipWord.length());//产生随机index
		Log.d("tipNum", String.valueOf(i));
		Log.d("tipLetter", "" + tipWord.charAt(i));
		//Toast.makeText(context.getApplicationContext(),
		// "Try the letter " + tipWord.charAt(i),
		//		Toast.LENGTH_LONG).show();
		context.guessedLetters.add(String.valueOf(tipWord.charAt(i)));
	}

	public void removeOne() {
		String tipWord = new String();
		String gussedOrCorrectLetter = new String();
		String removeLetter = new String();
		Random rand = new Random();
		for (int i = 0; i < noDuplicates.length(); i++) {
			if (context.guessedLetters.contains("" + noDuplicates.charAt(i))) {
				Log.d("tipGussed",String.valueOf(noDuplicates.charAt(i)));
			}else{
				tipWord = tipWord + noDuplicates.charAt(i);
			}
		}
		Log.d("tipWord", tipWord);//未猜过的正确字母
		//未猜过的正确字母或猜过的字母
		gussedOrCorrectLetter = tipWord + context.guessedLetters;
		for (char buttonChar = 'A'; buttonChar <= 'Z'; buttonChar++) {
			if (gussedOrCorrectLetter.contains("" + buttonChar)) {
				//Log.d("Gussed",String.valueOf(buttonChar));
			}else{
				removeLetter = removeLetter + buttonChar;
			}
		}
		int i = rand.nextInt(removeLetter.length());//产生随机index
		Log.d("removeNum", String.valueOf(i));
		Log.d("removeLetter", "" + removeLetter.charAt(i));
		//Toast.makeText(context.getApplicationContext(),
		// "Try the letter " + tipWord.charAt(i),
		//		Toast.LENGTH_LONG).show();
		context.guessedLetters.add(String.valueOf(removeLetter.charAt(i)));
	}

}
