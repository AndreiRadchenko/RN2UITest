package unidesign.ussdsmscodes.Preferences;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.PinLockListener;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import android.os.CancellationSignal;

import unidesign.ussdsmscodes.R;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import unidesign.ussdsmscodes.Preferences.pref_items;

/**
 * Created by United on 12/20/2017.
 */

public class FingerPrintDialog extends DialogFragment {

    public static final String FINGER_FEATURES_ENABLE = "finger_features_enable";
    public static final String FAIL_STRING = "fail_string";

    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private static FingerprintManager fingerprintManager;
    private static KeyguardManager keyguardManager;
    Context mContext;
    PinLockListener mPinLockListener;
    static Pin_lock_activity Pin_lock_activity;
    CancellationSignal cancellationSignal;
    TextView msg;

    public static FingerPrintDialog newInstance(Pin_lock_activity mPin_lock_activity){
        Pin_lock_activity = mPin_lock_activity;
        FingerPrintDialog dialogFragment = new FingerPrintDialog();

        return dialogFragment;

    }
    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_fingerprint, null);
        final TextView txtMessage = (TextView) dialogView.findViewById(R.id.fingerprint_message);
        txtMessage.setText(R.string.dialog_def_msg);
        msg = txtMessage;

        cancellationSignal = new CancellationSignal();
        mContext = getContext().getApplicationContext();
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(dialogView);

        builder.setTitle(R.string.dialog_title)
//                .setMessage("Use your fingerprint to verify your identity")
                .setIcon(R.drawable.ic_fingerprint_accent)
                // Add action buttons
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       // cancellationSignal.cancel();
                    }
                });
        //TextView msg = (TextView) alert.
        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onStart(){
        super.onStart();

        //TextView msg = (TextView) this.getDialog().findViewById(android.R.id.message);

        Bundle fbundle = checkFingerFeatures(mContext);
        if (fbundle.getBoolean(FingerPrintDialog.FINGER_FEATURES_ENABLE)){
            try {
                generateKey();
            } catch (FingerprintException e) {
                e.printStackTrace();
            }
            if (initCipher()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                FingerprintHandler helper = new FingerprintHandler(mContext, this, cancellationSignal);
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
        else {
            Toast.makeText(mContext, fbundle.getString(FingerPrintDialog.FAIL_STRING),
                    Toast.LENGTH_SHORT).show();
            dismiss();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStop() {
        Log.d("FingerPrintDialog", "onStop(), set cancellationSignal.cancel()");
        cancellationSignal.cancel();
        super.onStop();
    }

    void passFingerprint(){
        String pin = Pin_lock_activity.sharedPrefs.getString(pref_items.pref_PIN, "");
        Pin_lock_activity.mPinLockListener.onComplete(pin);
        dismiss();
    }

    void initTextMessage(TextView tv){
        msg = tv;
    }

    void setMessage(String message){
//        TextView msg = (TextView) this.getDialog().findViewById(android.R.id.message);
        msg.setText(message);
        msg.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    void resetMessage(){
//        TextView msg = (TextView) this.getDialog().findViewById(android.R.id.message);
        msg.setText(R.string.dialog_def_msg);
        msg.setTextColor(getResources().getColor(android.R.color.black));
    }

    public static Bundle checkFingerFeatures(Context mContext) {
        Bundle rBundle = new Bundle();
        rBundle.putBoolean(FINGER_FEATURES_ENABLE, true);
        rBundle.putString(FAIL_STRING, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            keyguardManager =
                    (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) mContext.getSystemService(FINGERPRINT_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {
                rBundle.putString(FAIL_STRING, mContext.getString(R.string.not_support_fingerprint));
                rBundle.putBoolean(FINGER_FEATURES_ENABLE, false);
                return rBundle;
                }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                rBundle.putString(FAIL_STRING, mContext.getString(R.string.enable_fingerprint));
                rBundle.putBoolean(FINGER_FEATURES_ENABLE, false);
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                rBundle.putString(FAIL_STRING, mContext.getString(R.string.register_fingerprint));
                rBundle.putBoolean(FINGER_FEATURES_ENABLE, false);
            }

            if (!keyguardManager.isKeyguardSecure()) {
                rBundle.putString(FAIL_STRING, mContext.getString(R.string.enable_lockscreen));
                rBundle.putBoolean(FINGER_FEATURES_ENABLE, false);
            }

        }
        else {
            rBundle.putString(FAIL_STRING, mContext.getString(R.string.incompatible_android_version));
            rBundle.putBoolean(FINGER_FEATURES_ENABLE, false);
        }

        return rBundle;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }



    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



    private class FingerprintException extends Exception {

        public FingerprintException(Exception e) {
            super(e);
        }
    }

}
