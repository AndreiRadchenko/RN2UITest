package unidesign.rn2uitest.SettingsTools;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import unidesign.rn2uitest.ImportActivity.ImportRecyclerItem;
import unidesign.rn2uitest.SettingsTools.RestoreSwipeAndDragHelper;
import unidesign.rn2uitest.R;

/**
 * Created by United on 12/26/2017.
 */

public class RestoreTemplateAdapter extends RecyclerView.Adapter<RestoreTemplateAdapter.ViewHolder> implements
        RestoreSwipeAndDragHelper.ActionCompletionContract {

    public interface OnItemClickListener {
        void onItemClick(RestoreRecyclerItem item);
    }

    public RestoreTemplateAdapter.OnItemClickListener listener;
    public List<RestoreRecyclerItem> listItems = new ArrayList<>();
    private ItemTouchHelper touchHelper;

    public RestoreTemplateAdapter(List<RestoreRecyclerItem> mlistItems, RestoreTemplateAdapter.OnItemClickListener mListener) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.listItems = mlistItems;
        this.listener = mListener;
    }


    @Override
    public RestoreTemplateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restore_recycle_item, parent, false);
        return new RestoreTemplateAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final RestoreTemplateAdapter.ViewHolder holder, final int position) {


        URL image_url = null;
        final RestoreRecyclerItem itemList = listItems.get(position);
        holder.bind(itemList, listener);
//        holder.m_icon_image_view.setImageResource(R.mipmap.ic_kyivstar);
        holder.txtName.setText(itemList.getName());
        holder.txtComment.setText(itemList.getComment());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {

        public RelativeLayout restore_item_container;
        TextView txtName;
        TextView txtComment;
//        TextView txtDescription;
//        TextView txtOptionDigit;
//        ImageView imgIcon;

        public ViewHolder(View itemView){
            super(itemView);
            restore_item_container = (RelativeLayout) itemView.findViewById(R.id.restore_item_container);
            //mDragHandle = itemView.findViewById(R.id.drag_handle);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtComment = (TextView) itemView.findViewById(R.id.txtComment);
        }

        public void bind(final  RestoreRecyclerItem item, final RestoreTemplateAdapter.OnItemClickListener listener) {

            restore_item_container.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        RestoreRecyclerItem targetItem = listItems.get(oldPosition);
        RestoreRecyclerItem Item = new RestoreRecyclerItem(targetItem);
        listItems.remove(oldPosition);
        listItems.add(newPosition, Item);
        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {

        this.touchHelper = touchHelper;
    }


}