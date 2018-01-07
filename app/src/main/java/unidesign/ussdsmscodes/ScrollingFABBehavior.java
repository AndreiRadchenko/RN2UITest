package unidesign.ussdsmscodes;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.util.AttributeSet;
import android.view.View;

/**
 * Created by United on 2/7/2017.
 */

 class Utils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

/*    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }
*/
}

//public class ScrollingFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
public class ScrollingFABBehavior extends FloatingActionButton.Behavior {
    private int toolbarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.toolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;

            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float appBarGetY = dependency.getY();
            float ratio;
            //float ratio = ((float)dependency.getY())/(float)toolbarHeight;
            //if (Math.abs(appBarGetY)<toolbarHeight)
            ratio = (dependency.getY()/(float)(toolbarHeight/2));
            //else ratio = 10;
            //fab.setTranslatinY( - (distanceToScroll * ratio) + distanceToScroll/2 );
            fab.setTranslationY( - (distanceToScroll * ratio));
        }
        return true;
    }
}