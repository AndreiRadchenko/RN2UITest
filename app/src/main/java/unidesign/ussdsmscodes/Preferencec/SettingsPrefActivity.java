package unidesign.ussdsmscodes.Preferencec;

/**
 * Created by United on 1/17/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import unidesign.ussdsmscodes.R;

import static unidesign.ussdsmscodes.Preferencec.preferences_item.pref_Autorization;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();

//    SwitchPreference authorization_switch;
//    Preference change_pin;
//    SwitchPreference fingerprint_switch;
      SharedPreferences sharedPrefs;
      SharedPreferences.Editor editor;
      public static Context mContext;
      static Context activityContext;
      static SwitchPreference authorization_switch;
      public final static int PIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        activityContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            authorization_switch = (SwitchPreference) getPreferenceManager()
                    .findPreference("Authorization_switch");

            authorization_switch
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            if (newValue.toString().equals("true")) {
//                                // Toast.makeText(getApplicationContext(), "CB: " +
//                                // "true", Toast.LENGTH_SHORT).show();
//
                                Intent j = new Intent(activityContext, Pin_lock_activity.class);
                                j.putExtra("lanchMode", "newPIN");
                                getActivity().startActivityForResult(j, PIN_REQUEST);
//                            } else {
//                                // Toast.makeText(getApplicationContext(), "CB: " +
//                                // "false", Toast.LENGTH_SHORT).show();
//
//                                Intent j = new Intent(getApplicationContext(),
//                                        EnterPINcode.class);
//                                j.putExtra("lanchMode", "deletePIN");
//                                startActivityForResult(j, UNCHEK_PIN);
//                            }

                            }

                            return true;
                        }
                    });

            SwitchPreference fingerprint_switch = (SwitchPreference) getPreferenceManager()
                    .findPreference("Fingerprint_switch");

            fingerprint_switch
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
//                            if (newValue.toString().equals("true")) {
//                                // Toast.makeText(getApplicationContext(), "CB: " +
//                                // "true", Toast.LENGTH_SHORT).show();
//
//                                Intent j = new Intent(getApplicationContext(),
//                                        EnterPINcode.class);
//                                j.putExtra("lanchMode", "newPIN");
//                                startActivity(j);
//                            } else {
//                                // Toast.makeText(getApplicationContext(), "CB: " +
//                                // "false", Toast.LENGTH_SHORT).show();
//
//                                Intent j = new Intent(getApplicationContext(),
//                                        EnterPINcode.class);
//                                j.putExtra("lanchMode", "deletePIN");
//                                startActivityForResult(j, UNCHEK_PIN);
//                            }
                            return true;
                        }
                    });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // запишем в лог значения requestCode и resultCode
        //Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PIN_REQUEST:
                    editor.putBoolean(pref_Autorization, true);
                    editor.commit();
                    authorization_switch.setChecked(true);
                    Toast.makeText(mContext, "PIN set", Toast.LENGTH_LONG).show();
                    finish();
                    break;

            }
            // если вернулось не ОК
        } else {
            editor.putBoolean(pref_Autorization, false);
            editor.commit();
            authorization_switch.setChecked(false);
            Toast.makeText(mContext, "PIN not set", Toast.LENGTH_LONG).show();
        }
    }

}
