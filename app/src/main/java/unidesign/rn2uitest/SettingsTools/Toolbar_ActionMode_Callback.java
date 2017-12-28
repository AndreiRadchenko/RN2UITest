package unidesign.rn2uitest.SettingsTools;

import android.content.Context;
import android.os.Build;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.R;

/**
 * Created by United on 12/27/2017.
 */

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {
    private Context context;
    private RestoreTemplateAdapter recyclerView_adapter;
    private List<RestoreRecyclerItem> message_models;
    private boolean isListViewFragment;

    public Toolbar_ActionMode_Callback(Context context, RestoreTemplateAdapter recyclerView_adapter,
                                       List<RestoreRecyclerItem> message_models, boolean isListViewFragment) {
        this.context = context;
        this.recyclerView_adapter = recyclerView_adapter;
        this.message_models = message_models;
        this.isListViewFragment = isListViewFragment;
    }
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_restore_actoinmod, menu);         //Inflate the menu over action mode
        return true;
    }
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu according to SDK Levels

        return true;
    }
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restore_action_delete_selection:
                ((RestoreActivity) context).deleteRows();//delete selected rows
                mode.finish();//Finish action mode
                break;
            case R.id.restore_action_select_all:
                //Get selected ids on basis of current fragment action mode
                SparseBooleanArray selected;
                selected = recyclerView_adapter.getSelectedIds();

                if (selected.size() < message_models.size()) {
                    recyclerView_adapter.selectAllView(message_models.size(), true);
                    //set action mode title on item selection
                    ((RestoreActivity) context).mActionMode.setTitle(String.valueOf(recyclerView_adapter
                            .getSelectedCount()) + " selected");
                }
                else {
                    recyclerView_adapter.removeSelection();
                    ((RestoreActivity) context).mActionMode.finish();
//                    ((RestoreActivity) context).mActionMode.setTitle(String.valueOf(recyclerView_adapter
//                        .getSelectedCount()) + " selected");
                }

                break;

        }
        return false;
    }
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        recyclerView_adapter.removeSelection();         // remove selection
        ((RestoreActivity) context).setNullToActionMode();       //Set action mode null
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = ((RestoreActivity) context).getWindow();
//            window.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusbarColorAnimator anim = new StatusbarColorAnimator(context,
                    context.getResources().getColor(R.color.select_mod_status_bar),
                    context.getResources().getColor(R.color.colorPrimaryDark));
            anim.setDuration(250).start();
        }
    }
}