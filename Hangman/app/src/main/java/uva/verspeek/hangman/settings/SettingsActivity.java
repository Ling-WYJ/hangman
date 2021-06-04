package uva.verspeek.hangman.settings;


import uva.verspeek.hangman.R;
import uva.verspeek.hangman.R.id;
import uva.verspeek.hangman.R.layout;
import uva.verspeek.hangman.R.xml;
import uva.verspeek.hangman.gameplay.MainActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;


public class SettingsActivity extends PreferenceActivity {
	ArrayAdapter<String> adapter;
	public SharedPreferences gamePrefs;
	public static final String GAME_PREFS = "ArithmeticFile";
	private per.stramchen.switchbuttondemo.SwitchButton switchButton;
	private AppCompatDelegate mDelegate;



	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getDelegate().installViewFactory();
		getDelegate().onCreate(savedInstanceState);
		boolean isNightMode = getSharedPreferences(GAME_PREFS, 0).getBoolean("mode",false);
		if(isNightMode) {
			getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
		addPreferencesFromResource(R.xml.settings);
		Button buttonNewGame = (Button) findViewById(R.id.buttonNewGame);
		buttonNewGame.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent gameIntent = new Intent(SettingsActivity.this,
						MainActivity.class);
				gameIntent.putExtra("newgame", "newgame");
				SettingsActivity.this.startActivity(gameIntent);
				SettingsActivity.this.finish();
			}

		});
		Button buttonResume = (Button) findViewById(R.id.buttonResume);
		buttonResume.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent gameIntent = new Intent(SettingsActivity.this,
						MainActivity.class);
				SettingsActivity.this.startActivity(gameIntent);
				SettingsActivity.this.finish();
			}

		});
		String[] ctype = new String[]{"CET4", "CET6", "IETLS", "TOEFL", "default"};
		//创建一个数组适配器

		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ctype);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     //设置下拉列表框的下拉选项样
		Spinner mspinner = (Spinner) findViewById(R.id.spinner);
		mspinner.setAdapter(adapter);
		SharedPreferences settings = getSharedPreferences(GAME_PREFS, 0);
		final SharedPreferences.Editor editor = settings.edit();
		int position = settings.getInt("ThesaurusId", 0 );
		mspinner.setSelection(position);
		mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			private String positions;
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				positions = adapter.getItem(position);

				editor.putString("Thesaurus",positions);
				editor.putInt("ThesaurusId",position);
				editor.apply();
				Log.d("word",positions);
				Log.d("word",getSharedPreferences(GAME_PREFS, 0).getString("Thesaurus","default"));
				parent.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				parent.setVisibility(View.VISIBLE);
			}
		});
		switchButton = this.<per.stramchen.switchbuttondemo.SwitchButton>findViewById(id.switchButton);
		if(!isNightMode){
			switchButton.setChecked(false);
		} else {
			switchButton.setChecked(true);
		}
		switchButton.setOnCheckedChangeListener(
				new per.stramchen.switchbuttondemo.SwitchButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(per.stramchen.switchbuttondemo.SwitchButton view, final boolean isChecked) {
						editor.putBoolean("mode",isChecked);
						editor.putBoolean("change",true);
						editor.apply();
						Log.d("mode",isChecked?"夜间":"日间");
						Log.d("mode",getSharedPreferences(GAME_PREFS, 0).getBoolean("mode",false)?"夜间":"日间");
						if(isChecked) {
							getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
						} else {
							getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
						}
					}
				});


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent gameIntent = new Intent(SettingsActivity.this,
					MainActivity.class);
			SettingsActivity.this.startActivity(gameIntent);
			SettingsActivity.this.finish();
			return true;
		}
		return false;
	}

	private AppCompatDelegate getDelegate() {
		if (mDelegate == null) {
			mDelegate = AppCompatDelegate.create(this, null);
		}
		return mDelegate;
	}
}
