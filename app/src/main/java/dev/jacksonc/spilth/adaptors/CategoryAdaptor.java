package dev.jacksonc.spilth.adaptors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dev.jacksonc.spilth.ListActivity;
import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;
import dev.jacksonc.spilth.data.Category;

/**
 * RecyclerView Adaptor used to show a list of categories
 *
 * @author Jackson
 * @author Izzy
 */
public class CategoryAdaptor extends RecyclerView.Adapter<CategoryAdaptor.ViewHolder> {
    protected final List<Category> categories;
    protected final EventListener listener;


    /**
     * Initialize the dataset of the Adapter using the default event listener.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param activity the activity to initialise the event listener with.
     */
    public CategoryAdaptor(List<Category> dataSet, Activity activity) {
        this(dataSet, new DefaultEventListener(activity));
    }


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param listener the EventListener to propagate events to.
     */
    public CategoryAdaptor(List<Category> dataSet, EventListener listener) {
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
                .inflate(R.layout.category_row, viewGroup, false);

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

        // Update Image
        if (category.hasImage()) {
            String imagePath = category.getImagePath();
            try {
                InputStream image = Spilth.getContext().getAssets().open(imagePath);
                Drawable d = Drawable.createFromStream(image, null);
                viewHolder.getImage().setBackground(viewHolder.getImage().getDrawable());
                viewHolder.getImage().setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Propagate Events
        viewHolder.getCategoryBackground().setOnClickListener((View v) -> listener.onClick(category));
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
         * Called when a category has been clicked
         *
         * @param category the category that has been clicked
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
            activity.startActivity(intent);
        }
    }

    /**
     * ViewHolder for the CategoryAdaptor.
     * <p>
     * Manages information about the layout and views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final CardView categoryBackground;
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);

            this.categoryName = view.findViewById(R.id.category_row_cat_name);
            this.categoryBackground = view.findViewById(R.id.CatRow_Background);
            this.image = view.findViewById(R.id.CatRow_Image);
        }

        public TextView getCategoryName() {
            return categoryName;
        }

        public CardView getCategoryBackground() {
            return categoryBackground;
        }

        public ImageView getImage() {
            return image;
        }
    }
}
