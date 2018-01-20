package unidesign.ussdsmscodes.Preferencec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import unidesign.ussdsmscodes.R;

import static unidesign.ussdsmscodes.Preferencec.preferences_item.pref_PIN;

/**
 * Created by United on 1/19/2018.
 */

public class Pin_lock_activity extends AppCompatActivity{
    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    TextView profile_hint_text;
    String lanchMode;
    String IntermediatePIN;
    int enterPINattemption;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    Context pin_lock_context;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            enterPINattemption ++;
            //first lanch for set PIN
            if (lanchMode.equals("newPIN")){
                //confirm PIN
                if (IntermediatePIN.equals(pin)){
                    editor.putString(pref_PIN, pin);
                    editor.commit();
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
                else if (enterPINattemption < 2){
                    profile_hint_text.setText("Confirm PIN");
                    IntermediatePIN = pin;
                    mPinLockView.resetPinLockView();
                }
                //confirm fail, try new pin
                else {
                    profile_hint_text.setText("Enter PIN");
                    IntermediatePIN = "";
                    enterPINattemption = 0;
                    mPinLockView.resetPinLockView();
                    Toast.makeText(pin_lock_context, "Confirm fail, try new pin", Toast.LENGTH_LONG).show();
                }

            }
            //confirm PIN
            else if (lanchMode.equals("newPIN") && !IntermediatePIN.equals("")){

            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pin_lock_layout);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.mine_shaft));

        profile_hint_text = findViewById(R.id.profile_hint_text);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        //mPinLockView.enableLayoutShuffling();

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();
        IntermediatePIN = "";
        enterPINattemption = 0;
        pin_lock_context = this;

        Bundle extras = getIntent().getExtras();
        lanchMode = extras.getString("lanchMode");

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();
        }

}
