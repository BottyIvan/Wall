package com.botty.wall.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.botty.wall.R;

import java.util.Locale;

public class Util {

    public static final String SNACKBAR = "SNACKBAR";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("inlineValue")
    public static TextView setToolbarTypeface(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (textView.getText().equals(toolbar.getTitle())) {
                    Typeface typeface = ResourcesCompat
                            .getFont(toolbar.getContext(), R.font.robotomono_medium);
                    textView.setTypeface(typeface);
                    return textView;
                }
            }
        }
        return null;
    }

    public static void setDarkStatusBarIcons(final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.post(new Runnable() {
                @Override
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void run() {
                    v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            });
        }
    }

    public static void setLightStatusBarIcons(final View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.post(new Runnable() {
                @Override
                public void run() {
                    v.setSystemUiVisibility(0);
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public static boolean areStatusBarIconsDark(final View v) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && v.getSystemUiVisibility() != 0;
    }

    public static void showSnackbar(Snackbar snackbar) {
        snackbar.getView().setTag(SNACKBAR);
        TextView textView = snackbar.getView()
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTypeface(ResourcesCompat
                .getFont(textView.getContext(), R.font.robotomono_medium));
        snackbar.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void colorToolbarOverflowMenuIcon(Toolbar toolbar, int color) {
        //set Toolbar overflow icon color
        Drawable drawable = toolbar.getOverflowIcon();
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable.mutate(), color);
            toolbar.setOverflowIcon(drawable);
        }
    }
/*
    public static Drawable getErrorPlaceholder(Context context) {
        Drawable errorPlaceholder = AppCompatResources.getDrawable(context,
                R.drawable.error_placeholder);

        if (errorPlaceholder == null) {
            return null;
        }
        return tintDrawableWithAccentColor(context, errorPlaceholder);
    }
*//*
    private static Drawable tintDrawableWithAccentColor(Context context, Drawable d) {
        Settings s = Settings.getInstance(context);
        Theme theme = s.getThemeInstance(context);

        int tintColor = theme.getAccentColorLight(context);
        d = DrawableCompat.wrap(d);
        DrawableCompat.setTint(d, tintColor);
        return d;
    }*/

    //int[left, top, right, bottom]
    public static int[] getScreenSize(Activity context) {
        Rect displayRect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(displayRect);
        return new int[]{
                displayRect.left, displayRect.top,
                displayRect.right, displayRect.bottom};
    }

    public static float getAnimatorSpeed(Context context) {
        PowerManager powerManager = (PowerManager)
                context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode()) {
            // Animations are disabled in power save mode, so just show a toast instead.
            return 0.0f;
        }
        return android.provider.Settings.Global.getFloat(context.getContentResolver(),
                android.provider.Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
    }

    public static Locale getLocale(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}