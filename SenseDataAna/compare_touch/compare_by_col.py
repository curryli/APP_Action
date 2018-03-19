# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
import numpy as np
import os                          #python miscellaneous OS system tool
from collections import Counter

from sklearn import preprocessing
from dateutil import parser

import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib.dates import AutoDateLocator

#from month_cnt_func import Month_Cnt_class
 

if __name__ == '__main__':
    col_focus = "sens1"

    plt.title('type: '+ col_focus, fontsize='large', fontweight='bold')

    df_All = pd.read_csv(r"lxr.csv",names=['idx', 'x', 'y', 'sens1', 'sens2','time'], sep=',')
    idx_list = list(df_All.idx.drop_duplicates())

    for i in idx_list:
        df_i = df_All.loc[df_All['idx']==i, col_focus].reset_index(drop=True)
        plt.plot(df_i,label="i",color='blue')

    df_All = pd.read_csv(r"zjb.csv",names=['idx', 'x', 'y', 'sens1', 'sens2','time'], sep=',')
    idx_list = list(df_All.idx.drop_duplicates())
    for i in idx_list:
        df_i = df_All.loc[df_All['idx']==i, col_focus].reset_index(drop=True)
        plt.plot(df_i,label="i",color='red')

    df_All = pd.read_csv(r"hzl.csv",names=['idx', 'x', 'y', 'sens1', 'sens2','time'], sep=',')
    idx_list = list(df_All.idx.drop_duplicates())
    for i in idx_list:
        df_i = df_All.loc[df_All['idx']==i, col_focus].reset_index(drop=True)
        plt.plot(df_i,label="i",color='green')

    plt.show()



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



