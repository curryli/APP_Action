# -*- coding: utf-8 -*- 
import tensorflow as tf
from sklearn.datasets import load_iris
import numpy as np
import pandas as pd
 

iris=load_iris()
iris_data=iris.data
iris_target=iris.target

iris_target1=pd.get_dummies(iris_target).values
print(iris_data.shape)
 
X=iris_data
print(X.shape)

x=tf.placeholder(dtype=tf.float32,shape=[None,3],name="input")
y=tf.placeholder(dtype=tf.float32,shape=[None,4],name="output")  #4分类

w=tf.get_variable("weight",shape=[4,3],dtype=tf.float32,initializer=tf.truncated_normal_initializer(stddev=0.1))
bais=tf.get_variable("bais",shape=[3],dtype=tf.float32,initializer=tf.constant_initializer(0))
y_out=tf.nn.bias_add(tf.matmul(x,w),bais)

loss=tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y,logits=y_out))
 
accuracy=tf.reduce_mean(tf.cast(tf.equal(tf.arg_max(y,1),tf.arg_max(y_out,1)),tf.float32))
train_step=tf.train.AdamOptimizer().minimize(loss)


with tf.Session() as sess:
    sess.run(tf.global_variables_initializer())
    for i in range(3001):
        sess.run(train_step,feed_dict={x:X,y:iris_target1})
        if i%500==0:
            accuracy_print=sess.run(accuracy,feed_dict={x:X,y:iris_target1})
            print(accuracy_print)

 