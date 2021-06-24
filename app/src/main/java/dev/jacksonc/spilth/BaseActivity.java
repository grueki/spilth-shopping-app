package dev.jacksonc.spilth;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import dev.jacksonc.spilth.data.TopPicks;
import dev.jacksonc.spilth.data.Wishlist;

/**
 * Pages used in the navigation bar
 */
enum NavigationPage {
    BROWSE(R.id.navbar_browse),
    SEARCH(R.id.navbar_search),
    WISHLIST(R.id.navbar_wishlist);

    private final int itemId;

    NavigationPage(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }
}

/**
 * Base activity that is extended by all activities to provide shared functionalities
 *
 * @author Jackson
 * @author Izzy
 */
public abstract class BaseActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    /**
     * Sets up the navigation bar at the bottom of each activity.
     * Should be called in onCreate for each child activity
     *
     * @param navigation the navigation bar to setup
     * @param page       the current page
     * @// TODO: 6/06/21 Migrate navigation to fragments
     */
    protected void setNavigation(BottomNavigationView navigation, NavigationPage page) {
        // Set current page
        if (page != null) {
            navigation.setSelectedItemId(page.getItemId());
        }

        // Handle events
        navigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navbar_browse && page != NavigationPage.BROWSE) {
                Intent a = new Intent(this, MainActivity.class);
                startActivity(a, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else if (itemId == R.id.navbar_search) {
                onSearchRequested();
            } else if (itemId == R.id.navbar_wishlist && page != NavigationPage.WISHLIST) {
                Intent a = new Intent(this, WishlistActivity.class);
                startActivity(a, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

            return false;
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add menu bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_favourites) {
            resetFavourites();
            return true;
        } else if (id == R.id.reset_suggestions) {
            resetSuggestions();
            return true;
        } else if (id == R.id.reset_views) {
            resetViews();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void resetViews() {
        TopPicks.reset();

        View context = findViewById(R.id.settings_button);
        Snackbar snackbar = Snackbar.make(context, R.string.reset_views_snackbar, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Ok", v -> snackbar.dismiss()).show();
    }

    private void resetSuggestions() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchProvider.AUTHORITY, SearchProvider.MODE);
        suggestions.clearHistory();

        View context = findViewById(R.id.settings_button);
        Snackbar snackbar = Snackbar.make(context, R.string.reset_suggestions_snackbar, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Ok", v -> snackbar.dismiss()).show();
    }

    private void resetFavourites() {
        Wishlist.reset();

        View context = findViewById(R.id.settings_button);
        Snackbar snackbar = Snackbar.make(context, R.string.reset_favourites_snackbar, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Ok", v -> snackbar.dismiss()).show();
    }

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.appbar, popup.getMenu());

        popup.setOnMenuItemClickListener(this);
        popup.show();
    }
}
