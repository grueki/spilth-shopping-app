package dev.jacksonc.spilth;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import dev.jacksonc.spilth.data.Category;
import dev.jacksonc.spilth.data.DataProvider;
import dev.jacksonc.spilth.data.Item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class DataProviderUnitTest {

    @After
    public final void tearDown() {
        DataProvider.reset();
    }

    @Test
    public void testInitialiseDataProvider() {
        DataProvider data = DataProvider.getInstance();

        assertSame(DataProvider.getInstance(), data);
    }

    @Test
    public void testGetItem(){
        Item item = Item.get("2b41adab-c1f5-4cfd-a001-6ea90e9e52d8");

        assertEquals(UUID.fromString("2b41adab-c1f5-4cfd-a001-6ea90e9e52d8"), item.getId());
        assertEquals("Refined Cotton Soap", item.getTitle());
        assertEquals("If we transmit the alarm, we can get to the RAM protocol through the virtual THX matrix!", item.getDescription());
        assertEquals(new BigDecimal("704.00"), item.getPrice());
        assertEquals(Arrays.asList("http://lorempixel.com/540/480",
                "http://lorempixel.com/740/480",
                "http://lorempixel.com/640/480"), item.getImages());
    }

    @Test
    public void testGetCategory(){
        Category category = Category.get("6fe14db1-3a6f-45d8-b49c-29616534257f");

        assertEquals(UUID.fromString("6fe14db1-3a6f-45d8-b49c-29616534257f"), category.getId());
        assertEquals("Upgradable", category.getName());
    }

    @Test
    public void testGetItemCategories(){
        Item item = Item.get("2b41adab-c1f5-4cfd-a001-6ea90e9e52d8");
        List<UUID> expectedCategories = Arrays.asList(
                UUID.fromString("f31857c0-ce32-494e-aac5-fb8235dd8cc1"),
                UUID.fromString("6fe14db1-3a6f-45d8-b49c-29616534257f")
        );

        Collection<Category> categories = item.getCategories();
        List<UUID> categoryIds = categories.stream().map(Category::getId).collect(Collectors.toList());

        assertEquals(expectedCategories.size(), categories.size());
        assertTrue(categoryIds.containsAll(expectedCategories));
    }

    @Test
    public void testGetCategoryItems(){
        Category category = Category.get("689655b9-3651-4a33-8f63-03772983f5d1");
        List<UUID> expectedItems = Arrays.asList(
                UUID.fromString("bd7b544a-6d88-42ce-8576-9eeabde80c53"),
                UUID.fromString("e3d1965c-c490-4d20-a2c9-cee2ec4d21a0")
        );

        Collection<Item> items = category.getItems();
        List<UUID> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());

        assertEquals(expectedItems.size(), items.size());
        assertTrue(itemIds.containsAll(expectedItems));
    }
}
