package com.bizfit.bizfit.views;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bizfit.bizfit.R;
import com.bizfit.bizfit.Tracker;
import com.bizfit.bizfit.activities.MainActivity;
import com.bizfit.bizfit.fragments.TabTrackables;
import com.bizfit.bizfit.utils.AssetManagerOur;

public class TrackableView extends FrameLayout {

    private View layout;
    public static final float animSpeed = 6;
    private Tracker tracker;

    private TextView trackerName;
    private TextView targetAmount;
    private TextView timeLeftAmount;
    private TextView progressPercent;

    public TrackableView(Context context, Tracker tracker, LayoutInflater inflater) {
        super(context);
        this.tracker = tracker;
        layout = inflater.inflate(R.layout.view_trackable_expandable, this, false);
        this.addView(layout);
        init();
    }

    public TrackableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TrackableView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Is the default constructor when creating this object progmatically.
     *
     * @param view    View on which data is displayed on.
     * @param tracker Tracker from which data is pulled from.
     */


    /**
     * Formats components which can be formatted without data from Tracker.
     */
    private void init() {
        this.trackerName = (TextView) layout.findViewById(R.id.tracker_name);
        this.targetAmount = (TextView) layout.findViewById(R.id.target_amount);
        this.timeLeftAmount = (TextView) layout.findViewById(R.id.time_left_amount);
        this.progressPercent = (TextView) layout.findViewById(R.id.progress_percentage);

        trackerName.setTypeface(AssetManagerOur.getFont(AssetManagerOur.medium));
        targetAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        timeLeftAmount.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        progressPercent.setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        ((TextView) findViewById(R.id.target_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        ((TextView) findViewById(R.id.time_left_label)).setTypeface(AssetManagerOur.getFont(AssetManagerOur.regular));
        findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity parentActivity = (MainActivity) getContext();
                TabTrackables parentFragment = (TabTrackables) parentActivity
                        .getSupportFragmentManager().getFragments().get(0);
                System.out.println("Tracker index: " + tracker.getIndex());
                parentFragment.launchViewTrackerActivity(tracker
                        , ((ViewGroup) getParent()).indexOfChild(TrackableView.this));
            }
        });

        layout.findViewById(R.id.card_view).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        update();
    }


    /**
     * Returns the Tracker from which data is pulled from.
     *
     * @return Tracker from which data is pulled from.
     */
    public Tracker getTracker() {
        return tracker;
    }

    /**
     * Pulls data from the given tracker.
     *
     * Does nothing if tracker is not set. When creating this view directly
     * from .xml, it is advised that setTracker() is called followed by this
     * method. Otherwise template values are shown.
     */
    public void update() {
            //System.out.println("Tracker: " + tracker);
            Tracker.RemainingTime time = tracker.getTimeRemaining();
            timeLeftAmount.setText((int) time.getTimeRemaining() + " " + time.getTimeType());
            trackerName.setText(tracker.getName());
            trackerName.setTextColor(tracker.getColor());
            targetAmount.setText((int) tracker.getTargetProgress() + "");
            progressPercent.setText(((int) (Math.floor(tracker.getProgressPercent() * 100))) + "");
            //animatePercentage();

    }

    // TODO Clean this up.
    private void animatePercentage() {
        float value, max, min;

        // Prevents the animation from targeting above the maximum
        // allowed value.
        max = (((value = tracker.getProgressPercent()) * 100) < 999)
                ? value : (999 / 100f);
        min = Float.parseFloat(progressPercent.getText().toString()) / 100;

        ValueAnimator animator = ValueAnimator.ofFloat(0, max);
        animator.setDuration(getResources().getInteger(R.integer.from_zero_anim_duration));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                progressPercent.setText(((int) (Math.floor(value * 100))) + "");
            }
        });

        animator.start();
    }

    public void deleteViewAndTracker() {
        // TODO Animate the removal.
        tracker.delete();
        ((ViewGroup) getParent()).removeView(this);
    }

    /**
     * Animates the view when it's corresponding tracker is deleted.
     */
    public void collapse() {
        final int initialHeight = this.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    setVisibility(View.GONE);
                } else {
                    getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // TODO Deleting view here!
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });

        // 1dp/ms
        a.setDuration((int) (initialHeight / getContext().getResources().getDisplayMetrics().density));
    }

    /**
     * Animates the view when it's corresponding tracker is first created.
     */
    public void expand() {
        this.measure(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = this.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        getLayoutParams().height = 1;
        setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                getLayoutParams().height = interpolatedTime == 1
                        ? FrameLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) ((targetHeight / getContext().getResources().getDisplayMetrics().density) * animSpeed));
        startAnimation(a);
    }

    public void setTracker(Tracker tracker) {
        this.tracker = tracker;
        update();
    }
}
