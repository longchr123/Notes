package com.example.myview;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

public abstract class ClickableImageSpan extends ImageSpan {
    public ClickableImageSpan(Drawable b) {
        super(b);
    }
    public ClickableImageSpan(Drawable b,String text) {
    	super(b,text);
    }

    public abstract void onClick(View view);
}