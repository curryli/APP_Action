# -*- coding: utf-8 -*- 
# Preparing a TF model for usage in Android
#http://blog.csdn.net/nicholas_wong/article/details/76509763
#http://blog.csdn.net/lujiandong1/article/details/53385092

import sys
import tensorflow as tf
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib


MODEL_NAME = 'tfdroid'

# Freeze the graph

input_graph_path = MODEL_NAME+'.pbtxt'
checkpoint_path = './'+MODEL_NAME+'.ckpt'
input_saver_def_path = ""
input_binary = False
output_node_names = "O"
restore_op_name = "save/restore_all"
filename_tensor_name = "save/Const:0"
output_frozen_graph_name = 'frozen_'+MODEL_NAME+'.pb'
output_optimized_graph_name = 'optimized_'+MODEL_NAME+'.pb'
clear_devices = True


freeze_graph.freeze_graph(input_graph_path, input_saver_def_path,
                          input_binary, checkpoint_path, output_node_names,
                          restore_op_name, filename_tensor_name,
                          output_frozen_graph_name, clear_devices, "")



# Optimize for inference

input_graph_def = tf.GraphDef()
with tf.gfile.Open(output_frozen_graph_name, "rb") as f:    #python3 用rb，python2 用 r
    data = f.read()
    input_graph_def.ParseFromString(data)

output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        input_graph_def,
        ["I"], # an array of the input node(s)
        ["O"], # an array of output nodes
        tf.float32.as_datatype_enum)

# Save the optimized graph

f = tf.gfile.FastGFile(output_optimized_graph_name, "wb")
f.write(output_graph_def.SerializeToString())

# tf.train.write_graph(output_graph_def, './', output_optimized_graph_name)  






#graph测试 

with tf.Graph().as_default():
    output_graph_def = tf.GraphDef()

    with open(output_optimized_graph_name, "rb") as f:
        output_graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(output_graph_def, name="")

    with tf.Session() as sess:
        init = tf.global_variables_initializer()
        sess.run(init)
        
        for op in sess.graph.get_operations():  
            print(op.name,op.values())  
        

        input_x = sess.graph.get_tensor_by_name("I:0")
        print(input_x)
        
        output_y = sess.graph.get_tensor_by_name("O:0")
        print(output_y)
        
        print("test1:")
        y_out = sess.run(output_y,feed_dict={input_x:[[5.1,3.5,1.4,0.2]]})
        print(y_out)
        print(sess.run(tf.nn.softmax(y_out)))
        
        print("test2:")
        y_out2 = sess.run(output_y,feed_dict={input_x:[[7,3.2,4.7,1.4]]})
        print(y_out2)
        print(sess.run(tf.nn.softmax(y_out2)))
        
        print("test3:")
        y_out3 = sess.run(output_y,feed_dict={input_x:[[6.3,3.3,6,2.5]]})
        print(y_out3)
        print(sess.run(tf.nn.softmax(y_out3)))

        
        

#I (<tf.Tensor 'I:0' shape=<unknown> dtype=float32>,)
#weight (<tf.Tensor 'weight:0' shape=(4, 3) dtype=float32>,)
#bais (<tf.Tensor 'bais:0' shape=(3,) dtype=float32>,)
#MatMul (<tf.Tensor 'MatMul:0' shape=(?, 3) dtype=float32>,)
#O (<tf.Tensor 'O:0' shape=(?, 3) dtype=float32>,)
#init ()
#Tensor("I:0", dtype=float32)
#Tensor("O:0", shape=(?, 3), dtype=float32)
#test1:
#[[ 4.4132867  1.1794835 -5.7147956]]
#[[9.6204972e-01 3.7911817e-02 3.8426184e-05]]
#test2:
#[[-2.4809046   0.78772527 -0.8168067 ]]
#[[0.0307161  0.807075   0.16220884]]
#test3:
#[[-5.625571  -0.8006677  2.3167386]]
#[[3.4020253e-04 4.2380523e-02 9.5727932e-01]]