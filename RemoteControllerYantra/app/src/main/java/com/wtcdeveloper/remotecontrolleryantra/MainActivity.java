package com.wtcdeveloper.remotecontrolleryantra;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private Button btnPackage;
    private RadioGroup radioGroup;
    private RadioButton radioGeneric;
    private ToggleButton tgBtnAutoManu;
    private Button btnSendDirection;
    private Button btnBrake;
    private EditText etDirections;
    private TextView angleTextView;
    private TextView powerTextView;
    private TextView directionTextView;
    private JoystickView joystick;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    //private static String address ="00:14:01:03:59:6B";//my bluetooth
    private static String address ="30:14:11:21:27:81";
    private static final String TAG = "BlueToothComm";
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        initComponents();
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


    //main java program
    void initComponents(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();
        btnPackage=(Button)findViewById(R.id.btnPackage);
        etDirections = (EditText)findViewById(R.id.etDirections);
        btnSendDirection=(Button)findViewById(R.id.btnSendDirection);
        angleTextView = (TextView)findViewById(R.id.angleTextView);
        powerTextView = (TextView)findViewById(R.id.powerTextView);
        directionTextView=(TextView)findViewById(R.id.directionTextView);
        joystick = (JoystickView)findViewById(R.id.joyStickView);
        tgBtnAutoManu =(ToggleButton) findViewById(R.id.tgBtnAutoManu);
        btnBrake =(Button)findViewById(R.id.btnBrake);
        btnBrake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.btnBrake:
                        sendData("b"); //brake arduino char s
                        break;
                    default:
                        break;
                }
            }
        });

        radioGroup=(RadioGroup)findViewById(R.id.radioSpeedSelector);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){
                    case R.id.radioButton:
                        sendData("h");
                        break;
                    case R.id.radioButton2:
                        sendData("j");
                        break;
                    case R.id.radioButton3:
                        sendData("k");
                        break;
                    case R.id.radioButton4:
                        sendData("l");
                        break;
                    case R.id.radioButton5:
                        sendData("n");
                        break;
                }
            }
        });

        tgBtnAutoManu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String switchButton = tgBtnAutoManu.getText().toString();
                switch (switchButton){
                    case "Automatic":
                        sendData("i");
                        Toast.makeText(context,switchButton,Toast.LENGTH_LONG).show();
                        break;
                    case "Manual":
                        sendData("o");
                        Toast.makeText(context,switchButton,Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }


            }
        });
        //package delivery button
        btnPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("d");
                Toast.makeText(context,"Package delivered",Toast.LENGTH_LONG).show();
            }
        });
        //autonomous direction send button
        btnSendDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnSendDirection:
                        String direction =etDirections.getText().toString();
                        direction.toUpperCase();
                        sendData(direction);
                        break;
                    default:
                        break;
                }

            }
        });
        joystick.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                angleTextView.setText(" " + String.valueOf(angle) + "°");
                powerTextView.setText(" " + String.valueOf(power) + "%");
                //String angPow = "A" + String.valueOf(angle) + "E" + "P" + String.valueOf(power) + "R";
                //sendData(String.valueOf(angPow));

                switch (direction) {
                    case JoystickView.FRONT:
                        directionTextView.setText(R.string.front_lab);
                        break;
                    case JoystickView.FRONT_RIGHT:
                        directionTextView.setText(R.string.front_right_lab);
                        break;
                    case JoystickView.RIGHT:
                        directionTextView.setText(R.string.right_lab);
                        break;
                    case JoystickView.RIGHT_BOTTOM:
                        directionTextView.setText(R.string.right_bottom_lab);
                        break;
                    case JoystickView.BOTTOM:
                        directionTextView.setText(R.string.bottom_lab);
                        break;
                    case JoystickView.BOTTOM_LEFT:
                        directionTextView.setText(R.string.bottom_left_lab);
                        break;
                    case JoystickView.LEFT:
                        directionTextView.setText(R.string.left_lab);
                        break;
                    case JoystickView.LEFT_FRONT:
                        directionTextView.setText(R.string.left_front_lab);
                        break;
                    default:
                        directionTextView.setText(R.string.center_lab);
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }

    //Bluetooth components
    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...In onResume - Attempting client connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting to Remote...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection established and data link opened...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Creating Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "...onPause()...");
        if(outStream !=null){
            try{
                outStream.flush();
            }catch (IOException e){
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }
        try{
            btSocket.close();
        }catch (IOException e2){
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }


    private void checkBTState(){
        // Check for Bluetooth support and then check to make sure it is turned on

        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth is enabled...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast msg = Toast.makeText(getBaseContext(),
                title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        finish();
    }

    private void sendData(String message){
        byte[] msgBuffer = message.getBytes();
        Log.d(TAG, "...Sending data:" + message + "...");
        try{
            outStream.write(msgBuffer);

        }
        catch (IOException e){
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00")) msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
            errorExit("Fatal Error", msg);
        }
    }
}
