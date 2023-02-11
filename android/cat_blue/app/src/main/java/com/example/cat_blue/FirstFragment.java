package com.example.cat_blue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cat_blue.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    MainViewModel mainModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainModel.bluetoothStatus.observe(getViewLifecycleOwner(),status -> {
            if (status.equals("Connected")) {
                binding.moveLayout.setVisibility(View.VISIBLE);
                binding.disBlue.setVisibility(View.INVISIBLE);
            } else {
                binding.moveLayout.setVisibility(View.INVISIBLE);
                binding.disBlue.setVisibility(View.VISIBLE);
            }
        });

        binding.buttonFirst.setOnClickListener(v ->  {
            mainModel.firmata.sendDigital(0,1);
        });

        binding.buttonSecond.setOnClickListener(v -> {
            mainModel.firmata.sendDigital(1,1);
        });

        binding.btnStart.setOnClickListener(v -> {
            mainModel.firmata.sendSetPinMode(0,1);
        });

        binding.btnStop.setOnClickListener(v -> {
            mainModel.firmata.sendSetPinMode(0,0);
        });

        binding.btnLeft.setOnClickListener(v -> {
            mainModel.firmata.sendDigital(2,1);
        });

        binding.btnRight.setOnClickListener(v -> {
            mainModel.firmata.sendDigital(3,1);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}