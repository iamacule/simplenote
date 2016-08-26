package vn.mran.simplenote.performance;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by MrAn on 26-Aug-16.
 */
public class CacheBitmap {
    private static CacheBitmap instance;
    private LruCache<String, Bitmap> mMemoryCache;

    public static CacheBitmap getInstance(){
        if(instance==null)
            instance = new CacheBitmap();
        return instance;
    }

    private CacheBitmap() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Log.d("asdasd",key);
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Log.d("asdasd load",key);
        return mMemoryCache.get(key);
    }
}
