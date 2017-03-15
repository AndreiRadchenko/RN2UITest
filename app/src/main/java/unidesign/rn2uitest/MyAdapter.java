package unidesign.rn2uitest;

/**
 * Created by United on 2/25/2017.
 */
import android.content.Context;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.DrawableUtils;
import android.support.v4.view.ViewCompat;
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

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.List;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
        implements DraggableItemAdapter<MyAdapter.ViewHolder> {

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private List<RecyclerItem> listItems;
    private Context mContext;

    public MyAdapter(List<RecyclerItem> listItems, Context mContext) {
        setHasStableIds(true); // this is required for D&D feature.
        this.listItems = listItems;
        this.mContext = mContext;
    }
// Методы интерфейса DraggableItemAdapter
//==========================================================================================================
    @Override
    public boolean onCheckCanStartDrag(MyAdapter.ViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyAdapter.ViewHolder holder, int position) {

/*        ItemDraggableRange draggableRangenew = new ItemDraggableRange(0, getItemCount() - 1);
        return draggableRangenew;*/
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

/*    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        RecyclerItem movedItem = listItems.remove(fromPosition);
        listItems.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }*/

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        RecyclerItem prev = listItems.remove(fromPosition);
        listItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }


    //==========================================================================================================
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

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
                                listItems.remove(position);
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

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

   public static class ViewHolder extends AbstractDraggableItemViewHolder {

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
    }

    static class RecyclerItem {

        private String title;
        private String description;

        public RecyclerItem(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class DrawableUtils {
        private static final int[] EMPTY_STATE = new int[] {};

        public static void clearState(Drawable drawable) {
            if (drawable != null) {
                drawable.setState(EMPTY_STATE);
            }
        }
    }

    public static class ViewUtils {
        public static boolean hitTest(View v, int x, int y) {
            final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
            final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
            final int left = v.getLeft() + tx;
            final int right = v.getRight() + tx;
            final int top = v.getTop() + ty;
            final int bottom = v.getBottom() + ty;

            return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
        }

    }
}

