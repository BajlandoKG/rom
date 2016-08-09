/*

    Copyright © 2016, Giuseppe Montuoro.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.peppe130.rominstaller.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.peppe130.rominstaller.R;
import com.peppe130.rominstaller.ControlCenter;
import org.michaelevans.colorart.library.ColorArt;


@SuppressWarnings("ConstantConditions")
public class SplashScreenActivity extends Activity {

    Bitmap mBitmap;
    ColorArt mColorArt;
    ImageView mImageView;
    RelativeLayout mRelativeLayout;
    BitmapFactory.Options mOptions;
    Boolean isActivityVisible = true;
    Integer mHeaderColor, mTheme = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_layout);

        if (ControlCenter.SHOULD_SHOW_SPLASH_SCREEN) {
            mRelativeLayout = (RelativeLayout) findViewById(R.id.splash_screen_layout);
            mImageView = (ImageView) findViewById(R.id.imageView);

            try {
                mTheme = getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.theme;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            switch (mTheme) {
                case R.style.AppTheme_Light:
                    mHeaderColor = ContextCompat.getColor(this, R.color.colorPrimary_Theme_Light);
                    setTaskDescription(new ActivityManager.TaskDescription(null, null, mHeaderColor));
                    break;
                case R.style.AppTheme_Dark:
                    mHeaderColor = ContextCompat.getColor(this, R.color.colorPrimary_Theme_Dark);
                    setTaskDescription(new ActivityManager.TaskDescription(null, null, mHeaderColor));
                    break;
            }

            mOptions = new BitmapFactory.Options();
            mOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), ControlCenter.SPLASH_SCREEN_IMAGE, mOptions);
            mBitmap = decodeBitmapFromResource(getResources(), ControlCenter.SPLASH_SCREEN_IMAGE, 500, 500);

            mColorArt = new ColorArt(mBitmap);
            mRelativeLayout.setBackgroundColor(mColorArt.getBackgroundColor());
            mImageView.setImageBitmap(mBitmap);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isActivityVisible) {
                        if (ControlCenter.SHOULD_SHOW_DISCLAIMER_SCREEN) {
                            startActivity(new Intent(SplashScreenActivity.this, AgreementActivity.class));
                        } else {
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        }
                    }
                    finish();
                }
            }, ControlCenter.SPLASH_SCREEN_DELAY);
        } else {
            if (ControlCenter.SHOULD_SHOW_DISCLAIMER_SCREEN) {
                startActivity(new Intent(SplashScreenActivity.this, AgreementActivity.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            }
            finish();
        }

    }

    public int calculateInSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap decodeBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options mOptions = new BitmapFactory.Options();
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, mOptions);
        mOptions.inSampleSize = calculateInSize(mOptions, reqWidth, reqHeight);
        mOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, mOptions);
    }

    @Override
    public void onBackPressed() {
        // Do nothing to lock back softkey
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActivityVisible) {
            isActivityVisible = true;
            recreate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
    }

}
