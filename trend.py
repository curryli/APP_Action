# -*- coding:utf-8 -*-
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# 绘图使用 ggplot 的 style
plt.style.use('ggplot')

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

# 绘图
def plot_axis(ax, x, y, title):
    ax.plot(x, y)
    ax.set_title(title)
    ax.xaxis.set_visible(False)
    ax.set_ylim([min(y) - np.std(y), max(y) + np.std(y)])
    ax.set_xlim([min(x), max(x)])
    ax.grid(True)

# 为给定的行为画出一段时间（180 × 50ms）的波形图
def plot_activity(activity, data):
    fig, (ax0, ax1, ax2) = plt.subplots(nrows=3, figsize=(15, 10), sharex=True)
    plot_axis(ax0, data['timestamp'], data['x-axis'], 'x-axis')
    plot_axis(ax1, data['timestamp'], data['y-axis'], 'y-axis')
    plot_axis(ax2, data['timestamp'], data['z-axis'], 'z-axis')
    plt.subplots_adjust(hspace=0.2)
    fig.suptitle(activity)
    plt.subplots_adjust(top=0.90)
    plt.show()

if __name__ == "__main__":
    dataset = read_data("data/WISDM_ar_v1.1_modify.txt")
    dataset['x-axis'] = feature_normalize(dataset['x-axis'])
    dataset['y-axis'] = feature_normalize(dataset['y-axis'])
    dataset['z-axis'] = feature_normalize(dataset['z-axis'])

    # 为每个行为绘图
    for activity in np.unique(dataset["activity"]):
        subset = dataset[dataset["activity"] == activity][:180]
        plot_activity(activity, subset)
