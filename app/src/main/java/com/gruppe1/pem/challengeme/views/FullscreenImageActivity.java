package com.gruppe1.pem.challengeme.views;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gruppe1.pem.challengeme.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class FullscreenImageActivity extends AppCompatActivity {

   @Bind (R.id.toolbar)
   Toolbar toolbar;
   @Bind (R.id.imageView)
   ImageViewTouch mImageView;
   @Bind (R.id.progressbar)
   ProgressBar mProgressBar;
   @Bind (R.id.frameLayout)
   FrameLayout mFrameLayout;

   @TargetApi (Build.VERSION_CODES.HONEYCOMB)
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_fullscreen_image);
      ButterKnife.bind(this);
      setSupportActionBar(toolbar);
      mImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
      mImageView.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {

         @Override
         public void onSingleTapConfirmed() {
            Log.d(FullscreenImageActivity.this.getLocalClassName(), "onSingleTapConfirmed");
            // Start low profile mode and hide ActionBar
            if (getSupportActionBar().isShowing()) {
               getSupportActionBar().hide();
            } else {
               getSupportActionBar().show();
            }
         }
      });
      setSupportActionBar(toolbar);
      final ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
         actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setDisplayShowTitleEnabled(false);
      }

      Bundle extras = getIntent().getExtras();
      if (extras != null) {
         ;
         String imageUrl = extras.getString("imageurl");
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inPreferredConfig = Bitmap.Config.ARGB_8888;
         Bitmap fullscreenBitmap = BitmapFactory.decodeFile(imageUrl, options);
         Matrix matrix = mImageView.getDisplayMatrix();
         mImageView.setImageBitmap(fullscreenBitmap, matrix, 1, 4);
      }
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
         onBackPressed();
         return true;
      }
      return super.onOptionsItemSelected(item);
   }
}

