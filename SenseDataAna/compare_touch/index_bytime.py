# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
import numpy as np
import os  # python miscellaneous OS system tool
from collections import Counter

from sklearn import preprocessing
from datetime import datetime

import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib.dates import AutoDateLocator

# from month_cnt_func import Month_Cnt_class




def get_timestamp(x):
    return datetime.strptime(x, "%m-%d %H:%M:%S:%f")

def ts_delta(x, start):
    return x - start



if __name__ == '__main__':
    df_ori = pd.read_csv(r"lxr.csv",names=['idx', 'x', 'y', 'sens1', 'sens2','time'], sep=',')
     
    #方法1
    #df_ori["ts"] = df_ori['time'].map(lambda x: get_timestamp(x))
    
    #方法2
    df_ori["ts"] = pd.to_datetime(df_ori['time'], format= "%m-%d %H:%M:%S:%f")

    idx_list = list(df_ori.idx.drop_duplicates())

    for i in idx_list:
        df_i = df_ori.loc[df_ori['idx'] == i].copy()
        df_i = df_i.reset_index(drop=True)
        first_ts_i = df_i["ts"].loc[0]

        df_i["ts_delta"] = df_i["ts"].map(lambda x: ts_delta(x,first_ts_i))
        df_i.index = df_i["ts_delta"]
        plt.plot(df_i["sens1"], label="df_i", color='red')
    plt.show()



    #df_lag1 = df_ori.shift(1)



























