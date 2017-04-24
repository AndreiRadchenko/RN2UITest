package unidesign.rn2uitest;

/**
 * Created by United on 2/25/2017.
 */
import android.content.ContentValues;
import android.content.Context;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.DrawableUtils;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;

import unidesign.rn2uitest.MySQLight.TemplatesDataSource;
import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.MySQLight.USSD_Template;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;
import unidesign.rn2uitest.helper.ItemTouchHelperViewHolder;
import unidesign.rn2uitest.helper.ItemTouchHelperAdapter;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;
import static java.security.AccessController.getContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
        implements ItemTouchHelperAdapter  {

    static final String LOG_TAG = "myLogs";
    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private List<RecyclerItem> listItems = new ArrayList<>();
    List<USSD_Template> templates;
    private Context mContext;
    TemplatesDataSource dbHelper;

    public MyAdapter(List<RecyclerItem> listItems, Context mContext) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.listItems = listItems;
        this.mContext = mContext;
    }

    public MyAdapter(Context mContext) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.mContext = mContext;
    }
/*    public void setlistItems(List<RecyclerItem> mlistItems){
        listItems = mlistItems;
    }*/

    public void swap(List<RecyclerItem> mlistItems){
        listItems.clear();
        listItems.addAll(mlistItems);
        notifyDataSetChanged();
    }

    public void updateDB(){

        Uri uri;
        ContentValues values = new ContentValues();

        for (int k  = 0 ; k < listItems.size(); k++){
            if (listItems.get(k).getID() != templates.get(k).getId()){

                uri = Uri.parse(TempContentProvider.CONTENT_URI + "/"
                        + templates.get(k).getId());

                values.put(USSDSQLiteHelper.COLUMN_NAME, listItems.get(k).getTitle());
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, listItems.get(k).getDescription());
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, listItems.get(k).getTemplate());

                mContext.getContentResolver().update(uri, values, null, null);
                //Log.d(LOG_TAG, "--- In MyAdapter updateDB() ---");
            }
        }
    };

    public void swapCursor(Cursor cursor){

        if (cursor != null) {
            templates = getAllTemplates(cursor);

            List<RecyclerItem> mlistItems = new ArrayList<>();
            //Generate sample data
            for (int k = 0; k < templates.size(); k++) {
                mlistItems.add(new RecyclerItem(templates.get(k).getId(),
                                    templates.get(k).getName(), templates.get(k).getComment(),
                                    templates.get(k).getTemplate()));
            }

            listItems.clear();
            listItems.addAll(mlistItems);
            notifyDataSetChanged();
        }
        else {
            notifyDataSetChanged();
        }
    }

    private USSD_Template cursorToTemplate(Cursor cursor) {
        USSD_Template template = new USSD_Template();
        template.setId(cursor.getLong(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_ID)));
        template.setName(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_NAME)));
        template.setComment(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_COMMENT)));
        template.setTemplate(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_TEMPLATE)));
        return template;
    }

    public List<USSD_Template> getAllTemplates(Cursor cursor) {
        List<USSD_Template> templates = new ArrayList<USSD_Template>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            USSD_Template template = cursorToTemplate(cursor);
            templates.add(template);
            cursor.moveToNext();
        }
        return templates;
    }

// Методы интерфейса DraggableItemAdapter
//==========================================================================================================

    //==========================================================================================================
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //dbHelper = new TemplatesDataSource(getActivity());
        //dbHelper.open();

        final RecyclerItem itemList = listItems.get(position);
        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDescription.setText(itemList.getDescription());
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display option menu

                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.mnu_item_save:
                                Toast.makeText(mContext, "Saved", Toast.LENGTH_LONG).show();
                                break;
                            case R.id.mnu_item_delete:
                                //Delete item
                                Uri uri = Uri.parse(TempContentProvider.CONTENT_URI + "/"
                                        + listItems.get(position).getID());
                                mContext.getContentResolver().delete(uri, null, null);
                                //fillData();
                                Log.d(LOG_TAG, "--- In MyAdapter() delete item ---");
                                Log.d(LOG_TAG, uri.toString());
                                //listItems.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public void onItemDismiss(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
        Log.d(LOG_TAG, "--- In MyAdapter() onItemDismiss --- position = " + position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        RecyclerItem prev = listItems.remove(fromPosition);
        //listItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        listItems.add(toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
        //Log.d(LOG_TAG, "--- In MyAdapter() onItemMove --- fromPosition = " + fromPosition + ", toPosition = " + toPosition);

// swap rows in  database ========================================================================
/*        Uri uri = Uri.parse(TempContentProvider.CONTENT_URI + "/"
                + listItems.get(toPosition).getID());

        ContentValues values = new ContentValues();
        values.put(USSDSQLiteHelper.COLUMN_NAME, listItems.get(fromPosition).getTitle());
        values.put(USSDSQLiteHelper.COLUMN_COMMENT, listItems.get(fromPosition).getDescription());
        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, listItems.get(fromPosition).getTemplate());

        mContext.getContentResolver().update(uri, values, null, null);

        uri = Uri.parse(TempContentProvider.CONTENT_URI + "/"
                + listItems.get(fromPosition).getID());

        values = new ContentValues();
        values.put(USSDSQLiteHelper.COLUMN_NAME, listItems.get(toPosition).getTitle());
        values.put(USSDSQLiteHelper.COLUMN_COMMENT, listItems.get(toPosition).getDescription());
        values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, listItems.get(toPosition).getTemplate());

        mContext.getContentResolver().update(uri, values, null, null);*/

    }

   public static class ViewHolder extends AbstractDraggableItemViewHolder implements
           ItemTouchHelperViewHolder {

       public FrameLayout mContainer;
       public View mDragHandle;
         TextView txtTitle;
         TextView txtDescription;
         TextView txtOptionDigit;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (FrameLayout) itemView.findViewById(R.id.container);
            mDragHandle = itemView.findViewById(R.id.drag_handle);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
        }

       @Override
       public void onItemSelected() {
           itemView.setBackgroundColor(Color.MAGENTA);
           itemView.setScaleY(1.05f);
           itemView.setScaleX(1.05f);
       }

       @Override
       public void onItemClear() {
           itemView.setBackgroundColor(0);
           itemView.setScaleY(1f );
           itemView.setScaleX(1f);
       }
    }

}

