# -*- coding:utf-8 -*-
import pandas as pd
import numpy as np
from scipy import stats
import matplotlib.pyplot as plt
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, roc_curve, auc
import tensorflow as tf
from reshape_seq import Reshape_seq,Reshape_semi_overlap
from sklearn.preprocessing import StandardScaler
#from __future__ import division


# 数据标准化
def feature_normalize(dataset):
    mu = np.mean(dataset, axis=0)
    sigma = np.std(dataset, axis=0)
    return (dataset - mu) / sigma


# weight initialization
def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)


def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)


# convolution
def conv2d(x, W):
    return tf.nn.conv2d(x, W, strides=[1, 1, 1, 1], padding='SAME')


# pooling
def max_pool_1x2(x):
    return tf.nn.max_pool(x, ksize=[1, 1, 2, 1], strides=[1, 1, 1, 1], padding='SAME')


if __name__ == "__main__":
    training_epochs = 200
    wz = 100  #每一百条计数组成一个轨迹片段
    batch_size = 50

    column_names = ['x-axis','y-axis','z-axis','label']
    df_all = pd.read_csv("labeled_motion.csv", header=None, names=column_names)
    df_all = df_all.fillna(-1)  #loss 出现NAN，先检查原始数据是不是有问题

#    sc = StandardScaler()
#    df_X = df_all[['x-axis', 'y-axis', 'z-axis']]
#    df_X = sc.fit_transform(df_X)

    df_all['x-axis'] = feature_normalize(df_all['x-axis'])
    df_all['y-axis'] = feature_normalize(df_all['y-axis'])
    df_all['z-axis'] = feature_normalize(df_all['z-axis'])

    df_all = df_all[['x-axis', 'y-axis', 'z-axis', 'label']]

    df_all = df_all[df_all['label'].isin(["Static","StandingBy","Jogging","Walking"])]


    segments, labels = Reshape_seq(df_all,wz)
    print(segments.shape, labels.shape)
    labels_ori = labels

    labels_dummy =pd.get_dummies(labels)
    print("labels_dummy")
    print(labels_dummy.drop_duplicates(inplace=False))

    labels = np.asarray(labels_dummy, dtype = np.int8)


    
    print(labels)
    # 创建输入
    reshaped_segments = segments.reshape(len(segments), 1, wz, 3)

    # 在准备好的输入数据中，分别抽取训练数据和测试数据，按照 70/30 原则来做。
    train_test_split = np.random.rand(len(reshaped_segments)) < 0.70
    train_x = reshaped_segments[train_test_split]
    train_y = labels[train_test_split]
    test_x = reshaped_segments[~train_test_split]
    test_y = labels[~train_test_split]

    # 定义输入数据的维度和标签个数
    input_height = 1
    input_width = wz
    num_labels = 4  #6类， 输出6维的onehot
    num_channels = 3  # x, y ,z 每个轨迹看做一个1*wz 的图像，一共三个channel

    learning_rate = 1e-4   #如果loss出现NAN，或者accuracy固定在一个很小的位置，很可能上来学习率就设的太大了，导致到不了最小点。   这时候尝试一个量级一个量级地降学习率试试


    total_batchs = reshaped_segments.shape[0]//batch_size

    # 下面是使用 Tensorflow 创建神经网络的过程。
    X = tf.placeholder(tf.float32, shape=[None,input_height,input_width,num_channels],name="I")
    Y = tf.placeholder(tf.float32, shape=[None,num_labels])

    # [filter_height, filter_width, in_channels, out_channels]  [卷积核的高，卷积核的宽，输入通道数，输出通道数]  一般卷积核3*3 5*5 9*9...  我们这里是1*100 的图像，所以f_h只能是1
    f_h_1 = 1
    f_w_1 = 9  # 自定义
    in_chan_1 = num_channels
    out_chan_1 = 64  # 自定义，深度

    x_Arr = tf.reshape(X, [-1, input_height,input_width,num_channels])

    # first convolutinal layer
    w_conv1 = weight_variable([f_h_1, f_w_1, in_chan_1, out_chan_1])
    b_conv1 = bias_variable([out_chan_1])


    h_conv1 = tf.nn.relu(conv2d(x_Arr, w_conv1) + b_conv1)
    h_pool1 = max_pool_1x2(h_conv1)


    f_h_2 = 1
    f_w_2 = 5  #自定义,比f_w_1小一点
    in_chan_2 = out_chan_1  #上一层输出chann数作为当前输入chann数
    out_chan_2 = 16 #自定义，深度


    # second convolutional layer
    w_conv2 = weight_variable([f_h_2, f_w_2, in_chan_2, out_chan_2])
    b_conv2 = bias_variable([out_chan_2])

    h_conv2 = tf.nn.relu(conv2d(h_pool1, w_conv2) + b_conv2)
    h_pool2 = max_pool_1x2(h_conv2)

    shape = h_pool2.get_shape().as_list()
    print(shape[1],shape[2],shape[3])  #经过两次max_pool_1x2      原来是1*100     现在1*（100/4）=25  但是有out_chan_2个 chann， 所以（1*25*16）


    num_out_1 = 1024  #自定义，第一层flattern输出维度   （输入维度就是shape[1] * shape[2] * shape[3]展开）
    # densely connected layer
    w_fc1 = weight_variable([shape[1] * shape[2] * shape[3], num_out_1])
    b_fc1 = bias_variable([num_out_1])

    h_pool2_flat = tf.reshape(h_pool2, [-1, shape[1] * shape[2] * shape[3]])
    h_fc1 = tf.nn.tanh(tf.matmul(h_pool2_flat, w_fc1) + b_fc1)

    # dropout
    h_fc1_drop = tf.nn.dropout(h_fc1, 0.5)  #保存0.5

    # readout layer
    w_fc2 = weight_variable([num_out_1, num_labels])   #第二层输出维度与类别数一致
    b_fc2 = bias_variable([num_labels])

    ylogits = tf.nn.bias_add(tf.matmul(h_fc1_drop, w_fc2) ,b_fc2 ,name="O")
    
 
    loss = tf.reduce_sum(tf.nn.softmax_cross_entropy_with_logits(labels=Y, logits=ylogits))
     
    optimizer = tf.train.AdamOptimizer(learning_rate = learning_rate).minimize(loss)


    y_ = tf.nn.softmax(ylogits)
    correct_prediction = tf.equal(tf.argmax(y_,1), tf.argmax(Y,1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

    saver = tf.train.Saver()
    # 开始训练
    with tf.Session() as sess:
        tf.global_variables_initializer().run()

        # save the graph
        tf.train.write_graph(sess.graph_def, '.', 'tfdroid.pbtxt')


        # 开始迭代
        for epoch in range(training_epochs):
            for b in range(total_batchs):
                offset = (b * batch_size) % (train_y.shape[0] - batch_size)
                batch_x = train_x[offset:(offset + batch_size), :, :, :]
                #print(batch_x[0])
                batch_y = train_y[offset:(offset + batch_size), :]
                _, c , a = sess.run([optimizer, loss,ylogits], feed_dict={X: batch_x, Y: batch_y})
                #print(a)
            print("Epoch {}: Training Loss = {}, Training Accuracy = {}".format(epoch, c, sess.run(accuracy, feed_dict={X: train_x, Y: train_y})))

        saver.save(sess, './tfdroid.ckpt')

        y_p = tf.argmax(y_, 1)
        y_true = np.argmax(test_y, 1)
        final_acc, y_pred = sess.run([accuracy, y_p], feed_dict={X: test_x, Y: test_y})
        print("Testing Accuracy: {}".format(final_acc))
        # 计算模型的 metrics
        print("Precision", precision_score(y_true.tolist(), y_pred.tolist(), average='weighted'))
        print("Recall", recall_score(y_true, y_pred, average='weighted'))
        print("f1_score", f1_score(y_true, y_pred, average='weighted'))
        print("confusion_matrix")
        print(confusion_matrix(y_true, y_pred))





# Testing Accuracy: 0.9863013625144958
# Precision 0.9863315551457674
# Recall 0.9863013698630136
# f1_score 0.9862913779048171
# confusion_matrix
# [[51  0  0  0]
#  [ 0 95  1  1]
#  [ 0  1 64  0]
#  [ 1  0  0 78]]



# labels_dummy
#      Jogging  StandingBy  Static  Walking
#          1           0       0        0
#          0           0       0        1
#          0           0       1        0
#         0           1       0        0