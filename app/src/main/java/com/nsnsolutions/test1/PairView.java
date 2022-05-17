package com.nsnsolutions.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class PairView extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> arrayAdapter;
    ArrayList arrayList = new ArrayList();

    //Set<BluetoothDevice> availableDevices ;
     ArrayList<BluetoothDevice> availableDevices = new ArrayList<BluetoothDevice>();
    ListView pairList;
    Button pair_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_view);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairList = findViewById(R.id.pairlist_view);
        pair_bt = findViewById(R.id.pairSearch_bt);

        //availableDevices = bluetoothAdapter.startDiscovery();
        //bluetoothAdapter.startDiscovery();

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, arrayList);
        pairList.setAdapter(arrayAdapter);
        pairList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Intent intent = new Intent(MainActivity.this,ChatView.class);
                //startActivity(new Intent(MainActivity.this,ChatView.class));
                //Object obj = adapterView.getItemAtPosition(i);
               // String val = obj.toString();
                BluetoothDevice blt = availableDevices.get(i);
                //intent.putExtra("chater", val);
                //intent.putExtra("deviceDetail",blt);
                blt.createBond();

                Log.d("pairing",blt.getName() + blt.getAddress() + blt.getBondState());

                //startActivity(intent);

            }

        });




    }


    public void discoverDevices(View v) {
        bluetoothAdapter.startDiscovery();


    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);



                if(arrayList.contains(device.getName())){}
                else {
                    availableDevices.add(device);
                    arrayList.add(device.getName());
                    arrayAdapter.notifyDataSetChanged();

                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFilter);
    }


}