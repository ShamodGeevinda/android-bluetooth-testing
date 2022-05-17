package com.nsnsolutions.test1;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection {
    private static final  String TAG = "BTConnect";
    private static final String appName = "myapp";
    private static final UUID Myuuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private BluetoothDevice mDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    public BluetoothConnection(Context context, BluetoothAdapter bluetoothAdapter) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread{
        private  final BluetoothServerSocket mServerSocket;
        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, Myuuid);
                Log.d(TAG,"AcceptThread: Setting up server using: "+ Myuuid);
            }catch (IOException e){}
            mServerSocket = tmp;

        }

        public void run(){
            Log.d(TAG, "run: AcceptThread running");
            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: start....");

                // waot until connection is made
                socket = mServerSocket.accept();
                Log.d(TAG, "accepted connection");
            }catch (IOException e){

            }

            if(socket!= null){
                connected(socket, mDevice);
            }

            Log.i(TAG,"END the acceptThread");

        }

        public void cancel(){
            try{
                mServerSocket.close();
            }catch (IOException e){}
        }
    }

    private class  ConnectThread extends Thread{
        private BluetoothSocket mSocket;
        public  ConnectThread (BluetoothDevice device, UUID uuid){
            Log.d(TAG, "ConnectThread: started");
            mDevice = device;
            deviceUUID = uuid;
        }
        
        public void run(){
            BluetoothSocket tmp = null;
            // Get a bluetooth socket for a connectin with the given BluetoothDevice
            try{
                tmp = mDevice.createInsecureRfcommSocketToServiceRecord(deviceUUID);
            }catch (IOException e){}
            mSocket = tmp;

            // always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // make a connection to the bleutooth socket
            // this is a blocking call and will only return a successful connection or an exception
            try {
                mSocket.connect();

            }catch (IOException e){
                try{
                    mSocket.close();
                }catch (IOException el){}
            }
            connected(mSocket, mDevice);
        }

        public void cancel(){
            try{
                mSocket.close();
            }catch (IOException e){

            }
        }

    }

    // Start the chat service Specially start AcceptThread to begin a session in listening (server) mode. Called by the Activity onResume()
    public synchronized void start(){

        // cancel any thread attempting to make a connection
        if (mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if(mInsecureAcceptThread == null){
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }


    // AcceptThread starts and sits waiting for a connection
    // Then ConnectThread starts and attempts to make a connection with the other device AcceptThread
    public void startClient(BluetoothDevice device, UUID uuid){
        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth", "Please wait...", true );

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }
}
