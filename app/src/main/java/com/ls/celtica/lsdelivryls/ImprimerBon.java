package com.ls.celtica.lsdelivryls;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class ImprimerBon extends AppCompatActivity {

    public static BluetoothAdapter bluetoothAdapter;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothDevice bluetoothDevice;

    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static Thread thread;

    public static byte[] readBuffer;
    public static int readBufferPosition;
    public static volatile boolean stopWorker;
    public static String msg;
    public static AppCompatActivity c;

    static ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprimer_bon);

    }

    public static void FindBluetoothDevice(AppCompatActivity cc){
        boolean attach=false;

        try {
            progress = new ProgressDialog(c); // activité non context ..

            progress.setTitle("Connexion");
            progress.setMessage("Attendez SVP...");
            progress.show();

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Log.e("bbb", "No Bluetooth Adapter found");
            }
            if (bluetoothAdapter.isEnabled()) {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                c.startActivityForResult(enableBT, 0);
            }

            Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();

            if (pairedDevice.size() > 0) {
                for (BluetoothDevice pairedDev : pairedDevice) {
                    Log.e("device", pairedDev.getName());
                    String name = null;

                    try {
                        Method m = pairedDev.getClass().getMethod("getAlias");
                        Object res = m.invoke(pairedDev);
                        if (res != null) {
                            name = res.toString();
                        }
                        Log.e("bbb", "Name set By user: " + name);
                        // My Bluetoth printer name is BTP_F09F1A
                        if (name != null) {
                            if (name.equals(Login.user.nom_printer)) {
                                attach = true;
                                bluetoothDevice = pairedDev;
                                Log.e("bbb", "Bluetooth Printer Attached: " + pairedDev.getName());
                                openBluetoothPrinter();
                                break;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (!attach) {
                    progress.dismiss();
                    Toast.makeText(c, "Veuillez attacher votre imprimante ..", Toast.LENGTH_SHORT).show();
                }
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static void openBluetoothPrinter() throws IOException {
        try{


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Standard uuid from string //
                        UUID uuidSting = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                        bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuidSting);
                        bluetoothSocket.connect();
                        if(bluetoothSocket != null) {

                            outputStream = bluetoothSocket.getOutputStream();
                            inputStream = bluetoothSocket.getInputStream();
                            Log.e("sss"," Djaaaz");
                            beginListenData();
                            printData();
                        }

                    } catch (IOException e) {
                        Log.e("sss","Open: "+e.getMessage());
                        e.printStackTrace();
                    }

                }
            }).start();


        }catch (Exception ex){

        }
    }

    public static void beginListenData(){
        try{

            final Handler handler =new Handler();
            final byte delimiter=10;
            stopWorker =false;
            readBufferPosition=0;
            readBuffer = new byte[1024];

            thread=new Thread(new Runnable() {
                @Override
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker){
                        try{
                            int byteAvailable = inputStream.available();
                            if(byteAvailable>0){
                                byte[] packetByte = new byte[byteAvailable];
                                inputStream.read(packetByte);

                                for(int i=0; i<byteAvailable; i++){
                                    byte b = packetByte[i];
                                    if(b==delimiter){
                                        byte[] encodedByte = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer,0,
                                                encodedByte,0,
                                                encodedByte.length
                                        );
                                        final String data = new String(encodedByte,"US-ASCII");
                                        readBufferPosition=0;
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.e("bbb","data: "+data);

                                            }
                                        });
                                    }else{
                                        readBuffer[readBufferPosition++]=b;
                                    }
                                }
                            }
                        }catch(Exception ex){
                            Log.e("sss"," Worker: "+ex.getMessage());
                            stopWorker=true;
                        }
                    }

                }
            });

            thread.start();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void printData() throws  IOException{
        try{

            c.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(c,"L'impression est commencer ..",Toast.LENGTH_SHORT).show();
                }
            });
            outputStream.write(msg.getBytes());
            Log.e("sss"," Djaaaz");
            disconnectBT();

            c.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progress.dismiss();
                }
            });
            //lblPrinterName.setText("Printing Text...");
        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("sss","Mahich mliha"+ ex.getMessage());
        }
    }

    // Disconnect Printer //
    static void disconnectBT() throws IOException{
        try {
            stopWorker=true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            // lblPrinterName.setText("Printer Disconnected.");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
