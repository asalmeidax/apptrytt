package com.example.brazucalite;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class SimpleImageLoader {
    private static final ExecutorService pool = Executors.newFixedThreadPool(3);
    private static final Handler main = new Handler(Looper.getMainLooper());
    private SimpleImageLoader() {}

    public static void load(String url, ImageView view) {
        view.setImageDrawable(null);
        if (url == null || url.isEmpty()) return;
        view.setTag(url);
        pool.execute(() -> {
            Bitmap bmp = null;
            HttpURLConnection c = null;
            try {
                c = (HttpURLConnection) new URL(url).openConnection();
                c.setConnectTimeout(5000);
                c.setReadTimeout(7000);
                bmp = BitmapFactory.decodeStream(c.getInputStream());
            } catch (Exception ignored) {
            } finally {
                if (c != null) c.disconnect();
            }
            Bitmap finalBmp = bmp;
            main.post(() -> {
                if (url.equals(view.getTag()) && finalBmp != null) view.setImageBitmap(finalBmp);
            });
        });
    }
}
