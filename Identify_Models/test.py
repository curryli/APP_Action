# -*- coding:utf-8 -*-
import pandas as pd
import numpy as np
from scipy import stats
import matplotlib.pyplot as plt
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, roc_curve, auc


# 加载数据集
def read_data(file_path):
    column_names = ['user-id', 'activity', 'timestamp', 'x-axis', 'y-axis', 'z-axis']
    data = pd.read_csv(file_path, header=None, names=column_names)
    return data

# 数据标准化
def feature_normalize(dataset):
    mu = np.mean(dataset, axis=0)
    sigma = np.std(dataset, axis=0)
    return (dataset - mu) / sigma

# 创建时间窗口，90 × 50ms，也就是 4.5 秒，每次前进 45 条记录，半重叠的方式。
def windows(data, size):
    start = 0
    while start < data.count():
        yield start, start + size
        start += int(size / 2)

# 创建输入数据，每一组数据包含 x, y, z 三个轴的 90 条连续记录，
# 用 `stats.mode` 方法获取这 90 条记录中出现次数最多的行为
# 作为该组行为的标签，这里有待商榷，其实可以完全使用同一种行为的数据记录
# 来创建一组数据用于输入的。
def segment_signal(data, window_size=90):
    segments = np.empty((0, window_size, 3))
    labels = np.empty((0))
#    print len(data['timestamp'])  #1098204
    count = 0
    for (start, end) in windows(data['timestamp'], window_size):
        if count%100==0:
            print(count)
            # print(start, end)
        count += 1
        x = data["x-axis"][start:end]
        y = data["y-axis"][start:end]
        z = data["z-axis"][start:end]
        if (len(dataset['timestamp'][start:end]) == window_size):
            segments = np.vstack([segments, np.dstack([x, y, z])])
            labels = np.append(labels, stats.mode(data["activity"][start:end])[0][0])
    return segments, labels




if __name__ == "__main__":
    dataset = read_data("data/WISDM_ar_v1.1_modify.txt")
    dataset['x-axis'] = feature_normalize(dataset['x-axis'])
    dataset['y-axis'] = feature_normalize(dataset['y-axis'])
    dataset['z-axis'] = feature_normalize(dataset['z-axis'])
    print(dataset)
    segments, labels = segment_signal(dataset)
    print(segments.shape, labels.shape)
    #(24403L, 90L, 3L) (24403L,)
    labels = np.asarray(pd.get_dummies(labels), dtype = np.int8)
    # 创建输入
    reshaped_segments = segments.reshape(len(segments), 1,90, 3)

    # 在准备好的输入数据中，分别抽取训练数据和测试数据，按照 70/30 原则来做。
    train_test_split = np.random.rand(len(reshaped_segments)) < 0.70
    train_x = reshaped_segments[train_test_split]
    train_y = labels[train_test_split]
    test_x = reshaped_segments[~train_test_split]
    test_y = labels[~train_test_split]

    # 定义输入数据的维度和标签个数
    input_height = 1
    input_width = 90
    num_labels = 6
    num_channels = 3

    batch_size = 10
    kernel_size = 60
    depth = 60

    # 隐藏层神经元个数
    num_hidden = 1000

    learning_rate = 0.0001

    # 降低 cost 的迭代次数
    training_epochs = 8

    total_batchs = reshaped_segments.shape[0] // batch_size
