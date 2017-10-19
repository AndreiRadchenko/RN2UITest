package unidesign.rn2uitest;

/**
 * Created by United on 2/25/2017.
 */

import android.content.ContentValues;
import android.content.Context;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.DrawableUtils;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import unidesign.rn2uitest.MySQLight.TemplatesDataSource;
import unidesign.rn2uitest.MySQLight.USSDSQLiteHelper;
import unidesign.rn2uitest.MySQLight.USSD_Template;
import unidesign.rn2uitest.TempContentProvider.TempContentProvider;
import unidesign.rn2uitest.helper.ItemTouchHelperViewHolder;
import unidesign.rn2uitest.helper.ItemTouchHelperAdapter;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static unidesign.rn2uitest.RN_USSD.PlaceholderFragment.ARG_SECTION_NUMBER;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    public interface OnItemClickListener {
        void onItemClick(RecyclerItem item, int mSecNumber);
    }

    static final String LOG_TAG = "MyAdapter";

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    public OnItemClickListener listener;
    public List<RecyclerItem> listItems = new ArrayList<>();
    public List<USSD_Template> templates;
    public Context mContext;
    public int mSectionNumber;
    TemplatesDataSource dbHelper;

    public MyAdapter(Context mContext, int sectionNumber, OnItemClickListener mListener) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.mSectionNumber = sectionNumber;
        this.mContext = mContext;
        this.listener = mListener;
    }

    public void setOnClickListener(OnItemClickListener mListener){
        this.listener = mListener;
    }

    public MyAdapter(Context mContext, int sectionNumber) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.mSectionNumber = sectionNumber;
        this.mContext = mContext;
    }

    public MyAdapter(Context mContext) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.mContext = mContext;
    }
