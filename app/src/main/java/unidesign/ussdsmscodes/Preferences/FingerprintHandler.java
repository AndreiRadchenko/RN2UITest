package unidesign.ussdsmscodes.Preferences;

/**
 * Created by United on 2/3/2018.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context context;
    FingerPrintDialog dialog;


    public FingerprintHandler(Context mContext, FingerPrintDialog mdialog, CancellationSignal mcancellationSignal) {
        context = mContext;
        dialog = mdialog;
        cancellationSignal = mcancellationSignal;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        //cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
//        Toast.makeText(context,
//                "Authentication error\n" + errString,
//                Toast.LENGTH_LONG).show();
        Log.d("FingerprintHandler", "Authentication error\n" + errString);
    }

    @Override
    public void onAuthenticationFailed() {
//        Toast.makeText(context,
//                "Authentication failed",
//                Toast.LENGTH_LONG).show();
        String msg = "Authentication failed\n";
        dialog.setMessage(msg);
        Log.d("FingerprintHandler", msg);

        Handler h = new Handler();
        h.postDelayed(reset_message, 3000);
        //h.removeCallbacks(reset_exit);

    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
//        Toast.makeText(context,
//                "Authentication help\n" + helpString,
//                Toast.LENGTH_LONG).show();
        String msg = helpString.toString();
        dialog.setMessage(msg);
        Log.d("FingerprintHandler", "Authentication help\n" + helpString);

        Handler h = new Handler();
        h.postDelayed(reset_message, 3000);
    }


    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

//        Toast.makeText(context,
//                "Success!",
//                Toast.LENGTH_LONG).show();
//        String msg = "Success!";
//        dialog.setMessage(msg);
        Log.d("FingerprintHandler", "Authentication success\n");
        dialog.passFingerprint();
    }

    // reset dialog "message" afte 3 sec spend
    Runnable reset_message = new Runnable() {
        public void run() {
            Log.d("post delayed handler", "run dialog.resetMessage()");
            try {
                dialog.resetMessage();
            } catch (Exception e){
                Log.d("post delayed exeption", e.toString());
            }

        }
    };

}

