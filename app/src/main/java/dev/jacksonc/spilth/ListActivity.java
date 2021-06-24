package dev.jacksonc.spilth;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dev.jacksonc.spilth.adaptors.ItemAdaptor;
import dev.jacksonc.spilth.data.Category;
import dev.jacksonc.spilth.data.Item;

/**
 * Activity displaying a list of items within a category
 *
 * @author Jackson
 */
public class ListActivity extends BaseActivity {
    public static final String CATEGORY_ACTION = "dev.jacksonc.spilth.CATEGORY_ACTION";

    private ItemAdaptor adaptor;

    /**
     * Initialises the ListActivity and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Set the bottom navigation bar to display no activated tabs
        this.setNavigation(findViewById(R.id.bottom_navigation), null);

        // Get Desired Category from Intent
        Intent intent = getIntent();
        String categoryId = intent.getStringExtra(CATEGORY_ACTION);
        Category category = Category.get(categoryId);

        // Set Layout Values
        TextView categoryName = findViewById(R.id.categoryName);
        TextView categoryDescription = findViewById(R.id.categoryDescription);
        categoryName.setText(category.getName());
        categoryDescription.setText(category.getDescription());

        // Create RecyclerView
        RecyclerView rvItems = findViewById(R.id.rvItems);

        // Get list of items from category and set RecyclerView
        List<Item> items = new ArrayList<>(category.getItems());
        adaptor =  new ItemAdaptor(items, this);
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
     *
     * e.g. wishlist status changed
     */
    @Override
    protected void onResume() {
        super.onResume();

        adaptor.notifyDataSetChanged();
    }
}