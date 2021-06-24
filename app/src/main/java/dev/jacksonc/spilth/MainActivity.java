package dev.jacksonc.spilth;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import dev.jacksonc.spilth.adaptors.CategoryAdaptor;
import dev.jacksonc.spilth.adaptors.ViewPagerTextAdaptor;
import dev.jacksonc.spilth.data.Category;
import dev.jacksonc.spilth.data.Item;
import dev.jacksonc.spilth.data.TopPicks;

/**
 * Activity showing main screen
 *
 * @author Izzy
 */
public class MainActivity extends BaseActivity {

    // creating object of ViewPager
    ViewPager mViewPager;
    // Creating Object of ViewPagerAdapter
    ViewPagerTextAdaptor mViewPagerAdapter;

    /**
     * Initialises the MainActivity and creates its components.
     *
     * @param savedInstanceState Activity's previously saved state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the bottom navigation bar to display the browse tab as activated
        this.setNavigation(findViewById(R.id.bottom_navigation), NavigationPage.BROWSE);

        // Initialise the ViewPager Object
        mViewPager = findViewById(R.id.viewPagerMain);
        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        // Initialise the MainActivity's search bar
        SearchView searchView = findViewById(R.id.main_searchbar);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        // Create RecyclerView
        RecyclerView rvCategories = findViewById(R.id.MainAct_rvCategories);

        // Get list of categories to display in RecyclerView
        List<Category> categories = new ArrayList<>(Category.getAll());
        CategoryAdaptor adaptor = new CategoryAdaptor(categories,this);
        rvCategories.setAdapter(adaptor);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     *  Called every time MainActivity is entered.
     *  Gets the top 3 Top Viewed items and sets the ViewPager to display them.
     */

    @Override
    protected void onStart() {
        super.onStart();

        List<Item> topViewed = TopPicks.getTopPicks().subList(0, 3);

        //Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerTextAdaptor(topViewed, this);

        //Adding the Adapter to the ViewPager
        mViewPager.setAdapter(mViewPagerAdapter);
    }
}