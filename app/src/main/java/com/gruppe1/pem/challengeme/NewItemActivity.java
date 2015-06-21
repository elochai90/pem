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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class NewItemActivity extends Activity {

    private ImageView ImgPhoto;
    private RatingBar ratingBar;
    private EditText itemNameExitText;
    private LinearLayout attributesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        ImgPhoto = (ImageView) findViewById(R.id.itemDetailImage);

        ImgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        final Activity thisActivity = this;
        FloatingActionButton saveItemFAB = (FloatingActionButton) findViewById(R.id.save_item);
        saveItemFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
                // TODO: save item
            }
        });

        itemNameExitText = (EditText) findViewById(R.id.itemName);
//            if(value is set) TODO: check if value was already set
        itemNameExitText.setText("Test Item Name");



        attributesView = (LinearLayout) findViewById(R.id.itemDetailAttributes);

        // TODO: get all attributes with values for this Item
        for(int i = 0; i<= 5; i++) {
            LinearLayout attributeLayout = new LinearLayout(this);
            attributeLayout.setOrientation(LinearLayout.HORIZONTAL);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            attributeLayout.setPadding(padding, padding, padding, padding);
            LinearLayout.LayoutParams attributeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            attributeLayout.setLayoutParams(attributeLayoutParams);

            TextView attributeName = new TextView(this);
            attributeName.setTextSize(18);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
            ViewGroup.LayoutParams attibuteNameLayoutParams = new ViewGroup.LayoutParams(width, height);
            attributeName.setLayoutParams(attibuteNameLayoutParams);
            // TODO: real attr names and values
            attributeName.setText("attr" + ":");

            View attributeValue;
//            if(attribute is boolean)
            if(i == 0) {
                RadioGroup attrValueRadioGroup = new RadioGroup(this);
                ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                attrValueRadioGroup.setLayoutParams(attibuteValueLayoutParams);
                attrValueRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

                ViewGroup.LayoutParams boolButtonLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                RadioButton boolTrueButton = new RadioButton(this);
                boolTrueButton.setLayoutParams(boolButtonLayoutParams);
                boolTrueButton.setText("yes");
                boolTrueButton.setTextSize(18);

                RadioButton boolFalseButton = new RadioButton(this);
                boolFalseButton.setLayoutParams(boolButtonLayoutParams);
                boolFalseButton.setText("no");
                boolFalseButton.setTextSize(18);

                attrValueRadioGroup.addView(boolTrueButton);
                attrValueRadioGroup.addView(boolFalseButton);

                attributeValue = attrValueRadioGroup;

            } else {
                EditText textAttributeValue = new EditText(this);
                textAttributeValue.setTextSize(18);
                ViewGroup.LayoutParams attibuteValueLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
//              if(value is set) TODO: check if value was already set
                textAttributeValue.setText("37 test value");
                textAttributeValue.setLayoutParams(attibuteValueLayoutParams);
                attributeValue = textAttributeValue;
            }


            attributeLayout.addView(attributeName);
            attributeLayout.addView(attributeValue);

            attributesView.addView(attributeLayout, attributesView.getChildCount()-1);
        }
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                try {

                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ImgPhoto.setImageBitmap(photo);

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();

                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    ImgPhoto.setImageBitmap(yourSelectedImage);
                }catch(FileNotFoundException e){
                    Log.w("FileNotFoundExeption: ", e.toString());
                }

            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
