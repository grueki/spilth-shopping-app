package dev.jacksonc.spilth;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import dev.jacksonc.spilth.adaptors.ItemAdaptor;
import dev.jacksonc.spilth.data.Item;
import dev.jacksonc.spilth.data.Wishlist;

/**
 * Activity that displays items that are in the wishlist
 *
 * @author Izzy
 */
public class WishlistActivity extends BaseActivity {

    // Create header text
    TextView wishlistHeader;
    RecyclerView rvItems;

    /**
     * Initialises the WishlistActivity and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Set the bottom navigation bar to display the favourites tab as activated
        this.setNavigation(findViewById(R.id.bottom_navigation), NavigationPage.WISHLIST);

        // Get items in wishlist
        Collection<Item> items = Wishlist.getAll();

        // Create wishlist text header
        wishlistHeader = findViewById(R.id.emptyWishlist);

        // Displays message in layout if wishlist is empty
        if (items.size() == 0) {
            wishlistHeader.setText(R.string.emptyWishlistMessage);
            wishlistHeader.setVisibility(View.VISIBLE);
        }
        else {
            wishlistHeader.setVisibility(View.GONE);
        }
      
        // Create RecyclerView and display items in wishlist
        rvItems = findViewById(R.id.rvItems);
        ItemAdaptor adaptor = new ItemAdaptor(new ArrayList<>(items), this);
        rvItems.setAdapter(adaptor);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvItems.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            rvItems.setLayoutManager(new GridLayoutManager(this, 4));
        }

        // Set animation for RecyclerView items
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_bottom);
        rvItems.setLayoutAnimation(controller);
        Objects.requireNonNull(rvItems.getAdapter()).notifyDataSetChanged();
        rvItems.scheduleLayoutAnimation();
    }

    /**
     * Ensure any changes are reflected once the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();

        ItemAdaptor adaptor = new ItemAdaptor(new ArrayList<>(Wishlist.getAll()), this);
        rvItems.swapAdapter(adaptor, false);
    }

}