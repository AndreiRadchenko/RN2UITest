package unidesign.ussdsmscodes.SettingsTools;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by United on 12/18/2017.
 */

public class RestoreDialog extends DialogFragment {

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    List<String> myFileArray = new ArrayList<>();
    String BackupName = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        myFileArray.clear();

        File sdPath = new File(Environment.getExternalStorageDirectory()
                + File.separator + BackupTask.DIR_SD);
        if (!sdPath.exists()) {
            // ñîçäàåì êàòàëîã
            myFileArray.add("Nothing to restore");
        } else {
            String files[] = sdPath.list();
            int filesLength = files.length;
            for (int i = 0; i < filesLength; i++) {
                BackupName = getBackupName(files[i]);
                if (BackupName != "")
                    myFileArray.add(BackupName);

            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, myFileArray);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
//                R.layout.restore_dialog_list_item, myFileArray);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select backup for restore")
                //builder.setMessage("Select backup for restore")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        Toast.makeText(getContext(), myFileArray.get(which) + " restored", Toast.LENGTH_LONG).show();
                    }
                })
                .setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
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