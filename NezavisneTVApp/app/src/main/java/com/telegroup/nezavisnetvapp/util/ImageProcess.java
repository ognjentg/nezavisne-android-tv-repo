package com.telegroup.nezavisnetvapp.util;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by Nemanja Đokić on 18/05/29.
 */

public class ImageProcess {


    private static final int gaussianMatrix3x3[][] = {
            {1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
    };
    private static final int gaussianMatrix3x3Denominator = 16;

    private static final int gaussianMatrix5x5[][] = {
            {1, 4, 7, 4, 1},
            {4, 16, 26, 16, 4},
            {7, 26, 41, 26, 7},
            {4, 16, 26, 16, 4},
            {1, 4, 7, 4, 1}
    };
    private static final int gaussianMatrix5x5Denominator = 273;

    public static Bitmap darken(Bitmap bitmap){
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint(Color.RED);
        //ColorFilter filter = new LightingColorFilter(0xFFFFFFFF , 0x00222222); // lighten
        ColorFilter filter = new LightingColorFilter(0xFF5F5F5F, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(bitmap, new Matrix(), p);

        return bitmap;
    }

}


class Pixel{

    public Pixel(byte red, byte green, byte blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public byte red;
    public byte green;
    public byte blue;
}