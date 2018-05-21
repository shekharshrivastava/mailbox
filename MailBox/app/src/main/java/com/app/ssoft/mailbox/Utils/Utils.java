package com.app.ssoft.mailbox.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

public class Utils {
    public static int getRandomMaterialColor(Context context,String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

}
