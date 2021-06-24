package dev.jacksonc.spilth.data;

import java.util.Collection;
import java.util.UUID;

/**
 * Provides querying for Category data.
 *
 * @author Jackson
 */
public class Category {
    private final UUID id;
    private final String name;
    private final String description;
    private final String imagePath;

    protected Category(UUID id, String name, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    /**
     * Get a Category using its UUID
     *
     * @param id uuid of the category
     * @return the category with the same uuid
     */
    public static Category get(UUID id) {
        return DataProvider.getInstance().getCategory(id);
    }

    /**
     * Get a Category using its UUID as a string
     *
     * @param id string representation of a uuid
     * @return the category with the same uuid
     */
    public static Category get(String id) {
        return get(UUID.fromString(id));
    }

    /**
     * Returns all categories in the database
     */
    public static Collection<Category> getAll() {
        return DataProvider.getInstance().getCategories();
    }

    /**
     * Returns all items inside this category
     *
     * @see Item
     */
    public Collection<Item> getItems() {
        return DataProvider.getInstance().getItems(this.id);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasImage(){
        return this.getImagePath() != null;
    }

    /**
     * Returns the path of the image listed by this category (or null if it has none)
     *
     * Paths are relative to asset directory.
     */
    public String getImagePath() {
        return imagePath;
    }
}
