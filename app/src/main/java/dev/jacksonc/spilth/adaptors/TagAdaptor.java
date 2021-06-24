package dev.jacksonc.spilth.adaptors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.jacksonc.spilth.ListActivity;
import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.data.Category;

/**
 * RecyclerView Adaptor used to show a list of category tags
 *
 * @author Jackson
 * @author Izzy
 */
public class TagAdaptor extends RecyclerView.Adapter<TagAdaptor.ViewHolder> {
    protected final List<Category> categories;
    protected final EventListener listener;

    /**
     * Initialize the dataset of the Adapter using the default event listener.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param activity the activity to initialise the event listener with.
     */
    public TagAdaptor(List<Category> dataSet, Activity activity) {
        this(dataSet, new DefaultEventListener(activity));
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param listener the EventListener to propagate events to.
     */
    public TagAdaptor(List<Category> dataSet, EventListener listener) {
        this.categories = dataSet;
        this.listener = listener;
    }

    /**
     * Creates new views (invoked by the layout manager)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_button, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Category category = categories.get(position);

        // Update Title
        viewHolder.getCategoryName().setText(category.getName());

        // Propagate Events
        viewHolder.getCategoryName().setOnClickListener(v -> listener.onClick(category));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * Interface that is used to handle events for this adaptor
     */
    public interface EventListener {
        /**
         * Called when a tag has been clicked
         *
         * @param category the category that was clicked
         */
        void onClick(Category category);
    }

    /**
     * A default implementation of EventHandler.
     * <p>
     * Can be overridden in specific cases where different behaviour is required
     */
    public static class DefaultEventListener implements EventListener {
        private final Activity activity;

        public DefaultEventListener(Activity activity) {
            this.activity = activity;
        }

        public void onClick(Category category) {
            Intent intent = new Intent(activity, ListActivity.class);
            intent.putExtra(ListActivity.CATEGORY_ACTION, category.getId().toString());
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        }
    }

    /**
     * ViewHolder for the ItemAdaptor.
     * <p>
     * Manages information about the layout and views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button categoryName;

        public ViewHolder(View view) {
            super(view);

            this.categoryName = view.findViewById(R.id.category_button);
        }

        public Button getCategoryName() {
            return categoryName;
        }

    }
}
