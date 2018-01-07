package unidesign.ussdsmscodes.helper;

/**
 * Created by United on 3/5/2017.
 */

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import unidesign.ussdsmscodes.MyAdapter;
import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.TempContentProvider.TempContentProvider;

/**
 * An implementation of {@link ItemTouchHelper.Callback} that enables basic drag & drop and
 * swipe-to-dismiss. Drag events are automatically started by an item long-press.<br/>
 * </br/>
 * Expects the <code>RecyclerView.Adapter</code> to react to {@link
 * ItemTouchHelperAdapter} callbacks and the <code>RecyclerView.ViewHolder</code> to implement
 * {@link ItemTouchHelperViewHolder}.
 *
 * @author Paul Burke (ipaulpro)
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdapter;
    public boolean LongPressDragEnabled = true;

    int dragFrom = -1;
    int dragTo = -1;

    static final String LOG_TAG = "myLogs";

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
//        MyAdapter adapter = (MyAdapter) mAdapter;
//        if (adapter.mode == adapter.NORMAL_MOD )
//            return true;
//        else
            return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
/*        mAdapter.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
        return true;*/
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();

        if(dragFrom == -1) {
            dragFrom =  fromPosition;
        }
        dragTo = toPosition;

        mAdapter.onItemMove(fromPosition, toPosition);

        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
            itemViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        ItemTouchHelperViewHolder itemViewHolder = (ItemTouchHelperViewHolder) viewHolder;
        itemViewHolder.onItemClear();

        if(dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
            reallyMoved(dragFrom, dragTo);
        }

        dragFrom = dragTo = -1;

    }

    private void reallyMoved(int from, int to) {
        // I guessed this was what you want...
        Uri uri;
        ContentValues values = new ContentValues();
        MyAdapter adapter = (MyAdapter) mAdapter;
        //Log.d(LOG_TAG, "--- In reallyMoved updateDB(), adapter.mSectionNumber = " +  adapter.mSectionNumber );

        switch (adapter.mSectionNumber) {
            case 1:
                for (int k  = 0 ; k < adapter.listItems.size(); k++){
                    if (adapter.listItems.get(k).getID() != adapter.templates.get(k).getId()){

                        uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                + adapter.templates.get(k).getId());

                        values.put(USSDSQLiteHelper.COLUMN_NAME, adapter.listItems.get(k).getTitle());
                        values.put(USSDSQLiteHelper.COLUMN_COMMENT, adapter.listItems.get(k).getDescription());
                        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, adapter.listItems.get(k).getTemplate());
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, adapter.listItems.get(k).getImageName());

                        adapter.mContext.getContentResolver().update(uri, values, null, null);
                        Log.d(LOG_TAG, "--- In reallyMoved update USSD, from = " + from + ", to = " + to );
                    }
                }

                break;
            case 2:
                for (int k  = 0 ; k < adapter.listItems.size(); k++){
                    if (adapter.listItems.get(k).getID() != adapter.templates.get(k).getId()){

                        uri = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                + adapter.templates.get(k).getId());

                        values.put(USSDSQLiteHelper.COLUMN_NAME, adapter.listItems.get(k).getTitle());
                        values.put(USSDSQLiteHelper.COLUMN_COMMENT, adapter.listItems.get(k).getDescription());
                        values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, adapter.listItems.get(k).getPhone());
                        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, adapter.listItems.get(k).getTemplate());
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, adapter.listItems.get(k).getImageName());

                        adapter.mContext.getContentResolver().update(uri, values, null, null);
                        Log.d(LOG_TAG, "--- In reallyMoved update SMS, from = " + from + ", to = " + to );
                    }
                }
                break;
        };

/*        for (int k  = 0 ; k < adapter.listItems.size(); k++){
            if (adapter.listItems.get(k).getID() != adapter.templates.get(k).getId()){

                uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                        + adapter.templates.get(k).getId());

                values.put(USSDSQLiteHelper.COLUMN_NAME, adapter.listItems.get(k).getTitle());
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, adapter.listItems.get(k).getDescription());
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, adapter.listItems.get(k).getTemplate());

                adapter.mContext.getContentResolver().update(uri, values, null, null);
                Log.d(LOG_TAG, "--- In reallyMoved updateDB(), from = " + from + ", to = " + to );
            }
        }*/

    }
}
