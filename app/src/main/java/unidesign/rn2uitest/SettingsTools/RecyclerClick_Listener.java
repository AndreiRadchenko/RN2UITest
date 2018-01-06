package unidesign.rn2uitest.SettingsTools;

import android.view.View;

/**
 * Created by United on 12/27/2017.
 */

public interface RecyclerClick_Listener {
    /**
     * Interface for Recycler View Click listener
     **/

    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
