package dev.jacksonc.spilth.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;

/**
 * Provides access to the wishlisted Items
 *
 * @author Jackson
 */
public class Wishlist {

    private static SharedPreferences getPref() {
        Context context = Spilth.getContext();
        return context.getSharedPreferences(
                context.getString(R.string.wishlist_pref_key), Context.MODE_PRIVATE);
    }

    /**
     * Set an items wishlist status
     *
     * @param item the item to set wishlist status on
     * @param wishlisted whether the item should be on the wishlist or not
     */
    public static void setWishlisted(Item item, boolean wishlisted) {
        SharedPreferences sharedPref = getPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(item.getId().toString(), wishlisted);
        editor.apply();
    }

    public static boolean isWishlisted(Item item){
        SharedPreferences sharedPref = getPref();
        return sharedPref.getBoolean(item.getId().toString(), false);
    }

    /**
     * Returns all items on the wishlist
     */
    public static Collection<Item> getAll() {
        SharedPreferences sharedPref = getPref();
        Set<String> allKeys = sharedPref.getAll().keySet();

        HashSet<Item> wishlistItems = new HashSet<>();
        for (String id : allKeys) {
            if (sharedPref.getBoolean(id, false)) {
                Item item = Item.get(id);
                wishlistItems.add(item);
            }
        }

        return wishlistItems;
    }

    /**
     * Reset all items wishlist status
     */
    public static void reset(){
        SharedPreferences sharedPref = getPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
