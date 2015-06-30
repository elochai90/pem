package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class CategoriesItemDetailActivity extends Activity {



//    !!!! NOT USED ANY MORE !!!!



//    private ImageView ImgPhoto;
//    private RatingBar ratingBar;
//
//    private TextView itemDetailNameValue;
//    private LinearLayout attributesView;
//
//    private int itemId;
//    private Item item;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_categories_item_detail);
//
//        ImgPhoto = (ImageView) findViewById(R.id.itemDetailImage);
////        ImgPhoto.setImageResource(); TODO: set Item Image if available
//
//        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
//        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(1).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//
//        attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);
//        itemDetailNameValue = (TextView) findViewById(R.id.itemDetailNameValue);
//
//
//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//            if(extras == null) {
//                itemId = -1;
//            } else {
//                itemId = extras.getInt(Constants.EXTRA_ITEM_ID);
//            }
//        } else {
//            itemId = -1;
//        }
//        DataBaseHelper db_helper = new DataBaseHelper(this);
//        db_helper.init();
//        item = new Item(this, itemId, db_helper);
//        itemId = item.getId();
//
//
//        setupAttributesLayout();
//        setContent();
//
//
//        FloatingActionButton editItemFAB = (FloatingActionButton) findViewById(R.id.edit_item);
//        editItemFAB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClassName(getPackageName(), getPackageName() + ".NewItemActivity");
//                Bundle b = new Bundle();
//                b.putInt(Constants.EXTRA_ITEM_ID, itemId);
//                intent.putExtras(b);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void setContent() {
//        setTitle(item.getName());
//        itemDetailNameValue.setText(item.getName());
//        ratingBar.setRating(item.getRating());
//        // TODO: all the attributes
//
//    }
//
//    private void setupAttributesLayout() {
//
//        ArrayList<AttributeType> allAttributeTypes = AttributeType.getAttributeTypes(getApplicationContext());
//
//        Iterator allAttrTypesIterator =  allAttributeTypes.iterator();
//
//
//        // TODO: get all attributes with values for this Item
//        while(allAttrTypesIterator.hasNext()) {
//        // TODO: get all attributes with values for this Item
////        for(int i = 0; i<= 5; i++) {
//            LinearLayout attributeLayout = new LinearLayout(this);
//            attributeLayout.setOrientation(LinearLayout.HORIZONTAL);
//            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
//            attributeLayout.setPadding(padding, padding, padding, padding);
//            LinearLayout.LayoutParams attributeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            attributeLayout.setLayoutParams(attributeLayoutParams);
//
//            TextView attributeName = new TextView(this);
//            attributeName.setTextSize(18);
//            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
//            ViewGroup.LayoutParams attibuteNameLayoutParams = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//            attributeName.setLayoutParams(attibuteNameLayoutParams);
//
//            TextView attributeValue = new TextView(this);
//            attributeValue.setTextSize(18);
//            ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            attributeValue.setLayoutParams(attibuteValueLayoutParams);
//
////            // TODO: real attr names and values
////            attributeName.setText("attr" + ":");
////            attributeValue.setText("37 test value");
//
//            AttributeType dbColumnName = (AttributeType) allAttrTypesIterator.next();
//            attributeName.setText(dbColumnName.getName() + ":");
//            attributeValue.setText("TODO");
//
//            attributeLayout.addView(attributeName);
//            attributeLayout.addView(attributeValue);
//
//            attributesView.addView(attributeLayout);
//        }
//
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_categories_item_detail, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
