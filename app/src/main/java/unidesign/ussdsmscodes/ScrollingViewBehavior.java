package unidesign.ussdsmscodes;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by United on 10/31/2017.
 */

public class ScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {

    public ScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

       // this.toolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        //return (dependency instanceof AppBarLayout) || (dependency instanceof Toolbar);
        //return  dependency instanceof Toolbar;
        return dependency instanceof AppBarLayout;
    }

/*    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent,
                                          View child,
                                          View dependency) {
        child.requestLayout();
        //child.setLayoutParams(new CoordinatorLayout.LayoutParams());

        return true;
    }*/
}
