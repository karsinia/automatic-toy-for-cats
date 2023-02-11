package com.example.cat_blue;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;

import com.example.cat_blue.databinding.FragmentFirstBinding;
import com.example.cat_blue.util.ByteReader;
import com.example.cat_blue.util.Firmata;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cat_blue.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.interfaces.DeviceCallback;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    MainViewModel mainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        permissionGranted();

        Bluetooth bluetooth = new Bluetooth(this);
        bluetooth.setReader(ByteReader.class);
        mainModel.setBluetooth(bluetooth);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainModel.bluetooth.isConnected())
                    Snackbar.make(view, "HC-06-PG", Snackbar.LENGTH_LONG)
                            .setAction("Disconnect", v-> {
                                mainModel.bluetooth.disconnect();
                            }).show();
                else
                    Snackbar.make(view, "HC-06-PG", Snackbar.LENGTH_LONG)
                            .setAction("Connect", v -> {
                                mainModel.bluetooth.connectToName("HC-06-PG");
                            }).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainModel.bluetooth.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainModel.bluetooth.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainModel.bluetooth.isConnected())
            mainModel.bluetooth.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void permissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                    new String[]{
                            android.Manifest.permission.BLUETOOTH,
                            android.Manifest.permission.BLUETOOTH_SCAN,
                            android.Manifest.permission.BLUETOOTH_ADVERTISE,
                            android.Manifest.permission.BLUETOOTH_CONNECT
                    },
                    1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{
                            android.Manifest.permission.BLUETOOTH
                    },
                    1);
        }
    }
}