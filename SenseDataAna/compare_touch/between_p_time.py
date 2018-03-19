# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
import numpy as np
import os  # python miscellaneous OS system tool
from collections import Counter

from sklearn import preprocessing
from dateutil import parser

import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib.dates import AutoDateLocator

# from month_cnt_func import Month_Cnt_class


if __name__ == '__main__':
    df_All = pd.DataFrame(columns=['idx', 'x', 'y', 'sens1', 'sens2', 'time'])  # 初始化一个空dataframe
    csv_files = os.listdir("./")
    for f in csv_files:
        if "csv" in f:
            df_tmp = pd.read_csv(f, names=['idx', 'x', 'y', 'sens1', 'sens2', 'time'], sep=',')
            df_All = pd.concat([df_All, df_tmp], axis=0)

    print df_All.shape

    zjb = df_All.loc[df_All['idx'] == 3, 'sens1'].reset_index(drop=True)
    lxr = df_All.loc[df_All['idx'] == 31, 'sens1'].reset_index(drop=True)

    print zjb.head(5)

    plt.plot(zjb, label="zjb", color='blue')
    plt.plot(lxr, label="lxr", color='red')
    plt.show()


























