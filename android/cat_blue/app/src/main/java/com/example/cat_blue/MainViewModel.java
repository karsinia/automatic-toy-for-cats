package com.example.cat_blue;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cat_blue.util.Firmata;
import com.example.cat_blue.util.FirmataVersionData;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainViewModel extends ViewModel {
    Bluetooth bluetooth;

    public Firmata firmata;
    public MutableLiveData<String> bluetoothStatus = new MutableLiveData<>();
    public MutableLiveData<FirmataVersionData> firmataVersionData = new MutableLiveData<>();

    public MainViewModel() {
        bluetoothStatus.setValue("Disconnected");
        firmataVersionData.setValue(new FirmataVersionData());
    }

    void setBluetooth(Bluetooth bluetooth) {
        this.bluetooth = bluetooth;
        firmata = new Firmata(this.bluetooth);

        bluetooth.setDeviceCallback(new DeviceCallback() {
            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                bluetoothStatus.postValue("Connected");
            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device, String message) {
                bluetoothStatus.postValue("Disconnected");
            }

            @Override
            public void onMessage(byte[] message) {
                firmata.processInput(message);
            }

            @Override
            public void onError(int errorCode) {
                bluetoothStatus.postValue("Error");
            }

            @Override
            public void onConnectError(BluetoothDevice device, String message) {
                bluetoothStatus.postValue("Connect Error");
            }
        });

        firmata.attach((m, n) -> {
            FirmataVersionData version = new FirmataVersionData();
            version.setMajor(m);
            version.setMinor(n);
            firmataVersionData.postValue(version);
        });
    }

    public Bluetooth getBluetooth() {
        return bluetooth;
    }
}
