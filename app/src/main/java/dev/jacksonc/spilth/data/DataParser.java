package dev.jacksonc.spilth.data;

import com.google.gson.Gson;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Provides the ability to parse a JSON data file and transform it into data classes.
 *
 * @author Jackson
 */
public class DataParser {
    private final Gson parser;
    private final Reader reader;


    /**
     * Create a new DataParser using the provided Reader
     *
     * @param reader the Reader that contains the JSON data
     */
    DataParser(Reader reader) {
        this.parser = new Gson();
        this.reader = reader;
    }

    /**
     * Parses the data into DataFormat
     *
     * @return the parsed data
     */
    public DataFormat parse() {
        return this.parser.fromJson(this.reader, DataFormat.class);
    }

    protected static class CategoryItem {
        private String name;
        private String description;
        private String image;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImage() {
            return image;
        }
    }

    protected static class DataItem {
        private List<String> images;
        private String title;
        private String description;
        private BigDecimal price;
        private List<String> categories;

        public List<String> getImages() {
            return images;
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

        public List<String> getCategories() {
            return categories;
        }
    }

    protected static class DataFormat {
        HashMap<UUID, CategoryItem> categories;
        HashMap<UUID, DataItem> items;
    }

}
