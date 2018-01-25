# -*- coding: utf-8 -*- 
import tensorflow as tf
from sklearn.datasets import load_iris
import numpy as np
import pandas as pd
from sklearn.cross_validation import train_test_split
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, roc_curve, auc


# weight initialization
def weight_variable(shape):
    initial = tf.truncated_normal(shape, stddev=0.1)
    return tf.Variable(initial)


def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)


Feat_size = 4  #"mean", "max", "min", "std"
first_num =256
second_num =128
third_num =64

Cat_num = 4
training_epochs=500
batch_size = 20
learning_rate = 1e-3   #如果loss出现NAN，或者accuracy固定在一个很小的位置，很可能上来学习率就设的太大了，导致到不了最小点。   这时候尝试一个量级一个量级地降学习率试试


df_All = pd.read_csv("motion_FE.csv", sep=',')
df_All = df_All.fillna(-1)

df_All = shuffle(df_All)


df_X = df_All.drop(["label"], axis=1,inplace=False)

df_y = df_All["label"]
print("labels_ori")
print(df_y.drop_duplicates(inplace=False))

df_y = pd.get_dummies(df_y)

print("labels_dummy")
print(df_y.drop_duplicates(inplace=False))

train_x, test_x, train_y, test_y = train_test_split(df_X, df_y, test_size=0.2)

X=tf.placeholder(dtype=tf.float32,shape=[None,Feat_size],name="I")
Y=tf.placeholder(dtype=tf.float32,shape=[None,Cat_num])  #4分类


w_fc1 = weight_variable([Feat_size,first_num])
b_fc1 = bias_variable([first_num])
h_fc1 = tf.nn.tanh(tf.matmul(X, w_fc1) + b_fc1)
h_fc1_drop = tf.nn.dropout(h_fc1, 0.5)  #保存0.5

w_fc2 = weight_variable([first_num, second_num])   #第二层输出维度与类别数一致
b_fc2 = bias_variable([second_num])
h_fc2 = tf.nn.tanh(tf.matmul(h_fc1_drop, w_fc2) + b_fc2)
h_fc2_drop = tf.nn.dropout(h_fc2, 0.5)  #保存0.5

w_fc3 = weight_variable([second_num, third_num])   #第二层输出维度与类别数一致
b_fc3 = bias_variable([third_num])
h_fc3 = tf.nn.tanh(tf.matmul(h_fc2_drop, w_fc3) + b_fc3)
h_fc3_drop = tf.nn.dropout(h_fc3, 0.5)  #保存0.5

w_fc4 = weight_variable([third_num, Cat_num])   #第二层输出维度与类别数一致
b_fc4 = bias_variable([Cat_num])
ylogits = tf.nn.bias_add(tf.matmul(h_fc3_drop, w_fc4) ,b_fc4 ,name="O")



loss = tf.reduce_sum(tf.nn.softmax_cross_entropy_with_logits(labels=Y, logits=ylogits))

optimizer = tf.train.AdamOptimizer(learning_rate=learning_rate).minimize(loss)

y_ = tf.nn.softmax(ylogits)
correct_prediction = tf.equal(tf.argmax(y_, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))



total_batchs = train_x.shape[0]//batch_size


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
            batch_x = train_x[offset:(offset + batch_size)]
            # print(batch_x[0])
            batch_y = train_y[offset:(offset + batch_size)]
            _, c, a = sess.run([optimizer, loss, ylogits], feed_dict={X: batch_x, Y: batch_y})
            # print(a)
        print("Epoch {}: Training Loss = {}, Training Accuracy = {}".format(epoch, c, sess.run(accuracy,
                                                                                               feed_dict={X: train_x,
                                                                                                          Y: train_y})))

    saver.save(sess, './tfdroid.ckpt')

    y_p = tf.argmax(y_, 1)

    final_acc, y_pred = sess.run([accuracy, y_p], feed_dict={X: test_x, Y: test_y})

    y_true = np.argmax(test_y.values, axis=1)
    print("Testing Accuracy: {}".format(final_acc))
    # 计算模型的 metrics
    print("Precision", precision_score(y_true.tolist(), y_pred.tolist(), average='weighted'))
    print("Recall", recall_score(y_true, y_pred, average='weighted'))
    print("f1_score", f1_score(y_true, y_pred, average='weighted'))
    print("confusion_matrix")
    print(confusion_matrix(y_true, y_pred))


# labels_ori
# 824    1 "StandingBy"
# 74     0  "Jogging"
# 366    3  "Walking"
# 704    2  "Static"
#
# labels_dummy
#      0  1  2  3
# 824  0  1  0  0
# 74   1  0  0  0
# 366  0  0  0  1
# 704  0  0  1  0