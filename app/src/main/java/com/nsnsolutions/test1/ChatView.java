package com.nsnsolutions.test1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Set;

public class ChatView extends AppCompatActivity  implements Serializable{

    TextView chatName;
    BluetoothDevice device;
    ImageView connectBt;
    // BluetoothAdapter mAdapter;
    //    private BluetoothAdapter BA;
    //    private Set<BluetoothDevice> PairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        chatName = findViewById(R.id.chatname);
        connectBt = findViewById(R.id.connect_bt);

        // to catch bond state changes
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReciever, filter);

        // retrieving data
        String chatername = getIntent().getStringExtra("chater");
        device = getIntent().getExtras().getParcelable("deviceDetail");

        chatName.setText(chatername);

        connectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatView.this, "pairing", Toast.LENGTH_SHORT).show();

                //BluetoothDevice newD = (BluetoothDevice) device;
                device.createBond();

                Log.d("Main Activity",device.getName() + device.getAddress() + device.getBondState());


            }
        });
    }

    private final BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 3 cases
                // bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {

                }
                // creating a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {

                }
                // breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {

                }
            }
        }
    };

//    @Override
//    public void onClick(View view) {
//            BluetoothDevice newD = (BluetoothDevice) device;
//            Toast.makeText(ChatView.this, "pairing", Toast.LENGTH_SHORT).show();
//            newD.createBond();
//            Log.d("Main Activity", "Shamod");
//
//    }

//    @Override
//    public void onClick(View view) {
//        //mAdapter.cancelDiscovery();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            BluetoothDevice newD = (BluetoothDevice) device;
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                newD.createBond();
//                Log.d("Main Activity", "Shamod");
//                return;
//            }
//
//        }
//    }



}