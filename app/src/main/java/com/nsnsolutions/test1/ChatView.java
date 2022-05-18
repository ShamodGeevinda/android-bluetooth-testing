package com.nsnsolutions.test1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Set;
import java.nio.charset.Charset;
import java.util.UUID;

public class ChatView extends AppCompatActivity  {

    TextView chatName;
    BluetoothDevice device, mBTDevice;
    ImageView connectBt;
    Button btnSend;
    EditText etSend;
    TextView incommingMessages;
    StringBuilder messages;

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // BluetoothAdapter mAdapter;
    //    private BluetoothAdapter BA;
    //    private Set<BluetoothDevice> PairedDevices;
    BluetoothConnection mBluetoothConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        chatName = findViewById(R.id.chatname);
        connectBt = findViewById(R.id.connect_bt);
        btnSend = findViewById(R.id.send_bt);
        etSend = findViewById(R.id.data);

        incommingMessages = (TextView)findViewById(R.id.incommingMessage) ;
        messages = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incommingMessage"));

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
                mBTDevice = device;
                mBluetoothConnection = new BluetoothConnection(ChatView.this);
                Log.d("Main Activity",device.getName() + device.getAddress() + device.getBondState());

                startConnection();


            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] bytes = etSend.getText().toString().getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
                etSend.setText("");
            }
        });
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");
            messages.append(text+ "\n");
            incommingMessages.setText(messages);
        }
    };

    private final BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 3 cases
                // bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        mBTDevice = mDevice;
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



    //create method for starting connection
    //***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBTDevice,MY_UUID_INSECURE);
    }

    /**
     * starting chat service method
     */
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        //Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

}