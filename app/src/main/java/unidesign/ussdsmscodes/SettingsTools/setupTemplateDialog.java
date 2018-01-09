package unidesign.ussdsmscodes.SettingsTools;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import unidesign.ussdsmscodes.R;

/**
 * Created by United on 12/20/2017.
 */

public class setupTemplateDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */
    public interface mDialogListener {
        public void onSetupDialogPositiveClick(DialogFragment dialog, long id, int SecNumber);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    mDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (mDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final Long setup_id = getArguments().getLong("setup_id");
        final int mSecNumber = getArguments().getInt("setup_SecNumber");
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        final View dialogView = inflater.inflate(R.layout.dialog_backup, null);
        final EditText eComment = (EditText) dialogView.findViewById(R.id.backup_comment);

        builder.setView(dialogView);

        builder.setTitle("Request needs to be set up")
                .setMessage("Follow the instructions in the comment, and set up request")
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        mListener.onSetupDialogPositiveClick(setupTemplateDialog.this, setup_id, mSecNumber);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(setupTemplateDialog.this);
                    }
                });
        return builder.create();
    }


}

