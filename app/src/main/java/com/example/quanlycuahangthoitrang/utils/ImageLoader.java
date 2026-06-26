package com.example.quanlycuahangthoitrang.utils;

import android.net.Uri;
import android.widget.ImageView;
import com.example.quanlycuahangthoitrang.R;

public class ImageLoader {
    public static void load(ImageView imageView, String path) {
        if (path == null || path.isEmpty()) {
            int resId = imageView.getContext().getResources().getIdentifier("shirt_1", "drawable", imageView.getContext().getPackageName());
            imageView.setImageResource(resId);
            return;
        }

        if (path.startsWith("content://") || path.startsWith("file://") || path.startsWith("/")) {
            imageView.setImageURI(Uri.parse(path));
        } else if (path.startsWith("assets/")) {
            try {
                java.io.InputStream is = imageView.getContext().getAssets().open(path.substring(7));
                imageView.setImageDrawable(android.graphics.drawable.Drawable.createFromStream(is, null));
            } catch (java.io.IOException e) {
                e.printStackTrace();
                int resId = imageView.getContext().getResources().getIdentifier("shirt_1", "drawable", imageView.getContext().getPackageName());
                imageView.setImageResource(resId);
            }
        } else {
            // Assume drawable name
            int resId = imageView.getContext().getResources().getIdentifier(path, "drawable", imageView.getContext().getPackageName());
            if (resId != 0) {
                imageView.setImageResource(resId);
            } else {
                resId = imageView.getContext().getResources().getIdentifier("shirt_1", "drawable", imageView.getContext().getPackageName());
                imageView.setImageResource(resId);
            }
        }
    }
}
