package com.ligera.app.view.util;

import android.animation.TimeInterpolator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Enhanced page transformer for ViewPager2 that applies depth-based transitions with
 * smooth interpolation and optimized performance.
 */
public class DepthPageTransformer implements ViewPager2.PageTransformer {
    
    // Animation style constants
    public static final int STYLE_DEPTH = 0;
    public static final int STYLE_ZOOM = 1;
    public static final int STYLE_FADE = 2;
    public static final int STYLE_CUBE = 3;
    
    @IntDef({STYLE_DEPTH, STYLE_ZOOM, STYLE_FADE, STYLE_CUBE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationStyle {}
    
    // Scale constants
    private static final float DEFAULT_MIN_SCALE = 0.85f;
    private static final float DEFAULT_MIN_ALPHA = 0.5f;
    
    // Configurable properties
    private float minScale;
    private float minAlpha;
    private TimeInterpolator interpolator;
    private int animationStyle;
    private boolean isHardwareAccelerationEnabled;
    
    /**
     * Creates a default DepthPageTransformer with STYLE_DEPTH animation.
     */
    public DepthPageTransformer() {
        this(STYLE_DEPTH);
    }
    
    /**
     * Creates a DepthPageTransformer with the specified animation style.
     *
     * @param animationStyle the animation style to use
     */
    public DepthPageTransformer(@AnimationStyle int animationStyle) {
        this(animationStyle, DEFAULT_MIN_SCALE, DEFAULT_MIN_ALPHA);
    }
    
    /**
     * Creates a DepthPageTransformer with custom parameters.
     *
     * @param animationStyle the animation style to use
     * @param minScale the minimum scale factor (0.0-1.0)
     * @param minAlpha the minimum alpha value (0.0-1.0)
     */
    public DepthPageTransformer(@AnimationStyle int animationStyle, 
                                @FloatRange(from = 0.0, to = 1.0) float minScale,
                                @FloatRange(from = 0.0, to = 1.0) float minAlpha) {
        this.animationStyle = animationStyle;
        this.minScale = minScale;
        this.minAlpha = minAlpha;
        this.interpolator = new AccelerateDecelerateInterpolator();
        this.isHardwareAccelerationEnabled = true;
    }
    
    /**
     * Set custom interpolator for smoother animations.
     *
     * @param interpolator the time interpolator to use
     * @return this transformer instance for chaining
     */
    public DepthPageTransformer setInterpolator(@NonNull TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }
    
    /**
     * Set hardware acceleration mode for better performance on capable devices.
     *
     * @param enabled true to enable hardware acceleration, false to disable
     * @return this transformer instance for chaining
     */
    public DepthPageTransformer setHardwareAccelerationEnabled(boolean enabled) {
        this.isHardwareAccelerationEnabled = enabled;
        return this;
    }
    
    @Override
    public void transformPage(@NonNull View view, float position) {
        // Enable/disable hardware acceleration based on configuration
        if (view.getLayerType() != (isHardwareAccelerationEnabled ? 
                View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE)) {
            view.setLayerType(
                    isHardwareAccelerationEnabled ? 
                    View.LAYER_TYPE_HARDWARE : View.LAYER_TYPE_NONE, 
                    null);
        }
        
        // Apply the appropriate animation style
        switch (animationStyle) {
            case STYLE_ZOOM:
                applyZoomAnimation(view, position);
                break;
            case STYLE_FADE:
                applyFadeAnimation(view, position);
                break;
            case STYLE_CUBE:
                applyCubeAnimation(view, position);
                break;
            case STYLE_DEPTH:
            default:
                applyDepthAnimation(view, position);
                break;
        }
    }
    
    /**
     * Apply depth-based animation with optimized calculations.
     */
    private void applyDepthAnimation(View view, float position) {
        int pageWidth = view.getWidth();
        
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);
            view.setTranslationZ(0f);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page.
            float positionOffset = Math.abs(position);
            float interpolatedOffset = interpolator.getInterpolation(1 - positionOffset);
            
            view.setAlpha(interpolatedOffset);
            view.setTranslationX(pageWidth * position * 0.75f); // Reduce translation for smoother feel
            view.setTranslationZ(0f);
            
            // Gradually scale the page as it moves offscreen
            float scaleFactor = minScale + (1 - minScale) * interpolatedOffset;
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            float interpolatedPosition = interpolator.getInterpolation(position);
            view.setAlpha(Math.max(minAlpha, 1 - interpolatedPosition));
            
            // Counteract the default slide transition with a smoother curve.
            view.setTranslationX(pageWidth * -position * 0.5f);
            
            // Move it behind the left page with a shadow effect
            view.setTranslationZ(-1f);
            
            // Scale the page down with interpolation for smoother transition
            float scaleFactor = minScale + (1 - minScale) * (1 - interpolatedPosition);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
            view.setTranslationZ(0f);
        }
    }
    
    /**
     * Apply zoom-based animation with optimized calculations.
     */
    private void applyZoomAnimation(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        
        if (position < -1 || position > 1) {
            view.setAlpha(0f);
        } else {
            // Calculate opacity based on position with smooth interpolation
            float alphaFactor = Math.max(minAlpha, 1 - Math.abs(position));
            view.setAlpha(alphaFactor);
            
            // Zoom effect
            float scaleFactor = Math.max(minScale, 1 - Math.abs(position));
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;
            
            if (position < 0) {
                view.setTranslationX(horizontalMargin - verticalMargin / 2);
            } else {
                view.setTranslationX(-horizontalMargin + verticalMargin / 2);
            }
            
            // Apply scale effect
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
    
    /**
     * Apply fade-based animation with optimized calculations.
     */
    private void applyFadeAnimation(View view, float position) {
        if (position < -1 || position > 1) {
            view.setAlpha(0f);
        } else if (position <= 0) {
            view.setAlpha(1 + position);
            view.setTranslationX(view.getWidth() * -position * 0.5f);
            view.setScaleX(1);
            view.setScaleY(1);
        } else if (position <= 1) {
            // Fade the page out
            view.setAlpha(1 - position);
            
            // Counteract the default slide transition
            view.setTranslationX(view.getWidth() * -position * 0.5f);
            
            // Scale the page down
            float scaleFactor = minScale + (1 - minScale) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
    
    /**
     * Apply cube-based animation with optimized calculations.
     */
    private void applyCubeAnimation(View view, float position) {
        // Rotate the pages for a cube effect
        if (position < -1) {
            view.setAlpha(0);
        } else if (position <= 0) {
            view.setAlpha(1);
            view.setPivotX(view.getWidth());
            view.setRotationY(90 * Math.abs(position));
        } else if (position <= 1) {
            view.setAlpha(1);
            view.setPivotX(0);
            view.setRotationY(-90 * Math.abs(position));
        } else {
            view.setAlpha(0);
        }
    }
}
