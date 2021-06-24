package dev.jacksonc.spilth.data;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;

/**
 * Provides basic access to the database.
 * <p>
 * Singleton class that must be acquired using getInstance().
 * <p>
 * Should not be used by code outside of dev.jacksonc.data package.
 * Specific data classes should be used instead.
 *
 * @author Jackson
 * @see Item
 * @see Category
 */
public class DataProvider {
    private static DataProvider instance = null;

    // Data
    private final Map<UUID, Item> items = new HashMap<>();
    private final Map<UUID, Category> categories = new HashMap<>();

    // Indexes
    private final Map<UUID, Set<UUID>> itemToCategoriesIndex = new HashMap<>();
    private final Map<UUID, Set<UUID>> categoryToItemsIndex = new HashMap<>();

    // Constructor is private as class is a singleton
    private DataProvider() {
    }

    /**
     * Returns an instance of DataProvider.
     * <p>
     * If an instance already exists, it will be returned;
     * Otherwise a new instance will be created and returned.
     */
    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
            instance.initialiseData(); // Initialise after creating instance to avoid possible recursion
        }
        return instance;
    }

    /**
     * Resets instance reference so that the next getInstance() will create a new instance.
     * <p>
     * This method is intended to ONLY be used for testing.
     */
    public static void reset() {
        DataProvider.instance = null;
    }

    private void initialiseData() {
        Context context = Spilth.getContext();

        InputStream dataStream = context.getResources().openRawResource(R.raw.data);
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(dataStream, StandardCharsets.UTF_8));

        DataParser parser = new DataParser(streamReader);
        DataParser.DataFormat data = parser.parse();

        for (UUID categoryId : data.categories.keySet()) {
            DataParser.CategoryItem category = data.categories.get(categoryId);
            if (category != null) {
                addCategory(categoryId, category);
            }
        }

        for (UUID itemId : data.items.keySet()) {
            DataParser.DataItem item = data.items.get(itemId);
            assert item != null;

            addItem(itemId, item);
        }
    }

    private void addCategory(UUID id, DataParser.CategoryItem categoryItem) {
        String categoryName = categoryItem.getName();
        String categoryDesc = categoryItem.getDescription();
        String categoryImage = categoryItem.getImage();

        Category category = new Category(id, categoryName, categoryDesc, categoryImage);
        this.categories.put(id, category);
    }

    private void addItem(UUID id, DataParser.DataItem dataItem) {
        String itemTitle = dataItem.getTitle();
        String itemDesc = dataItem.getDescription();
        BigDecimal itemPrice = dataItem.getPrice();
        List<String> itemImages = dataItem.getImages();
        List<String> itemCategories = dataItem.getCategories();

        Item item = new Item(id, itemTitle, itemDesc, itemPrice, itemImages);
        this.items.put(id, item);

        // Add category indexes
        for (String categoryId : itemCategories) {
            addItemToCategory(id, UUID.fromString(categoryId));
        }
    }

    private void addItemToCategory(UUID itemId, UUID categoryId) {
        if (!itemToCategoriesIndex.containsKey(itemId)) {
            itemToCategoriesIndex.put(itemId, new HashSet<>());
        }
        if (!categoryToItemsIndex.containsKey(categoryId)) {
            categoryToItemsIndex.put(categoryId, new HashSet<>());
        }

        Objects.requireNonNull(itemToCategoriesIndex.get(itemId)).add(categoryId);
        Objects.requireNonNull(categoryToItemsIndex.get(categoryId)).add(itemId);
    }

    /*
     * Query Operations
     */

    protected Item getItem(UUID id) {
        return items.get(id);
    }

    protected Category getCategory(UUID id) {
        return categories.get(id);
    }

    protected Collection<Item> getItems() {
        return items.values();
    }

    protected Collection<Category> getCategories() {
        return categories.values();
    }

    /**
     * Gets all Items within a category
     *
     * @param categoryId the uuid of the category
     * @return all items included in provided category
     */
    protected ArrayList<Item> getItems(UUID categoryId) {
        ArrayList<Item> categoryItems = new ArrayList<>();

        Set<UUID> itemIds = categoryToItemsIndex.get(categoryId);
        if (itemIds == null) {
            return categoryItems;
        }

        for (UUID id : itemIds) {
            categoryItems.add(Item.get(id));
        }

        return categoryItems;
    }

    /**
     * Get all categories that a particular Item is listed as.
     *
     * @param imageId the uuid of the item
     * @return all categories listed by the item.
     */
    protected Collection<Category> getCategories(UUID imageId) {
        ArrayList<Category> itemCategories = new ArrayList<>();

        Set<UUID> categoryIds = itemToCategoriesIndex.get(imageId);
        if (categoryIds == null) {
            return itemCategories;
        }

        for (UUID id : categoryIds) {
            itemCategories.add(categories.get(id));
        }

        return itemCategories;
    }
}
