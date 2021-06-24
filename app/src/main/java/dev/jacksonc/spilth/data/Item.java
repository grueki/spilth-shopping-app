package dev.jacksonc.spilth.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Provides querying for Item data.
 *
 * @author Jackson
 */
public class Item implements Comparable<Item> {
    private final UUID id;
    private final List<String> images;
    private final String title;
    private final String description;
    private final BigDecimal price;

    protected Item(UUID id, String title, String description, BigDecimal price, List<String> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.images = images;
    }

    /**
     * Get a Item using its UUID
     *
     * @param id uuid of the item
     * @return the item with the same uuid
     */
    public static Item get(UUID id) {
        return DataProvider.getInstance().getItem(id);
    }

    /**
     * Get a Item using its UUID as a string
     *
     * @param id string representation of a uuid
     * @return the item with the same uuid
     */
    public static Item get(String id) {
        return get(UUID.fromString(id));
    }

    /**
     * Returns all items in the database
     */
    public static Collection<Item> getAll() {
        return DataProvider.getInstance().getItems();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Returns image paths listed by this item
     *
     * Paths are relative to asset directory.
     */
    public Collection<String> getImages() {
        return images;
    }

    /**
     * Returns the image path which should be used as the images thumbnail
     *
     * Paths are relative to asset directory.
     */
    public String getThumbnail() {
        return images.get(0);
    }

    /**
     * Returns categories listed by this item
     *
     * @see Category
     */
    public Collection<Category> getCategories() {
        return DataProvider.getInstance().getCategories(this.getId());
    }

    /**
     * Returns how many times this item has been viewed
     */
    public int getViewCount() {
        return TopPicks.getViews(this);
    }

    /**
     * Returns whether this item has been put on the wishlist
     *
     * @see Wishlist
     */
    public boolean isWishlisted() {
        return Wishlist.isWishlisted(this);
    }

    /**
     * Compares items based on view count
     *
     * @param o item to compare
     */
    @Override
    public int compareTo(Item o) {
        int thisViews = this.getViewCount();
        int otherViews = o.getViewCount();

        return Integer.compare(thisViews, otherViews);
    }
}
