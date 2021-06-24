package dev.jacksonc.spilth;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dev.jacksonc.spilth.adaptors.ItemAdaptor;
import dev.jacksonc.spilth.data.Category;
import dev.jacksonc.spilth.data.Item;

/**
 * Activity displaying a list of items based on a search query
 *
 * @author Jackson
 */
public class SearchActivity extends BaseActivity {

    RecyclerView rvItems;
    TextView searchHeader;
    Button priceSort;
    Button nameSort;

    String lastQuery;

    SortCategory sortCategory = SortCategory.NAME;
    boolean sortAscending = false;
    Collection<Category> filter = new ArrayList<>();

    /**
     * Initialises the SearchActivity and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Set the bottom navigation bar to display the search tab as activated
        this.setNavigation(findViewById(R.id.bottom_navigation), NavigationPage.SEARCH);

        // Create text
        searchHeader = findViewById(R.id.search_header);

        // Create RecyclerView
        rvItems = findViewById(R.id.rvItems);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvItems.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            rvItems.setLayoutManager(new GridLayoutManager(this, 4));
        }

        // Handle initial intent
        handleIntent(getIntent());

        // Create animation
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_bottom);
        rvItems.setLayoutAnimation(controller);
        Objects.requireNonNull(rvItems.getAdapter()).notifyDataSetChanged();
        rvItems.scheduleLayoutAnimation();

        // Add category filter buttons
        ChipGroup chipGroup = findViewById(R.id.search_filter_chips);
        for (Category category : Category.getAll()) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setTag(category);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> onFilterChanged());
            chipGroup.addView(chip);
        }

        // Add sorting event handlers
        priceSort = findViewById(R.id.search_sort_price);
        nameSort = findViewById(R.id.search_sort_name);

        priceSort.setOnClickListener(v -> onSort(SortCategory.PRICE));
        nameSort.setOnClickListener(v -> onSort(SortCategory.NAME));

        setSortIcons();
    }

    /**
     * Changes the current search method
     *
     * If the same category is clicked, the mode will be changed.
     * Otherwise the category will be changed.
     */
    private void onSort(SortCategory clickedCategory) {
        if (sortCategory == clickedCategory) {
            sortAscending = !sortAscending;
        } else {
            sortCategory = clickedCategory;
        }

        setSortIcons();
        newSearch(lastQuery);
    }

    /**
     * Redraw the sorting icons based on current sort settings
     */
    private void setSortIcons() {
        int defaultIcon = R.drawable.ic_baseline_sort_24;
        int sortedIcon;
        if (sortAscending) {
            sortedIcon = R.drawable.ic_baseline_expand_less_24;
        } else {
            sortedIcon = R.drawable.ic_baseline_expand_more_24;
        }

        if (sortCategory == SortCategory.NAME) {
            nameSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, sortedIcon, 0);
            priceSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, defaultIcon, 0);
        } else {
            priceSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, sortedIcon, 0);
            nameSort.setCompoundDrawablesWithIntrinsicBounds(0, 0, defaultIcon, 0);
        }

        nameSort.invalidate();
        priceSort.invalidate();
    }

    /**
     * Redo search when the filter has been changed
     */
    private void onFilterChanged() {
        ChipGroup chipGroup = findViewById(R.id.search_filter_chips);
        List<Integer> checkedChipIds = chipGroup.getCheckedChipIds();

        List<Category> filters = new ArrayList<>();
        for (int chipId : checkedChipIds) {
            Chip chip = findViewById(chipId);
            Category category = (Category) chip.getTag();
            filters.add(category);
        }

        this.filter = filters;

        newSearch(lastQuery);
    }

    /**
     * Ensure any changes are reflected once the activity resumes.
     * <p>
     * e.g. wishlist status changed
     */
    @Override
    protected void onResume() {
        super.onResume();

        Objects.requireNonNull(rvItems.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Activates initialisation of activity based on intent.
     *
     * @param intent The current intent to be activated.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    /**
     * Performs activity initialisation.
     *
     * @param intent The current intent to be interpreted.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            lastQuery = query;

            newSearch(query);
        }
    }

    /**
     * Initiates a new search
     * @param query string to search
     */
    private void newSearch(String query) {
        // Get results from query
        Collection<Item> items = search(query);
        ItemAdaptor adaptor = new ItemAdaptor(new ArrayList<>(items), this);

        // If there are results for the search, schedule animation for the recyclerView.
        if (rvItems.getAdapter() != null) {
            rvItems.swapAdapter(adaptor, false);
            rvItems.scheduleLayoutAnimation();
        } else {
            rvItems.setAdapter(adaptor);
        }

        // Set layout header text
        searchHeader.setText(getHeaderString(items.size(), query));
    }

    /**
     * Returns a String message to update the user on the success/results of their search.
     *
     * @param length The amount of results available for the user's search query.
     * @param query  The user's inputted search query.
     * @return A message for the SearchActivity header.
     */
    private String getHeaderString(int length, String query) {
        if (length == 0) {
            return String.format(getString(R.string.search_no_results_found), query);
        } else if (length == 1) {
            return String.format(Locale.ENGLISH, "%d result for \"%s\".", length, query);
        } else {
            return String.format(Locale.ENGLISH, "%d results for \"%s\".", length, query);
        }
    }

    /**
     * Searches through all items' text for matches with the user's input query.
     *
     * @param query The user's input search string.
     * @return A collection of items containing text matching the user's query.
     */
    private Collection<Item> search(String query) {
        // Save search as a suggestion
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SearchProvider.AUTHORITY, SearchProvider.MODE);
        suggestions.saveRecentQuery(query, null);

        // Create array of items matching query
        ArrayList<Item> items = new ArrayList<>();

        for (Item item : Item.getAll()) {
            // Convert query and all text to lower case so that search is case insensitive
            String itemName = item.getTitle().toLowerCase();
            String itemDescription = item.getDescription().toLowerCase();
            query = query.toLowerCase();

            // Get all categories to search through category names
            Collection<Category> categories = item.getCategories();

            // Filter based on categories
            if (!categories.containsAll(filter)) {
                continue;
            }

            // Check if each item's name or description contains user query
            if (itemName.contains(query) || itemDescription.contains(query)) {
                items.add(item);
                continue;
            }

            // Check if each category name contains user query
            for (Category category : categories) {
                String categoryName = category.getName().toLowerCase();
                if (categoryName.contains(query)) {
                    items.add(item);
                    break;
                }
            }
        }

        // Sort items
        Comparator<Item> sorter;
        if (sortCategory == SortCategory.NAME) {
            sorter = new nameComparator();
        } else {
            sorter = new priceComparator();
        }
        Collections.sort(items, sorter);
        if (sortAscending) {
            Collections.reverse(items);
        }

        return items;
    }

    enum SortCategory {
        NAME,
        PRICE
    }

    /**
     * Sorts items based on their price
     */
    private static class priceComparator implements Comparator<Item> {
        @Override
        public int compare(Item a, Item b) {
            return a.getPrice().compareTo(b.getPrice());
        }
    }

    /**
     * Sorts items based on their name
     */
    private static class nameComparator implements Comparator<Item> {
        @Override
        public int compare(Item a, Item b) {
            return a.getTitle().compareTo(b.getTitle());
        }
    }

}