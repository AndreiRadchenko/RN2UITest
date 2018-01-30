package unidesign.ussdsmscodes.Preferences;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RN_USSD;

import static unidesign.ussdsmscodes.Preferences.pref_items.pref_PIN;

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
    public static Context pin_lock_context;
    public static CountThread PINCountThread = null;
    public static final long IDLE_LOCK_INTERVAL = 3*60*1000; // in millisecond
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
                    PINCountThread = new CountThread();
                    PINCountThread.start();
                    enterPINattemption = 0;
                    editor.putString(pref_PIN, pin);
                    editor.commit();
                    setResult(RESULT_OK, new Intent());
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
                        editor.putBoolean(pref_items.pref_Autorization, false);
                        editor.commit();
                        enterPINattemption = 0;

                        PINCountThread.interrupt();

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
                            editor.putString(pref_PIN, "");
                            editor.putBoolean(pref_items.pref_Autorization, false);
                            editor.commit();
                            enterPINattemption = 0;
                            try {
                                PINCountThread.interrupt();
                            } catch (NullPointerException e) {

                            }

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
                    PINCountThread = new Pin_lock_activity.CountThread();
                    PINCountThread.start();

                    //PINCountThread.interrupt();
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
                        try {
                            PINCountThread.interrupt();
                        } catch (NullPointerException e) {

                        }
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

//        RN_USSD.PINCountThread = new CountThread();

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

    public static class CountThread extends Thread {

        Message msg;
        String time_left;
        int session_min;
        Date currDate;
        long mLockTime;
        boolean newLockTimeSet;
        //ActivityManager.RunningAppProcessInfo myAppProcess;

        @Override
        public void run() {
            super.run();
            //Code
            try {
                currDate = new Date(System.currentTimeMillis());
                mLockTime = currDate.getTime() + IDLE_LOCK_INTERVAL;
                newLockTimeSet = true;

                msg = RN_USSD.h.obtainMessage(RN_USSD.TIMER_START,
                        mLockTime);
                RN_USSD.h.sendMessage(msg);

                for (;;) {
                    // if application was closed - thread
                    // finish too
                    try {
                        if (PINCountThread.interrupted()) {
                            Log.d("In new thread ", "PINCountThread, break cycle success");
                            break;
                        }
                    } catch (Exception e) {
                        Log.d("In new thread ", "PINCountThread, break cycle Exception " + e);
                    }
                    //finish tread when idle time is up
                    currDate = new Date(System.currentTimeMillis());
                    if (currDate.getTime() > mLockTime)
                        break;

                    if (!appInForeground(pin_lock_context)){
                        Log.d("In new thread ", "Time left: " +
                                (mLockTime - currDate.getTime()));
                    }
                    else {
                        mLockTime = currDate.getTime() + IDLE_LOCK_INTERVAL;
                        Log.d("In new thread ", "mLockTime set to: " +
                                mLockTime);
                        //renew LockTime in UI (RN_USSD)
                        msg = RN_USSD.h.obtainMessage(RN_USSD.TIMER_COUNT,
                                mLockTime);
                        RN_USSD.h.sendMessage(msg);
                    }

                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Interrupting and stopping the PINCountThread");
                        return;
                    }

                    //TimeUnit.SECONDS.sleep(1);
                }

                RN_USSD.h.sendEmptyMessage(RN_USSD.TIMER_STOP);
                //Looper.myLooper().quit();

            } catch (Exception e) {
                e.printStackTrace();
                RN_USSD.h.sendEmptyMessage(RN_USSD.TIMER_STOP);
                //Looper.myLooper().quit();
            }

        }

        private boolean appInForeground(@NonNull Context context) {

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses == null)
                return false;

            for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
                    if (runningAppProcess.processName.equals(context.getPackageName()) &&
                            runningAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                     return true;
                }
             return  false;
        }
    }
}
