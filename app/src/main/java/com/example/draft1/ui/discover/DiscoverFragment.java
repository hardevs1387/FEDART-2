package com.example.draft1.ui.discover;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.draft1.databinding.FragmentDiscoverBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class DiscoverFragment extends Fragment {

    private FragmentDiscoverBinding binding;

    List<Bitmap> images = new ArrayList<>();

    DiscoverAdapter adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();

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


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter
                = new DiscoverAdapter(images);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

            loadImageFromStorage();

//        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(FirstFragment.this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //public void register(View view, Bundle savedInstanceState);

}
