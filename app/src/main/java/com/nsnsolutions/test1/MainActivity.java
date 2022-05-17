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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    CheckBox enable_bt, visible_bt ;
    ImageView search_bt, pair_bt;
    TextView name_bt;
    ListView listView;


    //final ArrayList list = new ArrayList();
    //ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
    //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
// ArrayAdapter adapter;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> PairedDevices;
    //private Object IntentFilter;
//    private Set<BluetoothDevice> DiscoverableDevices;
    // private Object BluetoothAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enable_bt = findViewById(R.id.enable_bt);
        visible_bt = findViewById(R.id.visible_bt);
        search_bt = findViewById(R.id.search_bt);
        name_bt = findViewById(R.id.name_bt);
        listView = findViewById(R.id.list_view);
        pair_bt = findViewById(R.id.pair_bt);

        name_bt.setText(getLocalBluetoothName());
        BA = BluetoothAdapter.getDefaultAdapter();

        if (BA == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (BA.isEnabled()) {
            enable_bt.setChecked(true);
        }

        enable_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    BA.disable();
                    Toast.makeText(MainActivity.this, "Turned off", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0);
                    Toast.makeText(MainActivity.this, "Turned On", Toast.LENGTH_SHORT).show();
                }
            }
        });

        visible_bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(getVisible, 0);
                    Toast.makeText(MainActivity.this, "Visible for 2 min", Toast.LENGTH_SHORT).show();
                }
            }
        });


        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                list();

            }
        });


    }

    public void pairView(View view){
        Intent intent = new Intent(this,PairView.class);
        startActivity(intent);
    }

    private void list() {
        PairedDevices = BA.getBondedDevices();

        final ArrayList list = new ArrayList();
        final ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice bt : PairedDevices) {
            list.add(bt.getName());
            devices.add(bt);

        }


        // to populate listview
        Toast.makeText(this, "Showing Devices", Toast.LENGTH_SHORT).show();
        //ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,ChatView.class);
                //startActivity(new Intent(MainActivity.this,ChatView.class));
                Object obj = adapterView.getItemAtPosition(i);
                String val = obj.toString();
                BluetoothDevice blt = devices.get(i);
                intent.putExtra("chater", val);
                intent.putExtra("deviceDetail",blt);
                Toast.makeText(MainActivity.this, blt.toString(), Toast.LENGTH_SHORT).show();

                startActivity(intent);

            }

        });
    }




    public String getLocalBluetoothName() {
        if (BA == null) {
            BA = BluetoothAdapter.getDefaultAdapter();
        }
        String name = BA.getName();
        if (name == null) {
            name = BA.getAddress();
        }
        return name;
    }



}
