package com.example.draft1.ui.contribute;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.draft1.databinding.FragmentContributionNewBinding;
import com.example.draft1.ui.discover.DiscoverAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class contributeFragment extends Fragment {

    private FragmentContributionNewBinding binding;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_PERMISSION = 9;
    //int count = 1;
    ImageView imageView;

    List<Bitmap> images = new ArrayList<>();

    DiscoverAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentContributionNewBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.UploadImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                checkPermission();
            }
        });

        adapter
                = new DiscoverAdapter(images);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        loadImageFromStorage();

    }

    private void loadImageFromStorage(){

        images.clear();

        try {
            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            File[] imagep = directory.listFiles();

            for (int i = 0; i < imagep.length; i++) {
                File f = new File(directory, imagep[i].getAbsolutePath());

                File fromFile = new File(imagep[i].getAbsolutePath());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(fromFile));
                images.add(b);
            }
            adapter.notifyDataSetChanged();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    void checkPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_PERMISSION);
            } else {

                selectImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 99);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 100);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                } else {
                    selectImage();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_CANCELED && requestCode != REQUEST_IMAGE_CAPTURE) {
            switch (requestCode) {
                case 99:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        saveToInternalStorage(selectedImage);
                        loadImageFromStorage();
                    }
                    break;
                case 100:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                saveToInternalStorage(bitmap);
                                cursor.close();

                                loadImageFromStorage();
                            }
                        }
                    }
                    break;
            }
        }
//        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//            if (count == 7) {
//                count = 0;
//            }
//
//            switch (count) {
//                case 1:
//
//                    imageView.setImageBitmap(imageBitmap);
//                    break;
//                case 2:
//                    final ImageView imageView1 = binding.camImg1;
//                    imageView1.setImageBitmap(imageBitmap);
//                    break;
//                case 3:
//                    final ImageView imageView2 = binding.camImg2;
//                    imageView2.setImageBitmap(imageBitmap);
//                    break;
//                case 4:
//                    final ImageView imageView3 = binding.camImg3;
//                    imageView3.setImageBitmap(imageBitmap);
//                    break;
//                case 5:
//                    final ImageView imageView4 = binding.camImg4;
//                    imageView4.setImageBitmap(imageBitmap);
//                    break;
//
//                case 6:
//                    final ImageView imageView5 = binding.camImg5;
//                    imageView5.setImageBitmap(imageBitmap);
//                    break;
//            }
//
//            count++;
//        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        // Create imageDir
        File mypath = new File(directory, System.currentTimeMillis() + "_tourist.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}