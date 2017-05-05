package com.bortolan.iquadriv2.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.bortolan.iquadriv2.R;
import com.transitionseverywhere.TransitionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    TextView titleTextView;
    @BindView(R.id.price)
    TextView priceTextView;
    @BindView(R.id.isbn)
    TextView isbnTextView;
    @BindView(R.id.left_image)
    View leftImage;
    @BindView(R.id.content)
    View main_content;
    private Interpolator interpolator = new DecelerateInterpolator(2f);
    private View view;

    public ViewHolder(View view) {
        super(view);
        this.view = view;
        ButterKnife.bind(this, view);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setPrice(int price) {
        priceTextView.setText(String.format("€%s", price));
    }

    public void setISBN(String ISBN) {
        isbnTextView.setText(ISBN);
    }

    public View getView() {
        return view;
    }

    void handleSwipeGesture(float dX) {
        float height = main_content.getHeight();
        float maxAbsXDiff = main_content.getWidth() / 2f;
        float factor = interpolator.getInterpolation(Math.min(Math.abs(dX), maxAbsXDiff) / maxAbsXDiff);
        float diffX = factor * height;

        main_content.setTranslationX(diffX);
        if (dX >= 0) {
            leftImage.setAlpha(factor);
            leftImage.setTranslationX(Math.abs(height - diffX) / -2f);
        }
    }

    public void reset() {
        TransitionManager.beginDelayedTransition((ViewGroup) view.getRootView());
        main_content.setTranslationX(0);
        leftImage.setAlpha(1f);
        leftImage.setTranslationX(0);
    }
}

