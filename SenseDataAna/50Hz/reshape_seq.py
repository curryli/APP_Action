# -*- coding: utf-8 -*-

import pandas as pd
from pandas import DataFrame
import numpy as np
from scipy import stats
import os
import time
from collections import Counter

def Reshape_seq(data, window=90):
    tot_size = data.shape[0]
    feat_cols = set(data.columns.values)
    feat_cols.remove('label')
    feat_cnt = len(feat_cols)

    features = np.empty((0, window, feat_cnt))
    labels = np.empty((0))
    start = 0
    while start < tot_size:
        end = start + window
        if (end > tot_size):
            start = tot_size - window
            end = start + window

        feat_seg = [data[f][start:end] for f in feat_cols]
        cur_feat = np.dstack(feat_seg)
        cur_label = stats.mode(data["label"][start:end])[0][0]      # 或者写为 cur_label2 = Counter(data["label"][start:end]).most_common(1)[0][0]  # 取该窗口中出现最多的label作为该片段的label
        features = np.vstack([features, cur_feat])
        labels = np.append(labels, cur_label)
        start = end
        print(start)
    return features, labels


def Reshape_semi_overlap(data, window=90):
    tot_size = data.shape[0]
    feat_cols = set(data.columns.values)
    feat_cols.remove('label')
    feat_cnt = len(feat_cols)

    features = np.empty((0, window, feat_cnt))
    labels = np.empty((0))
    start = 0
    while start < tot_size:
        end = start + window
        if (end > tot_size):
            start = tot_size - window
            end = start + int(window/2)


        feat_seg = [data[f][start:end] for f in feat_cols]
        cur_feat = np.dstack(feat_seg)
        cur_label = stats.mode(data["label"][start:end])[0][0]      # 或者写为 cur_label2 = Counter(data["label"][start:end]).most_common(1)[0][0]  # 取该窗口中出现最多的label作为该片段的label
        features = np.vstack([features, cur_feat])
        labels = np.append(labels, cur_label)
        start = end
        print(start)
    return features, labels



if __name__=="__main__":
    column_names = ['user-id', 'label', 'timestamp', 'x-axis', 'y-axis', 'z-axis']
    df = pd.read_csv("data/WISDM_ar_v1.1_modify.txt", header=None, names=column_names)

    df_all = df[['x-axis', 'y-axis', 'z-axis', 'label']]

    segments, labels = Reshape_seq(df_all)

