package unidesign.ussdsmscodes.ImportActivity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import unidesign.ussdsmscodes.MyAdapter;
import unidesign.ussdsmscodes.R;
import unidesign.ussdsmscodes.RecyclerItem;

/**
 * Created by United on 9/13/2017.
 */

class ImportTemplateAdapter extends RecyclerView.Adapter<ImportTemplateAdapter.ViewHolder> implements
        SwipeAndDragHelper.ActionCompletionContract{

    private static String LOG_TAG = "ImportTemplateAdapter";
    // A menu item view type.
    public static final int MENU_ITEM_VIEW_TYPE = 0;

    // The banner ad view type.
    public static final int BANNER_AD_VIEW_TYPE = 1;

    public interface OnItemClickListener {
        void onItemClick(ImportRecyclerItem item);
    }

    public ImportTemplateAdapter.OnItemClickListener listener;
    public List<Object> listItems = new ArrayList<>();
    private ItemTouchHelper touchHelper;

    public ImportTemplateAdapter(List<Object> mlistItems, ImportTemplateAdapter.OnItemClickListener mListener) {
        //setHasStableIds(true); // this is required for D&D feature.
        this.listItems = mlistItems;
        this.listener = mListener;
    }


    @Override
     public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                final View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.import_recycle_item, parent, false);
                return new ViewHolder(v);
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                Log.d(LOG_TAG, "in onCreateViewHolder, BANNER_AD_VIEW_TYPE ");
                View bannerLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.banner_ad_container,
                        parent, false);
                return new AdViewHolder(bannerLayoutView);
        }

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITEM_VIEW_TYPE:
                final ImportRecyclerItem itemList;
                URL image_url = null;
                itemList = (ImportRecyclerItem) listItems.get(position);
                holder.bind(itemList, listener);
//        holder.m_icon_image_view.setImageResource(R.mipmap.ic_kyivstar);
                holder.m_txtTitle.setText(itemList.getTemplatename());
                // Download image
                try {
                    image_url = new URL(itemList.getPngdirref());
                } catch(Exception e) {}
                // URL image_url = new URL(Import_Templates_URL);
                //Picasso.with(holder.m_txtTitle.getContext()).setIndicatorsEnabled(true);
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
                break;
            case BANNER_AD_VIEW_TYPE:
                // fall through
            default:
                Log.d(LOG_TAG, "in onBindViewHolder, BANNER_AD_VIEW_TYPE ");
                AdViewHolder bannerHolder = (AdViewHolder) holder;
                AdView adView = (AdView) listItems.get(position);
                ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                // The AdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // AdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled AdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the banner ad to the ad view.
                adCardView.addView(adView);
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        return (listItems.get(position) instanceof ImportRecyclerItem) ? MENU_ITEM_VIEW_TYPE : BANNER_AD_VIEW_TYPE;
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
//        ImportRecyclerItem targetItem = listItems.get(oldPosition);
//        ImportRecyclerItem Item = new ImportRecyclerItem(targetItem);
//        listItems.remove(oldPosition);
//        listItems.add(newPosition, Item);
//        notifyItemMoved(oldPosition, newPosition);
    }

    @Override
    public void onViewSwiped(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {

        this.touchHelper = touchHelper;
    }

    /**
     * The {@link AdViewHolder} class.
     */
    public class AdViewHolder extends ViewHolder {

        AdViewHolder(View view) {
            super(view);
        }
    }

}
