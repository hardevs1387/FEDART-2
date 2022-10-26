package com.example.draft1.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.draft1.R;
import com.example.draft1.databinding.FragmentProfileBinding;
import com.example.draft1.models.User;
import com.example.draft1.ui.discover.DiscoverAdapter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    SharedPreferences mPrefs;

    List<Bitmap> images = new ArrayList<>();

    DiscoverAdapter adapter;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        mPrefs = getActivity().getSharedPreferences(getString(R.string.userPref), MODE_PRIVATE);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.userdata), "");
        User obj = gson.fromJson(json, User.class);

        binding.nameTV.setText(obj.getName());

        adapter
                = new DiscoverAdapter(images);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        loadImageFromStorage();

        binding.btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ProfileFragment.this).popBackStack();
            }
        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

