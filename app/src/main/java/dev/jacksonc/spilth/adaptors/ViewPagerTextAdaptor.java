package dev.jacksonc.spilth.adaptors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dev.jacksonc.spilth.DetailsActivity;
import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;
import dev.jacksonc.spilth.data.Item;

/**
 * Pager Adaptor used to display Items
 *
 * @author Izzy
 */
public class ViewPagerTextAdaptor extends PagerAdapter {
    private final List<Item> items;
    private final EventListener listener;

    /**
     * Initialize the dataset of the Adapter using the default EventListener
     *
     * @param items List containing the items to populate views to be used
     *               by ViewPager.
     * @param activity the activity to initialise the event listener with.
     */
    public ViewPagerTextAdaptor(List<Item> items, Activity activity) {
        this(items, new DefaultEventListener(activity));
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param items  List containing the items to populate views to be used
     *                 by ViewPager.
     * @param listener the EventListener to propagate events to.
     */
    public ViewPagerTextAdaptor(List<Item> items, EventListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return items.size();
    }

    /**
     * Create the page for the given position. (invoked by the layout manager)
     */
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        Item item = items.get(position);

        View itemView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.feature_image, container, false);

        // referencing the image view from the item.xml file
        ImageView imageView = itemView.findViewById(R.id.imageViewMain);
        TextView nameView = itemView.findViewById(R.id.featureName);
        TextView priceView = itemView.findViewById(R.id.featurePrice);

        // setting the image in the imageView
        String imagePath = item.getThumbnail();
        try {
            InputStream image = Spilth.getContext().getAssets().open(imagePath);
            Drawable d = Drawable.createFromStream(image, null);
            imageView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set text
        nameView.setText(String.format(Locale.getDefault(), "%s", item.getTitle()));
        priceView.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));

        // Event Propagation
        itemView.setOnClickListener(v -> listener.onClick(item, imageView));

        // Set shared transition
        ViewCompat.setTransitionName(imageView, item.getId().toString());

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    /**
     * Determines whether a page View is associated with a specific key object
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Remove a page for the given position
     */
    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    /**
     * Interface that is used to handle events for this adaptor
     */
    public interface EventListener {
        /**
         * Called when an item has been clicked
         *
         * @param v the view that was clicked
         * @param item the item represented by the view
         */
        void onClick(Item item, View v);
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

        public void onClick(Item item, View v) {
            Intent intent = new Intent(Spilth.getContext(), DetailsActivity.class);
            intent.putExtra(DetailsActivity.ITEM_ACTION, item.getId().toString());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, v, ViewCompat.getTransitionName(v));
            activity.startActivity(intent, options.toBundle());
        }

    }
}
