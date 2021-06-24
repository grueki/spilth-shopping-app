package dev.jacksonc.spilth.adaptors;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import dev.jacksonc.spilth.DetailsActivity;
import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;
import dev.jacksonc.spilth.data.Item;
import dev.jacksonc.spilth.data.Wishlist;

/**
 * RecyclerView Adaptor used to show a list of items
 *
 * @author Jackson
 * @author Izzy
 */
public class ItemAdaptor extends RecyclerView.Adapter<ItemAdaptor.ViewHolder> {
    private final List<Item> items;
    private final EventListener listener;

    /**
     * Initialize the dataset of the Adapter using the default event listener.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param activity the activity to initialise the event listener with.
     */
    public ItemAdaptor(List<Item> dataSet, Activity activity){
        this(dataSet, new DefaultEventListener(activity));
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet  List containing the data to populate views to be used
     *                 by RecyclerView.
     * @param listener the EventListener to propagate events to.
     */
    public ItemAdaptor(List<Item> dataSet, EventListener listener) {
        this.items = dataSet;
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
                .inflate(R.layout.item_row, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Item item = items.get(position);

        // Update Text
        viewHolder.getItemName().setText(item.getTitle());
        viewHolder.getItemPrice().setText(String.format("$%s", item.getPrice()));

        // Update Images
        viewHolder.setFavouriteButtonImage(item.isWishlisted());
        String imagePath = (String) item.getImages().toArray()[0];
        try {
            InputStream image = Spilth.getContext().getAssets().open(imagePath);
            Drawable d = Drawable.createFromStream(image, null);
            viewHolder.getItemThumbnail().setImageDrawable(d);

            viewHolder.resizeImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Propagate Events
        viewHolder.getItemFavouriteButton().setOnClickListener(v -> listener.onFavouriteClick(viewHolder, item));
        viewHolder.getItemThumbnail().setOnClickListener(v -> listener.onItemClick(viewHolder, item));

        // Set Shared Transition
        ViewCompat.setTransitionName(viewHolder.getItemThumbnail(), item.getId().toString());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Interface that is used to handle events for this adaptor
     */
    public interface EventListener {
        /**
         * Called when a favourite button has been clicked
         *
         * @param v the view that was clicked
         * @param item the item represented by the view
         */
        void onFavouriteClick(ViewHolder v, Item item);

        /**
         * Called when an item has been clicked
         *
         * @param v the view that was clicked
         * @param item the item represented by the view
         */
        void onItemClick(ViewHolder v, Item item);
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

        public void onFavouriteClick(ViewHolder v, Item item) {
            // Toggle Wishlist Status
            boolean wishlisted = !Wishlist.isWishlisted(item);
            Wishlist.setWishlisted(item, wishlisted);

            // Change image icon
            v.setFavouriteButtonImage(wishlisted);
        }

        public void onItemClick(ViewHolder v, Item item) {
            View common = v.getItemThumbnail();

            Intent intent = new Intent(Spilth.getContext(), DetailsActivity.class);
            intent.putExtra(DetailsActivity.ITEM_ACTION, item.getId().toString());

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, common, ViewCompat.getTransitionName(v.getItemThumbnail()));
            activity.startActivity(intent, options.toBundle());
        }
    }

    /**
     * ViewHolder for the ItemAdaptor.
     * <p>
     * Manages information about the layout and views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView itemPrice;
        private final ImageView itemThumbnail;
        private final ImageButton itemFavouriteButton;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            this.itemName = view.findViewById(R.id.itemName);
            this.itemPrice = view.findViewById(R.id.itemPrice);
            this.itemThumbnail = view.findViewById(R.id.itemThumbnail);
            this.itemFavouriteButton = view.findViewById(R.id.favouriteButton);
        }

        /**
         * Resizes the image so that it is square
         */
        public void resizeImage() {
            ImageView image = getItemThumbnail();
            image.post(() -> {
                ViewGroup.LayoutParams params = image.getLayoutParams();
                params.height = image.getMeasuredWidth();
                image.requestLayout();
            });
        }

        /**
         * Sets the button icon depending on wishlist status
         */
        public void setFavouriteButtonImage(boolean wishlisted) {
            if (wishlisted) {
                itemFavouriteButton.setImageResource(R.drawable.ic_baseline_favorite_24);
            } else {
                itemFavouriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
            itemFavouriteButton.invalidate();
        }

        public TextView getItemName() {
            return itemName;
        }

        public TextView getItemPrice() {
            return itemPrice;
        }

        public ImageView getItemThumbnail() {
            return itemThumbnail;
        }

        public ImageButton getItemFavouriteButton() {
            return itemFavouriteButton;
        }
    }
}
