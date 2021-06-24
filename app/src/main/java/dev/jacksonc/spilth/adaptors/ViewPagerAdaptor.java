package dev.jacksonc.spilth.adaptors;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import dev.jacksonc.spilth.R;
import dev.jacksonc.spilth.Spilth;

/**
 * Pager Adaptor used to show images contained in an Item
 *
 * @author Izzy
 */
public class ViewPagerAdaptor extends PagerAdapter {
    private final List<String> images;
    private final EventListener listener;

    /**
     * Initialize the dataset of the Adapter using the default EventListener
     *
     * @param images List containing the images to populate views to be used
     *               by ViewPager.
     */
    public ViewPagerAdaptor(List<String> images) {
        this(images, new DefaultEventListener());
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param images List containing the images to populate views to be used
     *               by ViewPager.
     * @param listener the EventListener to propagate events to.
     */
    public ViewPagerAdaptor(List<String> images, EventListener listener) {
        this.images = images;
        this.listener = listener;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position. (invoked by the layout manager)
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.details_images, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageViewDetails);

        // setting the image in the imageView
        String imagePath = images.get(position);
        try {
            InputStream image = Spilth.getContext().getAssets().open(imagePath);
            Drawable d = Drawable.createFromStream(image, null);
            imageView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Event Propagation
        imageView.setOnClickListener(v -> listener.onImageClick(imagePath, imageView));

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
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
         * @param v     the view that was clicked
         * @param image the path of the image clicked
         */
        void onImageClick(String image, View v);
    }

    /**
     * A default implementation of EventHandler.
     * <p>
     * Can be overridden in specific cases where different behaviour is required
     */
    public static class DefaultEventListener implements EventListener {

        @Override
        public void onImageClick(String image, View v) {
            Dialog builder = new Dialog(v.getContext());
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(builder.getLayoutInflater().inflate(R.layout.image_dialog, null));

            ImageView imageView = builder.findViewById(R.id.image_dialog_view);
            try {
                InputStream imageStream = Spilth.getContext().getAssets().open(image);
                Drawable d = Drawable.createFromStream(imageStream, null);
                imageView.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }

            builder.findViewById(R.id.image_dialog_layout).setOnClickListener(i -> builder.dismiss());

            builder.getWindow().setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );

            builder.show();
        }
    }
}
