package dev.jacksonc.spilth;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provides search suggestions
 *
 * @author Jackson
 */
public class SearchProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "dev.jacksonc.SearchProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
