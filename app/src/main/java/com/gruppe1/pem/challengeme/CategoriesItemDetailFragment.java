package com.gruppe1.pem.challengeme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CategoriesItemDetailFragment extends Fragment {

    private ImageView imgPhoto;
    Activity activity;

    public CategoriesItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories_item_detail, container, false);

        imgPhoto = (ImageView) view.findViewById(R.id.itemDetailImage);

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        return view;
    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                CategoriesActivity activity = (CategoriesActivity)getActivity();
                if (options[item].equals("Take Photo")){
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        activity.callActivityMethod(intent, imgPhoto,1);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        activity.callActivityMethod(intent,imgPhoto, 2);
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }
}
