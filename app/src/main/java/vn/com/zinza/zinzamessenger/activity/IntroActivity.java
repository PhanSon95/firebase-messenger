/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package vn.com.zinza.zinzamessenger.activity;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import vn.com.zinza.zinzamessenger.R;
import vn.com.zinza.zinzamessenger.utils.AndroidUtilities;
import vn.com.zinza.zinzamessenger.utils.PrefManager;

public class IntroActivity extends Activity{
    private ViewPager mViewPager;
    private PageIndicator mIndicator;
    private ImageView mTopImage1;
    private ImageView mTopImage2;
    private int mLastPage = 0;
    private boolean mStartPressed = false;
    private int[] mIcons;
    private int[] mMessages;
    private TextView mStartLogin, mStartMessagingButton;
    private PrefManager mPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mPrefManager = new PrefManager(this);
        if (!mPrefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        setContentView(R.layout.intro_layout);


        mIcons = new int[]{
                R.drawable.zinza_icon,
                R.drawable.chat,
                R.drawable.search_friend,
                R.drawable.transfer_files
        };
        mMessages = new int[]{
                R.string.app_overview_1st_item_text,
                R.string.app_overview_2nd_item_text,
                R.string.app_overview_3rd_item_text,
                R.string.app_overview_4th_item_text
        };
        mViewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        mStartMessagingButton = (TextView) this.findViewById(R.id.start_messaging_button);
        mStartLogin = (TextView) this.findViewById(R.id.startLogin);
        mStartMessagingButton.setText(getString(R.string.StartMessaging));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(mStartMessagingButton, "translationZ", AndroidUtilities.dp(2, this), AndroidUtilities.dp(4, this)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(mStartMessagingButton, "translationZ", AndroidUtilities.dp(4, this), AndroidUtilities.dp(2, this)).setDuration(200));
            mStartMessagingButton.setStateListAnimator(animator);
        }
        mTopImage1 = (ImageView) findViewById(R.id.icon_image1);
        mTopImage2 = (ImageView) findViewById(R.id.img_get_avatar);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

        mTopImage2.setVisibility(View.GONE);
        mViewPager.setAdapter(new IntroAdapter());
        mViewPager.setPageMargin(0);
        mViewPager.setOffscreenPageLimit(1);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (mViewPager.getCurrentItem() == 3) {
                    mStartLogin.setVisibility(View.VISIBLE);
                    mStartMessagingButton.setVisibility(View.INVISIBLE);
                } else {
                    mStartLogin.setVisibility(View.INVISIBLE);
                    mStartMessagingButton.setVisibility(View.VISIBLE);
                }
                if (mLastPage != mViewPager.getCurrentItem()) {
                    mLastPage = mViewPager.getCurrentItem();

                    final ImageView fadeoutImage;
                    final ImageView fadeinImage;
                    if (mTopImage1.getVisibility() == View.VISIBLE) {
                        fadeoutImage = mTopImage1;
                        fadeinImage = mTopImage2;

                    } else {
                        fadeoutImage = mTopImage2;
                        fadeinImage = mTopImage1;
                    }

                    fadeinImage.bringToFront();
                    fadeinImage.setImageResource(mIcons[mLastPage]);
                    fadeinImage.clearAnimation();
                    fadeoutImage.clearAnimation();

                    Animation outAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_out);
                    outAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            fadeoutImage.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    Animation inAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_in);
                    inAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            fadeinImage.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                    fadeoutImage.startAnimation(outAnimation);
                    fadeinImage.startAnimation(inAnimation);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mStartMessagingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStartPressed) {
                    return;
                }
                mStartPressed = true;
                launchHomeScreen();
            }
        });

        mStartLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchHomeScreen();
            }
        });

    }


    private void launchHomeScreen() {
        mPrefManager.setFirstimeLaunch(false);
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private class IntroAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.intro_view_layout, null);
            TextView messageTextView = (TextView) view.findViewById(R.id.message_text);
            container.addView(view, 0);
            messageTextView.setText(AndroidUtilities.replaceTags(getString(mMessages[position]), getApplicationContext()));

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
}
