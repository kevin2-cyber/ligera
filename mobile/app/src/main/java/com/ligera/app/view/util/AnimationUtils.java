package com.ligera.app.view.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ligera.app.R;

/**
 * Utility class for animations throughout the app.
 * Provides reusable animations for activities, RecyclerView items, loading states,
 * bottom sheets, and common UI elements.
 */
public class AnimationUtils {
    
    // Default animation durations
    private static final long DEFAULT_DURATION = 300;
    private static final long RECYCLER_ITEM_DURATION = 200;
    private static final long BUTTON_ANIMATION_DURATION = 150;
    
    // Common interpolators
    private static final Interpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    private static final Interpolator ACCELERATE = new AccelerateInterpolator();
    private static final Interpolator DECELERATE = new DecelerateInterpolator();
    private static final Interpolator LINEAR = new LinearInterpolator();
    private static final Interpolator OVERSHOOT = new OvershootInterpolator();
    
    /**
     * Default constructor to prevent instantiation
     */
    private AnimationUtils() {
        throw new AssertionError("No instances");
    }
    
    // ==============================================================================================
    // Activity Transition Animations
    // ==============================================================================================
    
    /**
     * Start an activity with a fade transition
     *
     * @param activity Current activity
     * @param intent Intent for the new activity
     */
    public static void startActivityWithFade(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    /**
     * Start an activity with a slide transition
     *
     * @param activity Current activity
     * @param intent Intent for the new activity
     * @param slideDirection Direction to slide (true for slide up, false for slide right)
     */
    public static void startActivityWithSlide(Activity activity, Intent intent, boolean slideUp) {
        activity.startActivity(intent);
        if (slideUp) {
            activity.overridePendingTransition(R.anim.slide_up_in, R.anim.slide_up_out);
        } else {
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
    
    /**
     * Start an activity with shared element transitions
     *
     * @param activity Current activity
     * @param intent Intent for the new activity
     * @param sharedElements Pairs of shared elements (view, transitionName)
     */
    @SafeVarargs
    public static void startActivityWithSharedElements(Activity activity, Intent intent, 
                                                     Pair<View, String>... sharedElements) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, sharedElements);
        activity.startActivity(intent, options.toBundle());
    }
    
    /**
     * Finish an activity with a fade transition
     *
     * @param activity Activity to finish
     */
    public static void finishActivityWithFade(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    /**
     * Finish an activity with a slide transition
     *
     * @param activity Activity to finish
     * @param slideDirection Direction to slide (true for slide down, false for slide left)
     */
    public static void finishActivityWithSlide(Activity activity, boolean slideDown) {
        activity.finish();
        if (slideDown) {
            activity.overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        } else {
            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
    
    // ==============================================================================================
    // RecyclerView Item Animations
    // ==============================================================================================
    
    /**
     * Set up fade-in animation for RecyclerView items
     *
     * @param recyclerView The RecyclerView to animate
     */
    public static void setupRecyclerViewFadeAnimation(RecyclerView recyclerView) {
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                          @Nullable ItemHolderInfo preLayoutInfo,
                                          @NonNull ItemHolderInfo postLayoutInfo) {
                viewHolder.itemView.setAlpha(0f);
                return super.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
            }
            
            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                holder.itemView.animate()
                        .alpha(1f)
                        .setDuration(RECYCLER_ITEM_DURATION)
                        .setInterpolator(DECELERATE)
                        .start();
                return true;
            }
        });
    }
    
    /**
     * Set up slide-up animation for RecyclerView items
     *
     * @param recyclerView The RecyclerView to animate
     */
    public static void setupRecyclerViewSlideAnimation(RecyclerView recyclerView) {
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder,
                                          @Nullable ItemHolderInfo preLayoutInfo,
                                          @NonNull ItemHolderInfo postLayoutInfo) {
                viewHolder.itemView.setAlpha(0f);
                viewHolder.itemView.setTranslationY(viewHolder.itemView.getHeight() / 2);
                return super.animateAppearance(viewHolder, preLayoutInfo, postLayoutInfo);
            }
            
