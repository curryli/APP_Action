/*
Created by Omid Alemi
Feb 17, 2017
 */

package al.omid.tfdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import java.util.Arrays;
import java.util.Collections;


import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {

    public static int getMaxIndex(float[] arr){
        int maxIndex = 0;   //获取到的最大值的角标
        for(int i=0; i<arr.length; i++){
            if(arr[i] > arr[maxIndex]){
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    private static final String MODEL_FILE = "file:///android_asset/optimized_tfdroid.pb";
    private static final String INPUT_NODE = "I:0";
    private static final String OUTPUT_NODE = "O:0";
    private static final int[] INPUT_SIZE = {1,4};

    private TensorFlowInferenceInterface inferenceInterface;

    static {
        System.loadLibrary("tensorflow_inference");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inferenceInterface = new TensorFlowInferenceInterface();
        inferenceInterface.initializeTensorFlow(getAssets(), MODEL_FILE);


        final Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                final EditText editNum1 = (EditText) findViewById(R.id.editNum1);
                final EditText editNum2 = (EditText) findViewById(R.id.editNum2);
                final EditText editNum3 = (EditText) findViewById(R.id.editNum3);
                final EditText editNum4 = (EditText) findViewById(R.id.editNum4);

                float num1 = Float.parseFloat(editNum1.getText().toString());
                float num2 = Float.parseFloat(editNum2.getText().toString());
                float num3 = Float.parseFloat(editNum3.getText().toString());
                float num4 = Float.parseFloat(editNum4.getText().toString());

                float[] inputFloats = {num1, num2, num3, num4};

                inferenceInterface.fillNodeFloat(INPUT_NODE, INPUT_SIZE, inputFloats);

                inferenceInterface.runInference(new String[] {OUTPUT_NODE});

                float[] resu = {0,0,0};
                inferenceInterface.readNodeFloat(OUTPUT_NODE, resu);

                final TextView textViewR = (TextView) findViewById(R.id.txtViewResult);
                //textViewR.setText(Float.toString(resu[0]) + " " + Float.toString(resu[1])+ " " +Float.toString(resu[2]) );
                textViewR.setText(Integer.toString(getMaxIndex(resu)));
            }
        });

    }




}
