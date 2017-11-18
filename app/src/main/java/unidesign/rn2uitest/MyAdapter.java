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
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CheckBox;
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
import unidesign.rn2uitest.RN_USSD;

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
    //int a = RN_USSD.selected_items_count;
    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    public static final int NORMAL_MOD = 0;
    public static final int SELECTION_MOD = 1;
    public int mode = NORMAL_MOD;

    public OnItemClickListener listener;
    public List<RecyclerItem> listItems = new ArrayList<>();
    public List<USSD_Template> templates;
    public Context mContext;
    public int mSectionNumber;
    public CheckBox mCheckBox;
    TemplatesDataSource dbHelper;

    //public static String selected_color = String.format("#%08X", (0xFFFFFFFF & R.color.bg_item_selected_state));
    public int  selected_color;
    public int  normal_color;

    public MyAdapter(Context mContext, int sectionNumber, OnItemClickListener mListener) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.mSectionNumber = sectionNumber;
        this.mContext = mContext;
        this.listener = mListener;
        selected_color = ContextCompat.getColor(mContext, R.color.bg_item_selected_state);
        normal_color = ContextCompat.getColor(mContext, R.color.bg_item_normal_state);
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
        //holder.getAdapterPosition();
        final RecyclerItem itemList = listItems.get(position);
        //final RecyclerItem itemList = listItems.get(holder.getAdapterPosition());

        File file = new File(mContext.getFilesDir().getPath() + "/" + "icons", itemList.getImageName() + ".png");
        Log.d(LOG_TAG, file.getAbsolutePath());

// check selection due to RecycleView reuse ViewHolders
        if (itemList.isSelected()) {
            holder.vhCheckBox.setChecked(true);
            holder.mContainer.setBackgroundColor(selected_color);
        }
        else {
            holder.vhCheckBox.setChecked(false);
            holder.mContainer.setBackgroundColor(normal_color);
        };
        //RN_USSD.selected_items_count = 10;
        holder.bind(itemList, listener, mSectionNumber, mode, selected_color, normal_color);
//==========================set selection/normal mode================================================
        if (mode == SELECTION_MOD) {
            holder.vhCheckBox.setVisibility(holder.vhCheckBox.VISIBLE);
            holder.EditButton.setVisibility(holder.EditButton.INVISIBLE);
        }
        else {
            holder.vhCheckBox.setVisibility(holder.vhCheckBox.INVISIBLE);
            holder.EditButton.setVisibility(holder.EditButton.VISIBLE);
        }
//===================================================================================================
        //holder.imgIcon.setImageResource(R.mipmap.ic_kyivstar);
        //Picasso.with(holder.txtTitle.getContext()).setIndicatorsEnabled(true);
        Picasso.with(holder.txtTitle.getContext())
                .load(file)
                .placeholder(android.R.drawable.ic_menu_rotate)
                .error(android.R.drawable.ic_menu_camera)
                .into(holder.imgIcon);

        holder.txtTitle.setText(itemList.getTitle());
        holder.txtDescription.setText(itemList.getDescription());
        holder.EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        ImageView EditButton;
        ImageView imgIcon;
        CheckBox vhCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
            //mDragHandle = itemView.findViewById(R.id.drag_handle);
            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView) itemView.findViewById(R.id.txtDescription);
            EditButton = (ImageView) itemView.findViewById(R.id.EditButton);
            imgIcon = (ImageView) itemView.findViewById(R.id.my_image_view);
            vhCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
//        RN_USSD.selected_items_count = 10;
        public void bind(final RecyclerItem item, final OnItemClickListener listener,
                         final int mSN, final int mode, final int selected_color, final int normal_color) {
            //todo: selection mode bihavior
            //======================================================================================
            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode == SELECTION_MOD) {
                        item.setSelected(!item.isSelected());
                        if (item.isSelected()) {
                            vhCheckBox.setChecked(true);
                            mContainer.setBackgroundColor(selected_color);
                            //RN_USSD.selected_items_count++;
                            RN_USSD.myCount.setCount(RN_USSD.myCount.getCount() + 1);
                        }
                        else {
                            vhCheckBox.setChecked(false);
                            mContainer.setBackgroundColor(normal_color);
                            //RN_USSD.selected_items_count--;
                            RN_USSD.myCount.setCount(RN_USSD.myCount.getCount() - 1);
                        };

                    } else
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

    void setMod(int mmode) {
        this.mode = mmode;

        if (mmode == NORMAL_MOD)
            deselectAllItems();

        notifyDataSetChanged();
    }
// deselect all items when exit selection mode
    void deselectAllItems(){
        for (int k = 0; k < listItems.size(); k++) {
            listItems.get(k).setSelected(false);
        }
    }
    // deselect all items when exit selection mode
    void selectAllItems(){
        for (int k = 0; k < listItems.size(); k++) {
            listItems.get(k).setSelected(true);
        }
    }
}

