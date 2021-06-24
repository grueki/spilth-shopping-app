package dev.jacksonc.spilth;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.jacksonc.spilth.adaptors.ItemAdaptor;
import dev.jacksonc.spilth.adaptors.TagAdaptor;
import dev.jacksonc.spilth.adaptors.ViewPagerAdaptor;
import dev.jacksonc.spilth.data.Category;
import dev.jacksonc.spilth.data.Item;
import dev.jacksonc.spilth.data.TopPicks;
import dev.jacksonc.spilth.data.Wishlist;

/**
 * Activity showing details of an item
 *
 * @author Izzy
 */
public class DetailsActivity extends BaseActivity {
    public static final String ITEM_ACTION = "dev.jacksonc.spilth.ITEM_ACTION";

    private Item item;

    private ViewPager mViewPager;
    private TextView itemDesc;
    private FloatingActionButton favouriteButton;
    private NestedScrollView scrollView;


    /**
     * Initialises the DetailsActivity and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Set the bottom navigation bar to display no activated tabs
        this.setNavigation(findViewById(R.id.bottom_navigation), null);

        // Get Desired Item from Intent
        Intent intent = getIntent();
        String itemId = intent.getStringExtra(ITEM_ACTION);
        this.item = Item.get(itemId);

        //Add View Count
        TopPicks.incrementViewCount(item);

        //Initializing the ViewPager Object
        mViewPager = findViewById(R.id.viewPagerDetails);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        mViewPager.setTransitionName(itemId);

        //Initializing the ViewPagerAdaptor
        ViewPagerAdaptor mViewPagerAdaptor = new ViewPagerAdaptor(new ArrayList<>(item.getImages()));

        //Adding the Adaptor to the ViewPager
        mViewPager.setAdapter(mViewPagerAdaptor);

        // Set text values
        TextView itemName = findViewById(R.id.detailsItemName);
        TextView itemPrice = findViewById(R.id.detailsItemPrice);
        itemDesc = findViewById(R.id.detailsItemDesc);
        itemName.setText(item.getTitle());
        itemPrice.setText(String.format("$%s", item.getPrice()));
        itemDesc.setText(item.getDescription());

        // Set recycler views
        RecyclerView itemTags = findViewById(R.id.rvTags);
        RecyclerView suggestItems = findViewById(R.id.suggestedItems);

        // Set tags to open category
        List<Category> tags = new ArrayList<>(item.getCategories());
        TagAdaptor tag_adaptor = new TagAdaptor(tags, this);
        itemTags.setAdapter(tag_adaptor);
        itemTags.setLayoutManager(new GridLayoutManager(this, 2));

        // Get first category from item
        Collection<Category> itemCategories = item.getCategories();
        Category suggestCategory = itemCategories.iterator().next();

        // Get all items from category
        List<Item> items = new ArrayList<>(suggestCategory.getItems());

        // Select first 2 from category
        List<Item> item_preview = new ArrayList<>();
        int i = 0;
        while (item_preview.size() < 2) {
            if (items.get(i).getId() != item.getId()) {
                item_preview.add(items.get(i));
            }
            i++;
        }

        // Set to show on DetailsActivity 'you might like'
        ItemAdaptor item_adaptor = new ItemAdaptor(item_preview, this);
        suggestItems.setAdapter(item_adaptor);
        suggestItems.setLayoutManager(new GridLayoutManager(this, 2));

        // Add scroll listener
        scrollView = findViewById(R.id.details_scroll_layout);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this::onScrollChanged);

        // Set FAB icon
        favouriteButton = findViewById(R.id.details_favourite_button);
        setFavouriteImage();
    }

    /**
     * Hides the Favourite FAB when the window has been scrolled down
     * <p>
     * Specifically, the button is hidden when the image at the top is no longer in view.
     */
    private void onScrollChanged() {
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);

        View target;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            target = mViewPager;
        } else {
            target = itemDesc;
        }

        if (target.getLocalVisibleRect(scrollBounds)){
            favouriteButton.show();
        }
        else {
            favouriteButton.hide();
        }
    }

    /**
     * Update the FAB image based on the item's wishlist status
     */
    private void setFavouriteImage() {
        if (Wishlist.isWishlisted(item)) {
            favouriteButton.setImageResource(R.drawable.ic_baseline_favorite_24);
        } else {
            favouriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
        }
    }

    /**
     * Update the item's wishlist status
     */
    public void onFavouriteClick(View view) {
        // Toggle Wishlist Status
        boolean wishlisted = !Wishlist.isWishlisted(item);
        Wishlist.setWishlisted(item, wishlisted);

        setFavouriteImage();
    }
}