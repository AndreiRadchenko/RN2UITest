package unidesign.rn2uitest.ImportActivity;

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

import unidesign.rn2uitest.R;

/**
 * Created by United on 9/13/2017.
 */

class ImportTemplateAdapter extends RecyclerView.Adapter<ImportTemplateAdapter.ViewHolder> implements
        SwipeAndDragHelper.ActionCompletionContract{

    public interface OnItemClickListener {
        void onItemClick(ImportRecyclerItem item);
    }

    public ImportTemplateAdapter.OnItemClickListener listener;
    public List<ImportRecyclerItem> listItems = new ArrayList<>();
    private ItemTouchHelper touchHelper;

    public ImportTemplateAdapter(List<ImportRecyclerItem> mlistItems, ImportTemplateAdapter.OnItemClickListener mListener) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.listItems = mlistItems;
        this.listener = mListener;
    }


    @Override
     public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.import_recycle_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        URL image_url = null;
        final ImportRecyclerItem itemList = listItems.get(position);
        holder.bind(itemList, listener);
//        holder.m_icon_image_view.setImageResource(R.mipmap.ic_kyivstar);
        holder.m_txtTitle.setText(itemList.getTemplatename());
        // Download image
        try {
            image_url = new URL(itemList.getPngdirref());
        } catch(Exception e) {}
       // URL image_url = new URL(Import_Templates_URL);
        Picasso.with(holder.m_txtTitle.getContext()).setIndicatorsEnabled(true);
        Picasso.with(holder.m_txtTitle.getContext())
                .load(itemList.getPngdirref())
                .placeholder(android.R.drawable.ic_menu_rotate)
                .error(android.R.drawable.ic_menu_camera)
                .into(holder.m_icon_image_view);
        holder.m_icon_image_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder {

        public RelativeLayout m_import_item_container;
        public ImageView m_icon_image_view;
        TextView m_txtTitle;
//        TextView txtDescription;
//        TextView txtOptionDigit;
//        ImageView imgIcon;

        public ViewHolder(View itemView){
            super(itemView);
            m_import_item_container = (RelativeLayout) itemView.findViewById(R.id.import_item_container);
            //mDragHandle = itemView.findViewById(R.id.drag_handle);
            m_icon_image_view = (ImageView) itemView.findViewById(R.id.icon_image_view);
            m_txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        }

        public void bind(final ImportRecyclerItem item, final ImportTemplateAdapter.OnItemClickListener listener) {

            m_import_item_container.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        ImportRecyclerItem targetItem = listItems.get(oldPosition);
        ImportRecyclerItem Item = new ImportRecyclerItem(targetItem);
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
