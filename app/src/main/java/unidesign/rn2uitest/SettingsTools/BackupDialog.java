package unidesign.rn2uitest.SettingsTools;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import unidesign.rn2uitest.R;

/**
 * Created by United on 12/20/2017.
 */

public class BackupDialog extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
* implement this interface in order to receive event callbacks.
* Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, String name, String comment);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
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
        final String backup_name = getArguments().getString("backup_name", "template_yy-MM-dd_HH-mm");
        final String time_name = getBackupName(backup_name);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        final View dialogView = inflater.inflate(R.layout.dialog_backup, null);
        final EditText eComment = (EditText) dialogView.findViewById(R.id.backup_comment);

        builder.setView(dialogView);

        builder.setTitle("Save backup to phone storage")
                .setMessage(time_name)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        mListener.onDialogPositiveClick(BackupDialog.this, backup_name,
                                eComment.getText().toString());
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(BackupDialog.this);
                    }
                });
        return builder.create();
    }

    String getBackupName(String file_name) {
        String delims = "[_.]";
        String[] tokens = file_name.split(delims);
        if (tokens.length > 1) {

            if (tokens[0].equals("SMS")) {
                Log.d("RestoreDialog: ", "tokens[0] = " + tokens[0]);
                return "";
            }
            else {
                // Log.d("RestoreDialog: ", "tokens[0] = " + tokens[0]);
                delims = "[-]";
                String[] time = tokens[3].split(delims);
                String backupName = tokens[2] + " " + time[0] + ":" + time[1];
                return backupName;
            }
        } else
            return "";
    }

}