            @Override
            public boolean animateAdd(RecyclerView.ViewHolder holder) {
                holder.itemView.animate()
                        .alpha(1f)
                        .translationY(0)
                        .setDuration(RECYCLER_ITEM_DURATION)
                        .setInterpolator(DECELERATE)
                        .start();
                return true;
            }
        });
    }
    
    /**
     * Apply a staggered animation to a RecyclerView's items
     *
     * @param recyclerView The RecyclerView to animate
     * @param staggerDelay Delay between item animations in milliseconds
     */
    public static void animateRecyclerViewItems(RecyclerView recyclerView, long staggerDelay) {
        int itemCount = recyclerView.getChildCount();
        
        for (int i = 0; i < itemCount; i++) {
            View item = recyclerView.getChildAt(i);
            if (item != null) {
                item.setAlpha(0f);
                item.setTranslationY(50f);
                
                item.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(RECYCLER_ITEM_DURATION)
                        .setInterpolator(DECELERATE)
                        .setStartDelay(i * staggerDelay)
                        .start();
            }
        }
    }
    
    // ==============================================================================================
    // Loading State Animations
    // ==============================================================================================
    
    /**
     * Set up a Lottie animation view for loading state
     *
     * @param view The LottieAnimationView to configure
     * @param animationResId Resource ID of the Lottie animation
     */
    public static void setupLoadingAnimation(LottieAnimationView view, int animationResId) {
        view.setAnimation(animationResId);
        view.setRepeatCount(LottieDrawable.INFINITE);
        view.setRepeatMode(LottieDrawable.RESTART);
        view.playAnimation();
    }
    
    /**
     * Show a loading view with animation
     *
     * @param loadingView The view to show
     * @param contentView Optional content view to hide
     */
    public static void showLoading(View loadingView, @Nullable View contentView) {
        if (contentView != null) {
            contentView.animate()
                    .alpha(0f)
                    .setDuration(DEFAULT_DURATION / 2)
                    .setInterpolator(ACCELERATE)
                    .start();
        }
        
        loadingView.setAlpha(0f);
        loadingView.setVisibility(View.VISIBLE);
        loadingView.animate()
                .alpha(1f)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(DECELERATE)
                .start();
    }
    
    /**
     * Hide a loading view with animation
     *
     * @param loadingView The view to hide
     * @param contentView Optional content view to show
     */
    public static void hideLoading(View loadingView, @Nullable View contentView) {
        loadingView.animate()
                .alpha(0f)
                .setDuration(DEFAULT_DURATION / 2)
                .setInterpolator(ACCELERATE)
                .withEndAction(() -> loadingView.setVisibility(View.GONE))
                .start();
        
        if (contentView != null) {
            contentView.setAlpha(0f);
            contentView.setVisibility(View.VISIBLE);
            contentView.animate()
                    .alpha(1f)
                    .setDuration(DEFAULT_DURATION)
                    .setInterpolator(DECELERATE)
                    .start();
        }
    }
    
    /**
     * Create a simple loading animation for views without Lottie
     *
     * @param view The view to animate
     * @return The animator object
     */
    public static Animator createPulseAnimation(View view) {
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.95f, 1.05f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.95f, 1.05f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.7f, 1f);
        
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(LINEAR);
        
        return animator;
    }
    
    // ==============================================================================================
    // Bottom Sheet Dialog Animations
    // ==============================================================================================
    
    /**
     * Apply custom slide-up animation to a BottomSheetDialog
     *
     * @param dialog The BottomSheetDialog to animate
     */
    public static void setupBottomSheetAnimation(BottomSheetDialog dialog) {
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            
            // Set initial state
            bottomSheet.setAlpha(0f);
            bottomSheet.setTranslationY(200f);
            
            // Add callback to animate when state changes
            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        // Animate elevation increase when fully expanded
                        ViewCompat.animate(bottomSheet)
                                .translationZ(16f)
                                .setDuration(200)
                                .start();
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        // Reset elevation when collapsed
                        ViewCompat.animate(bottomSheet)
                                .translationZ(0f)
                                .setDuration(200)
                                .start();
                    }
                }
                
                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Animate based on slide offset
                    bottomSheet.setAlpha(Math.max(0.5f, slideOffset));
                    
                    // Progressive elevation based on slide offset
                    float elevation = slideOffset * 8f;
                    ViewCompat.setElevation(bottomSheet, elevation);
                }
            });
            
            // Animate entry
            bottomSheet.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(DEFAULT_DURATION)
                    .setInterpolator(DECELERATE)
                    .start();
        }
    }
    
    /**
     * Show a bottom sheet dialog with a custom slide-in animation
     *
     * @param dialog The bottom sheet dialog to show
     */
    public static void showBottomSheetWithAnimation(BottomSheetDialog dialog) {
        dialog.setOnShowListener(dialogInterface -> {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                // Apply custom animation
                bottomSheet.setTranslationY(bottomSheet.getHeight());
                bottomSheet.animate()
                        .translationY(0)
                        .setDuration(DEFAULT_DURATION)
                        .setInterpolator(DECELERATE)
                        .start();
                
                // Create a dim background effect
                View parent = (View) bottomSheet.getParent();
                parent.setBackgroundColor(0x00000000);
                parent.animate()
                        .setDuration(DEFAULT_DURATION)
                        .setInterpolator(DECELERATE)
                        .start();
            }
        });
        
        dialog.show();
        dialog.getWindow().setWindowAnimations(R.anim.bottom_sheet_slide_in);
    }
    
    /**
     * Dismiss a bottom sheet dialog with a custom slide-out animation
     *
     * @param dialog The bottom sheet dialog to dismiss
     */
    public static void dismissBottomSheetWithAnimation(BottomSheetDialog dialog) {
        View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.animate()
                    .translationY(bottomSheet.getHeight())
                    .setDuration(DEFAULT_DURATION)
                    .setInterpolator(ACCELERATE)
                    .withEndAction(dialog::dismiss)
                    .start();
            
            // Fade out the dim background
            View parent = (View) bottomSheet.getParent();
            parent.animate()
                    .alpha(0f)
                    .setDuration(DEFAULT_DURATION)
                    .setInterpolator(ACCELERATE)
                    .start();
        } else {
            dialog.dismiss();
        }
    }
    
    // ==============================================================================================
    // Common UI Element Animations
    // ==============================================================================================
    
    /**
     * Apply a button click animation
     *
     * @param button The button to animate
     */
    public static void animateButtonClick(View button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(BUTTON_ANIMATION_DURATION)
                .setInterpolator(DECELERATE)
                .withEndAction(() -> 
                    button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(OVERSHOOT)
                            .setDuration(BUTTON_ANIMATION_DURATION)
                            .start()
                )
                .start();
    }
    
    /**
     * Apply a shake animation for error states
     *
     * @param view The view to shake
     */
    public static void shakeView(View view) {
        view.clearAnimation();
        view.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
                view.getContext(), R.anim.error_shake));
    }
    
    /**
     * Apply a highlight animation to draw attention to a view
     *
     * @param view The view to highlight
     * @param highlightColor The color to highlight with (use transparent color to end highlight)
     */
    public static void highlightView(View view, int highlightColor) {
        ValueAnimator colorAnim = ObjectAnimator.ofArgb(
                view.getBackground().mutate(), "tint", 
                android.graphics.Color.TRANSPARENT, 
                highlightColor);
        colorAnim.setDuration(500);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.setRepeatCount(1);
        colorAnim.start();
    }
    
    /**
     * Cross-fade between two views
     *
     * @param viewToShow The view to fade in
     * @param viewToHide The view to fade out
     */
    public static void crossFade(View viewToShow, View viewToHide) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        viewToShow.setAlpha(0f);
        viewToShow.setVisibility(View.VISIBLE);
        
        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        viewToShow.animate()
                .alpha(1f)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(DECELERATE)
                .setListener(null);
        
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        viewToHide.animate()
                .alpha(0f)
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(ACCELERATE)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToHide.setVisibility(View.GONE);
                    }
                });
    }
    
    /**
     * Apply a reveal animation using circular reveal (on API 21+)
     *
     * @param view The view to reveal
     * @param centerX X coordinate of reveal center
     * @param centerY Y coordinate of reveal center
     */
    public static void circularReveal(View view, int centerX, int centerY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Create the animator for this view (the start radius is zero)
            Animator circularReveal = android.view.ViewAnimationUtils.createCircularReveal(
                    view,
                    centerX,
                    centerY,
                    0f,
                    (float) Math.hypot(view.getWidth(), view.getHeight()));
            
            // Make the view visible and start the animation
            view.setVisibility(View.VISIBLE);
            circularReveal.setDuration(DEFAULT_DURATION);
            circularReveal.setInterpolator(DECELERATE);
            circularReveal.start();
        } else {
            // Fall back to a fade in animation if API is too low
            view.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.animate()
                    .alpha(1f)
                    .setDuration(DEFAULT_DURATION)
                    .setInterpolator(DECELERATE)
                    .start();
        }
    }
    
    /**
     * Create a shimmer effect for loading states (for views where Lottie isn't suitable)
     * 
     * @param view The view to apply the shimmer effect to
     * @return The animator object
     */
    public static Animator createShimmerEffect(View view) {
        // Create a gradient animation
        ValueAnimator shimmerAnimator = ValueAnimator.ofFloat(0f, 2f);
        shimmerAnimator.setDuration(1500);
        shimmerAnimator.setRepeatMode(ValueAnimator.RESTART);
        shimmerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        shimmerAnimator.setInterpolator(LINEAR);
        
        // Update the view's background on animation update
        shimmerAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            // Using translation to fake a shimmer effect - in a real implementation,
            // you'd use custom drawables or ShimmerFrameLayout from Facebook's library
            view.setTranslationX((value - 1) * view.getWidth());
        });
        
        return shimmerAnimator;
    }
}
