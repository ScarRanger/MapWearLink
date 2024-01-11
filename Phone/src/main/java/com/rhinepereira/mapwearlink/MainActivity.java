package com.rhinepereira.mapwearlink;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "tagula";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String textData = intent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(TAG, "onCreate: "+ textData);
        sendMessage(textData);
    }

    private void sendMessage(String textData) {
        // Create a new thread to avoid blocking the UI thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Get the connected nodes on the Wear network
                Task<List<com.google.android.gms.wearable.Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
                try {
                    List<Node> nodes = Tasks.await(nodeListTask);
                    for (Node node : nodes) {
                        // Build the message
  //                      String message = "https://www.google.com/maps/place/Pita+Smruti/@19.3683735,72.7939001,17z/data=!3m1!4b1!4m6!3m5!1s0x3be7ad6c8b3d894d:0x9852f8f9eb26b39d!8m2!3d19.3683685!4d72.796475!16s%2Fg%2F11vbq0jrty?entry=ttu";
                        byte[] payload = textData.getBytes();

                        Task<Integer> sendMessageTask =
                                Wearable.getMessageClient(getApplicationContext()).sendMessage(node.getId(), "/message", payload);

                        sendMessageTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                            @Override
                            public void onComplete(@NonNull Task<Integer> task) {
                                if (task.isSuccessful()) {
                                    int result = task.getResult();
                                    Log.d("from android", "Message sent to " + node.getDisplayName() + ". Result: " + result + ". message: " + textData);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Exception exception = task.getException();
                                    Log.e("from android", "Failed to send message: " + exception);
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, "Toast Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                } catch (ExecutionException exception) {
                    Log.e("from android", "Failed to send message: " + exception);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Toast 메시지 내용", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (InterruptedException exception) {
                    Log.e("from android", "Failed to send message: " + exception);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Toast 메시지 내용", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}