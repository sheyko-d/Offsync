package com.offsync.util;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;

import com.offsync.R;

import org.xml.sax.XMLReader;

public class HtmlTagHandler implements Html.TagHandler {

    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("code")) {
            processCode(opening, output);
        }
    }

    private void processCode(boolean opening, Editable output) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(MyApplication
                .getContext(), R.color.secondary_text));
        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("", Typeface.createFromAsset
                (MyApplication.getContext().getAssets(), "fonts/Roboto-Italic.ttf"));
        int len = output.length();
        if (opening) {
            output.setSpan(colorSpan, len, len, Spannable.SPAN_MARK_MARK);
            output.setSpan(typefaceSpan, len, len, Spannable.SPAN_MARK_MARK);
        } else {
            Object obj = getLast(output, ForegroundColorSpan.class);
            int where = output.getSpanStart(obj);

            output.removeSpan(obj);

            if (where != len) {
                output.setSpan(colorSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                output.setSpan(typefaceSpan, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private Object getLast(Editable text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);

        if (objs.length == 0) {
            return null;
        } else {
            for (int i = objs.length; i > 0; i--) {
                if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
                    return objs[i - 1];
                }
            }
            return null;
        }
    }


}