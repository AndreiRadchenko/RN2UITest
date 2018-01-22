package unidesign.ussdsmscodes.Preferencec;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RN_USSD;

import static unidesign.ussdsmscodes.Preferencec.pref_items.pref_PIN;

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
    public static Thread PINCountThread;
//    public static Thread PINCountThread;

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            enterPINattemption ++;
            //first lanch for set PIN
            if (lanchMode.equals("newPIN")){
                //enter new PIN
                if (IntermediatePIN.equals(pin)){
                    editor.putString(pref_PIN, pin);
                    editor.commit();
//                    RN_USSD.pinCheckComplete = true;
                    setResult(RESULT_OK, new Intent());
                    enterPINattemption = 0;
                    finish();
                }
                //confirm PIN
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
            //delete PIN
            else if (lanchMode.equals("deletePIN")){
                    if (sharedPrefs.getString(pref_PIN, null).equals(pin)){
                        editor.putString(pref_PIN, "");
                        editor.commit();
                        enterPINattemption = 0;
                        Intent i = new Intent();
                        i.putExtra("message", "pin deleted");
                        setResult(RESULT_CANCELED, i);
                        finish();
                    }
                    else {
                        if (enterPINattemption < 3) {
                            //enterPINattemption ++;
                            mPinLockView.resetPinLockView();
                            Toast.makeText(pin_lock_context, "Confirm fail, try again", Toast.LENGTH_LONG).show();
                        }
                        else if (enterPINattemption == 3){
                            //enterPINattemption ++;
                            mPinLockView.resetPinLockView();
                            Toast.makeText(pin_lock_context, "If you enter incorrect PIN again, " +
                                    "application data will be lost", Toast.LENGTH_LONG).show();
                        }
                        else {
                            //delete app data
                            enterPINattemption =0;
                            Intent i = new Intent();
                            i.putExtra("message", "delete app data");
                            setResult(RESULT_CANCELED, i);
                            finish();
                        }
                    }
            }
            //lanch app check in
            else if (lanchMode.equals("checkin")){
                if (sharedPrefs.getString(pref_PIN, null).equals(pin)){
                    enterPINattemption = 0;
                    PINCountThread.start();

                    Intent i = new Intent();
                    i.putExtra("message", "pin ok");
                    setResult(RESULT_OK, i);
                    finish();
                }
                else {
                    if (enterPINattemption < 3) {
                        //enterPINattemption ++;
                        mPinLockView.resetPinLockView();
                        Toast.makeText(pin_lock_context, "Confirm fail, try again", Toast.LENGTH_LONG).show();
                    }
                    else if (enterPINattemption == 3){
                        //enterPINattemption ++;
                        mPinLockView.resetPinLockView();
                        Toast.makeText(pin_lock_context, "If you enter incorrect PIN again, " +
                                "application data will be lost", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //delete app data
                        enterPINattemption =0;
                        Intent i = new Intent();
                        i.putExtra("message", "delete app data");
                        setResult(RESULT_CANCELED, i);
                        finish();
                    }
                }
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
        pin_lock_context = this;

        Bundle extras = getIntent().getExtras();
        lanchMode = extras.getString("lanchMode");

        PINCountThread = new Thread( new Runnable() {

            Message msg;
            String time_left;
            int session_min;

            public void run() {
                //Code
                try {

                    session_min = Integer.parseInt(sharedPrefs
                            .getString("session_time", "5"));
                    int session_sec = session_min * 60;

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "mm:ss");
                    time_left = "SMS  "
                            + sdf.format(session_sec * 1000);
                    // ������� ���������
                    msg = RN_USSD.h.obtainMessage(RN_USSD.TIMER_START,
                            time_left);
                    // ����������
                    RN_USSD.h.sendMessage(msg);
                    TimeUnit.SECONDS.sleep(1);

                    for (int i = 0; i < session_sec; i++) {
                        // if application was closed - thread
                        // finish too
                        if (PINCountThread.isInterrupted())
                            break;
                        // return;

                        time_left = "SMS  "
                                + sdf.format((session_sec - i) * 1000);
                        // Log.d("In new thread ", time_left);
                        // ������� ���������
                        msg = RN_USSD.h.obtainMessage(RN_USSD.TIMER_COUNT,
                                time_left);
                        // ����������
                        RN_USSD.h.sendMessage(msg);
                        TimeUnit.SECONDS.sleep(1);
                    }

                    RN_USSD.h.sendEmptyMessage(RN_USSD.TIMER_STOP);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    RN_USSD.h.sendEmptyMessage(RN_USSD.TIMER_STOP);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        super.onBackPressed();
        }

    @Override
    public void onPause() {
        super.onPause();
        editor.putInt(pref_items.pref_enterPINattemption, enterPINattemption);
        editor.commit();
        Log.d("PIN_Lock_Activity", "onPause(), enterPINattemption = " + enterPINattemption);
    }

    @Override
    public void onResume() {
        super.onResume();
        enterPINattemption = sharedPrefs.getInt(pref_items.pref_enterPINattemption, 0);
        Log.d("PIN_Lock_Activity", "onResume(), enterPINattemption = " + enterPINattemption);
    }

    @Override
    public void onDestroy() {
        //PINCountThread.interrupt();
        super.onDestroy();
    }
}
