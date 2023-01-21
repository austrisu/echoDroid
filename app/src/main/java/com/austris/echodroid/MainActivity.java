package com.austris.echodroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SoundDataTransfer soundDataTransfer;
    private EditText textInput;
    private Button sendButton;
    private AudioRecord audioRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO},
                    123);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    123);

        }



        soundDataTransfer = new SoundDataTransfer();

        textInput = findViewById(R.id.text_input);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> {
            String text = textInput.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text to send", Toast.LENGTH_SHORT).show();
            } else {
                soundDataTransfer.transmitData(text);
                Toast.makeText(this, "Data sent", Toast.LENGTH_SHORT).show();
            }
        });

        // Add a button for starting the data receiving
        Button receiveButton = (Button) findViewById(R.id.recieve_button);
        receiveButton.setOnClickListener(v -> {
//            @Override
//            public void onClick(v) {
                // Start receiving the data
                String receivedData = soundDataTransfer.receiveData();
                Toast.makeText(MainActivity.this, "Received Data: " + receivedData, Toast.LENGTH_LONG).show();
//            }
        });
    }
}