package fyp.generalbusinessgame.Service;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by pc on 7/11/2017.
 */

public class ImageHolder {
    public static LruCache<Integer, Bitmap> mMemoryCache;

    
    public static void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(Integer key) {
        return mMemoryCache.get(key);
    }
}
