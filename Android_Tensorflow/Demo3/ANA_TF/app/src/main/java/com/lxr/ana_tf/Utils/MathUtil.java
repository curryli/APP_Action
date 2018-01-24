package com.lxr.ana_tf.Utils;

import java.util.List;

public class MathUtil {
    public static float[] toFloatArray(List<Float> list)
    {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    public static float getMean(float[] arr){
        float Sum = 0;
        for(int i=0; i<arr.length; i++){
            Sum = Sum+arr[i];
        }
        return Sum/arr.length;
    }

    public static float getMax(float[] arr){
        float Max = arr[0];
        for(int i=1; i<arr.length; i++){
            if(arr[i] > Max){
                Max = arr[i];
            }
        }
        return Max;
    }

    public static float getMin(float[] arr){
        float Min = arr[0];
        for(int i=1; i<arr.length; i++){
            if(arr[i] < Min){
                Min = arr[i];
            }
        }
        return Min;
    }


    public static float getStd(float[] arr){
        float Sum = 0;
        for(int i = 0;i < arr.length;i++){
            Sum += Math.sqrt((arr[i] -getMean(arr)) * (arr[i] -getMean(arr)));
        }
        return (Sum / (arr.length - 1));
    }
}
