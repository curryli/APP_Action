# -*- coding: utf-8 -*- 
import tensorflow as tf
from sklearn.datasets import load_iris
import numpy as np
import pandas as pd
 

iris=load_iris()
iris_data=iris.data
iris_target=iris.target

iris_target1=pd.get_dummies(iris_target).values
 
X=iris_data
 
print(X[0],X[50],X[100])
print(iris_target1[0],iris_target1[50],iris_target1[100])

#[5.1 3.5 1.4 0.2] [7.  3.2 4.7 1.4] [6.3 3.3 6.  2.5]
#[1 0 0] [0 1 0] [0 0 1]

x=tf.placeholder(dtype=tf.float32,shape=[None,4],name="I")
y=tf.placeholder(dtype=tf.float32,shape=[None,3])  #三分类

w=tf.get_variable("weight",shape=[4,3],dtype=tf.float32,initializer=tf.truncated_normal_initializer(stddev=0.1))
bais=tf.get_variable("bais",shape=[3],dtype=tf.float32,initializer=tf.constant_initializer(0))
y_out=tf.nn.bias_add(tf.matmul(x,w),bais,name="O")

loss=tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y,logits=y_out))
 
accuracy=tf.reduce_mean(tf.cast(tf.equal(tf.arg_max(y,1),tf.arg_max(y_out,1)),tf.float32))
train_step=tf.train.AdamOptimizer().minimize(loss)

saver = tf.train.Saver()

init_op = tf.global_variables_initializer()

with tf.Session() as sess:
    sess.run(init_op)
    # save the graph
    tf.train.write_graph(sess.graph_def, '.', 'tfdroid.pbtxt')
  
  
    for i in range(3001):
        sess.run(train_step,feed_dict={x:X,y:iris_target1})
        if i%500==0:
            accuracy_print=sess.run(accuracy,feed_dict={x:X,y:iris_target1})
            print(accuracy_print)
            
    saver.save(sess, './tfdroid.ckpt')
    
#运行上面的代码会把模型的计算图保存在tfdroid.pbtxt文件中，同时把模型变量的checkpoint保存在tfdroid.ckpt中。

 