package unidesign.ussdsmscodes.SettingsTools;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;

/**
 * Created by United on 12/28/2017.
 */

public class StatusbarColorAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

    Context context;
    Window window;
    int statusBarColor;
    int statusBarToColor;

    public StatusbarColorAnimator(Context context, int fromColor, int toColor){
        this.setFloatValues(0, 1);
        this.addUpdateListener(this);
        window = ((RestoreActivity) context).getWindow();
        statusBarToColor = toColor;//context.getResources().getColor(R.color.select_mod_status_bar);
        statusBarColor = fromColor;//context.getResources().getColor(R.color.colorPrimaryDark);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // Use animation position to blend colors.
        float position = animation.getAnimatedFraction();

        // Apply blended color to the status bar.
        int blended = blendColors(statusBarColor, statusBarToColor, position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(blended);
        }

    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}
