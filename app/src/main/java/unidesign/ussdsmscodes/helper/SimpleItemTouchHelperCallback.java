package unidesign.ussdsmscodes.helper;

/**
 * Created by United on 3/5/2017.
 */

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.google.android.gms.ads.AdView;

import unidesign.ussdsmscodes.MyAdapter;
import unidesign.ussdsmscodes.MySQLight.USSDSQLiteHelper;
import unidesign.ussdsmscodes.RecyclerItem;
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

    static final String LOG_TAG = "SimpleItemTouchHelper";

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
        Log.d(LOG_TAG, "in onMove, fromPosition: " + fromPosition + ", toPosition: " + toPosition);

        if(dragFrom == -1) {
            dragFrom =  fromPosition;
            dragTo = toPosition;
        }
//        if(target.getItemViewType() == MyAdapter.MENU_ITEM_VIEW_TYPE) {
//            dragTo = toPosition;
//        }
//        else {
//            toPosition = (fromPosition > toPosition) ? toPosition-- : toPosition++;
//            dragTo = toPosition;
//        }
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
        RecyclerItem mRecyclerItem;
        Long templateId;
        //Log.d(LOG_TAG, "--- In reallyMoved updateDB(), adapter.mSectionNumber = " +  adapter.mSectionNumber );

        switch (adapter.mSectionNumber) {
            case 1:
                //boolean change_occur = false;
                for (int k  = 0 ; k < adapter.listItems.size(); k++){
                    mRecyclerItem = (RecyclerItem) adapter.listItems.get(k);
                    templateId = adapter.templates.get(k).getId();

                    if (mRecyclerItem != null && templateId != null && mRecyclerItem.getID() != templateId){

                        uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                + adapter.templates.get(k).getId());

                        values.put(USSDSQLiteHelper.COLUMN_NAME, mRecyclerItem.getTitle());
                        values.put(USSDSQLiteHelper.COLUMN_COMMENT, mRecyclerItem.getDescription());
                        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, mRecyclerItem.getTemplate());
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, mRecyclerItem.getImageName());

                        adapter.mContext.getContentResolver().update(uri, values, null, null);
                        Log.d(LOG_TAG, "--- In reallyMoved update USSD, from = " + from + ", to = " + to );
                       // change_occur = true;
                    }
                }
//                if (!change_occur){
//                    uri = TempContentProvider.CONTENT_URI_USSD;
//                    adapter.mContext.getContentResolver().notifyChange(uri, null);
//                }

                break;
            case 2:
                for (int k  = 0 ; k < adapter.listItems.size(); k++){
                    mRecyclerItem = (RecyclerItem) adapter.listItems.get(k);
                    if (mRecyclerItem.getID() != adapter.templates.get(k).getId()){

                        uri = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                + adapter.templates.get(k).getId());

                        values.put(USSDSQLiteHelper.COLUMN_NAME, mRecyclerItem.getTitle());
                        values.put(USSDSQLiteHelper.COLUMN_COMMENT, mRecyclerItem.getDescription());
                        values.put(USSDSQLiteHelper.COLUMN_PHONE_NUMBER, mRecyclerItem.getPhone());
                        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, mRecyclerItem.getTemplate());
                        values.put(USSDSQLiteHelper.COLUMN_IMAGE, mRecyclerItem.getImageName());

                        adapter.mContext.getContentResolver().update(uri, values, null, null);
                        Log.d(LOG_TAG, "--- In reallyMoved update SMS, from = " + from + ", to = " + to );
                    }
                }
                break;
        };
    }
}