/*    public void setlistItems(List<RecyclerItem> mlistItems){
        listItems = mlistItems;
    }*/

    public void swap(List<RecyclerItem> mlistItems) {
        listItems.clear();
        listItems.addAll(mlistItems);
        notifyDataSetChanged();
    }

    public void updateDB() {

        Uri uri;
        ContentValues values = new ContentValues();

        for (int k = 0; k < listItems.size(); k++) {
            if (listItems.get(k).getID() != templates.get(k).getId()) {

                uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                        + templates.get(k).getId());

                values.put(USSDSQLiteHelper.COLUMN_NAME, listItems.get(k).getTitle());
                values.put(USSDSQLiteHelper.COLUMN_COMMENT, listItems.get(k).getDescription());
                values.put(USSDSQLiteHelper.COLUMN_TEMPLATE, listItems.get(k).getTemplate());

                mContext.getContentResolver().update(uri, values, null, null);
                Log.d(LOG_TAG, "--- In MyAdapter updateDB() ---");
            }
        }
    }

    ;

    public void swapCursorUSSD(Cursor cursor) {

        if (cursor != null) {
            templates = getAllTemplates(cursor);

            List<RecyclerItem> mlistItems = new ArrayList<>();
            //Generate sample data
            for (int k = 0; k < templates.size(); k++) {
                mlistItems.add(new RecyclerItem(templates.get(k).getId(),
                        templates.get(k).getName(), templates.get(k).getComment(),
                        templates.get(k).getTemplate(), templates.get(k).getImage()));
            }

            listItems.clear();
            listItems.addAll(mlistItems);
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
    }

    public void swapCursorSMS(Cursor cursor) {

        if (cursor != null) {
            templates = getAllTemplates(cursor);

            List<RecyclerItem> mlistItems = new ArrayList<>();
            //Generate sample data
            for (int k = 0; k < templates.size(); k++) {
                mlistItems.add(new RecyclerItem(templates.get(k).getId(),
                        templates.get(k).getName(), templates.get(k).getComment(),
                        templates.get(k).getPhone(), templates.get(k).getTemplate(),
                        templates.get(k).getImage()));
            }

            listItems.clear();
            listItems.addAll(mlistItems);
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
    }


    public static USSD_Template cursorToTemplate(Cursor cursor) {

        USSD_Template template = new USSD_Template();

        try {
            template.setPhone(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_PHONE_NUMBER)));
        } catch (Exception e) {
            template.setPhone(null);
            Log.d(LOG_TAG, "in cursorToTemplate, template.setPhone(null) ");
        }


        template.setId(cursor.getLong(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_ID)));
        template.setName(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_NAME)));
        template.setComment(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_COMMENT)));
        template.setTemplate(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_TEMPLATE)));
        template.setImage(cursor.getString(cursor.getColumnIndex(USSDSQLiteHelper.COLUMN_IMAGE)));
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

        File file = new File(mContext.getFilesDir().getPath() + "/" + "icons", itemList.getImageName() + ".png");
        Log.d(LOG_TAG, file.getAbsolutePath());
        holder.bind(itemList, listener, mSectionNumber);

        //holder.imgIcon.setImageResource(R.mipmap.ic_kyivstar);
        Picasso.with(holder.txtTitle.getContext()).setIndicatorsEnabled(true);
        Picasso.with(holder.txtTitle.getContext())
                .load(file)
                .placeholder(android.R.drawable.ic_menu_rotate)
                .error(android.R.drawable.ic_menu_camera)
                .into(holder.imgIcon);

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

                            case R.id.mnu_item_edit:
                                //Toast.makeText(mContext, "Saved", Toast.LENGTH_LONG).show();
                                Uri uri2edit = null;
                                Intent intent = null;
                                switch (mSectionNumber) {
                                    case 1:
                                        intent = new Intent("intent.action.editussd");
                                        uri2edit = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                                + listItems.get(position).getID());
                                        intent.putExtra(TempContentProvider.CONTENT_ITEM_TYPE_USSD, uri2edit);
                                        break;
                                    case 2:
                                        intent = new Intent("intent.action.editsms");
                                        uri2edit = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                                + listItems.get(position).getID());
                                        intent.putExtra(TempContentProvider.CONTENT_ITEM_TYPE_SMS, uri2edit);
                                        break;
                                }
                                mContext.startActivity(intent);
                                break;

                            case R.id.mnu_item_delete:
                                //Delete item
                                Uri uri = null;
                                switch (mSectionNumber) {
                                    case 1:
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_USSD + "/"
                                                + listItems.get(position).getID());
                                        break;
                                    case 2:
                                        uri = Uri.parse(TempContentProvider.CONTENT_URI_SMS + "/"
                                                + listItems.get(position).getID());
                                        break;
                                }
                                mContext.getContentResolver().delete(uri, null, null);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                                //Log.d(LOG_TAG, "In MyAdapter Delete");
                                //Log.d(LOG_TAG, uri.toString());
                                //Log.d(LOG_TAG, "this.mSectionNumber = " + mSectionNumber);
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

    }

    public static class ViewHolder extends AbstractDraggableItemViewHolder implements
            ItemTouchHelperViewHolder {

        public RelativeLayout mContainer;
        public View mDragHandle;
        TextView txtTitle;
        TextView txtDescription;
        TextView txtOptionDigit;
        ImageView imgIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            //mDragHandle = itemView.findViewById(R.id.drag_handle);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            txtOptionDigit = (TextView) itemView.findViewById(R.id.txtOptionDigit);
            imgIcon = (ImageView) itemView.findViewById(R.id.my_image_view);
        }

        public void bind(final RecyclerItem item, final OnItemClickListener listener, final int mSN) {

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, mSN);
                }
            });
        }


        @Override
        public void onItemSelected() {
            // itemView.setBackgroundColor(Color.MAGENTA);
            itemView.setScaleY(1.05f);
            itemView.setScaleX(1.05f);
        }

        @Override
        public void onItemClear() {
            // itemView.setBackgroundColor(0);
            itemView.setScaleY(1f);
            itemView.setScaleX(1f);
        }

    }

}

