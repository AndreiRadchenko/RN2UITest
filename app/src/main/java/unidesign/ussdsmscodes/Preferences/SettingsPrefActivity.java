package unidesign.ussdsmscodes.Preferences;

/**
 * Created by United on 1/17/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import unidesign.ussdsmscodes.R;

import static unidesign.ussdsmscodes.Preferences.pref_items.pref_Autorization;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();

//    SwitchPreference authorization_switch;
//    Preference change_pin;
//    SwitchPreference fingerprint_switch;
      static SharedPreferences sharedPrefs;
      SharedPreferences.Editor editor;
      public static Context mContext;
      static Context activityContext;
      static SwitchPreference authorization_switch;
      public final static int PIN_REQUEST = 1;
      static boolean authorization_switch_old_value;

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
        authorization_switch_old_value = sharedPrefs.getBoolean(pref_items.pref_Autorization, false);
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
                            //set PIN check at start application
                            if (newValue.toString().equals("true")) {
                                authorization_switch_old_value = false;
                                Intent j = new Intent(activityContext, Pin_lock_activity.class);
                                j.putExtra("lanchMode", "newPIN");
                                getActivity().startActivityForResult(j, PIN_REQUEST);
                            }
                            //delete PIN check at start application
                            else {
                                authorization_switch_old_value = true;
                                Intent j = new Intent(activityContext, Pin_lock_activity.class);
                                j.putExtra("lanchMode", "deletePIN");
                                getActivity().startActivityForResult(j, PIN_REQUEST);
                            }
                            return true;
                            }

                    });

            Preference change_pin = (Preference) getPreferenceManager()
                    .findPreference("Change_PIN");

            change_pin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent j = new Intent(activityContext, Pin_lock_activity.class);
                    j.putExtra("lanchMode", "changePIN");
                    getActivity().startActivityForResult(j, PIN_REQUEST);
                    return false;
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

        @Override
        public void onResume(){
            super.onResume();
            authorization_switch.setChecked(sharedPrefs.getBoolean(pref_items.pref_Autorization, false));
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
        String message = "";
        Bundle extras = data.getExtras();
        if (extras != null)
            message = extras.getString("message");
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PIN_REQUEST:
//                    editor.putBoolean(pref_Autorization, true);
//                    editor.commit();
//                    authorization_switch.setChecked(true);
                    //RN_USSD.pinCheckComplete = true;
                    Toast.makeText(mContext, "PIN set", Toast.LENGTH_LONG).show();
                    //finish();
                    break;
            }
        }
        // если вернулось не ОК
        else {
            switch (message){
                //onBackPressed() pin deleted
                case "":
//                    editor.putBoolean(pref_Autorization, authorization_switch_old_value);
//                    editor.commit();
//                    authorization_switch.setChecked(authorization_switch_old_value);
                    break;
                case "pin deleted":

                    //authorization_switch.setChecked(false);
                    Toast.makeText(mContext, "PIN set canceled", Toast.LENGTH_LONG).show();
                    break;
                case "delete app data":
                    //authorization_switch.setChecked(false);
                    Toast.makeText(mContext, "Application data deleted", Toast.LENGTH_LONG).show();
                    break;
                case "enter new pin":
                    authorization_switch.setChecked(false);
                    Intent j = new Intent(activityContext, Pin_lock_activity.class);
                    j.putExtra("lanchMode", "newPIN");
                    startActivityForResult(j, PIN_REQUEST);
                    break;
            }


        }
    }

}
