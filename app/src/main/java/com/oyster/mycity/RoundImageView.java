package com.oyster.mycity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by dima on 29.06.14.
 */
public class RoundImageView extends ImageView {
    private static final String TAG = "RoundedImageiew";

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        try {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap cropped = getCroppedBitmap(bitmap);
            Drawable croppedDrawable = new BitmapDrawable(getResources(), cropped);
            super.setImageDrawable(croppedDrawable);
        } catch (NullPointerException e) {
            super.setImageDrawable(drawable);
        }

    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(getCroppedBitmap(bitmap));
    }


    /**
     * TODO: This needs to be refactored!
     *
     * @param bitmap
     * @return
     */
    private static Bitmap getCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int minDim = Math.min(height, width); // minimum dimension
        Rect subSet;
        if (minDim == width)
            subSet = new Rect(0, 0, minDim, minDim);
        else
            subSet = new Rect((width - minDim) / 2, 0, width - (width - minDim) / 2, height);

        Bitmap output = Bitmap.createBitmap(minDim, minDim, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        final Rect rect = new Rect(0, 0, minDim, minDim);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

//        canvas.drawCircle(output.getWidth() / 2, output.getHeight() / 2,
//                output.getWidth() / 2, paint);
        canvas.drawRect(rect,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, subSet, rect, paint);

        return output;
    }
}
