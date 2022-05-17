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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
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

    private ConnectedThread mConnectedThread;
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

    public class ConnectedThread extends Thread{
        private final BluetoothSocket mSocket;
        private final InputStream mInputstream;
        private final OutputStream moutputStream;


        public ConnectedThread(BluetoothSocket socket) {
            mSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            // dismiss the progress dialog box when connection is established
            mProgressDialog.dismiss();
            try {
                tempIn = mSocket.getInputStream();
                tempOut = mSocket.getOutputStream();
            } catch (IOException e){}
            mInputstream = tempIn;
            moutputStream = tempOut;
        }

        public void run(){
            byte[] buffer = new byte[1024]; // buffer store for the stream
            int bytes;
            while(true){
                try{
                    bytes = mInputstream.read(buffer);
                    String incommingMsg = new String(buffer, 0 , bytes);
                }catch (IOException e){}
                    break;
            }
        }

        // Call this from the main activity to sned data to the remote device
        public void write(byte[] bytes){
            try {
                String text = new String (bytes, Charset.defaultCharset());
                moutputStream.write(bytes);
            }catch (IOException e){

            }

        }
        // call this from the main activity to shutdown the connection
        public void cancel(){
            try{
                mSocket.close();
            }catch(IOException w){}
        }
    }

    private void connected(BluetoothSocket mSocket, BluetoothDevice mDevice) {
        // start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mSocket);
        mConnectedThread.start();
    }

    // write to the connected thread
    public  void  write(byte[] out){
        ConnectedThread r;
        mConnectedThread.write(out);
    }
}
