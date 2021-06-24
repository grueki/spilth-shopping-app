package dev.jacksonc.spilth.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;

/**
 * Provides access to the most viewed Items and allows view counts to be logged
 *
 * @author Jackson
 */
public class TopPicks {
    private static SharedPreferences getPref() {
        Context context = Spilth.getContext();
        return context.getSharedPreferences(
                context.getString(R.string.top_picks_pref_key), Context.MODE_PRIVATE);
    }

    private static void setCount(UUID itemId, int count){
        SharedPreferences sharedPref = getPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(itemId.toString(), count);
        editor.apply();
    }

    private static int getCount(UUID itemId){
        SharedPreferences sharedPref = getPref();
        return sharedPref.getInt(itemId.toString(), 0);
    }

    public static int getViews(Item item){
        return getCount(item.getId());
    }

    /**
     * Increases an items view count by 1
     *
     * @param item the item to increase view count
     * @return new view count
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int incrementViewCount(Item item){
        UUID id = item.getId();
        int views = getViews(item) + 1;
        setCount(id, views);
        return views;
    }

    /**
     * Get a list of Items sorted by most viewed (descending)
     *
     * @// TODO: 5/06/21 Add parameter to limit number of items returned
     *
     * @return sorted items
     */
    public static List<Item> getTopPicks(){
        List<Item> items = new ArrayList<>(Item.getAll());
        Collections.sort(items);

        return Lists.reverse(items);
    }

    /**
     * Reset all view counts
     */
    public static void reset(){
        SharedPreferences sharedPref = getPref();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
