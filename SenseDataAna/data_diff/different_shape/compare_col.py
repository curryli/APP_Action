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
    col_focus = "y"
    plt.title('type: '+ col_focus, fontsize='large', fontweight='bold')

    df_All = pd.DataFrame(columns=['idx', 'x', 'y', 'sens1', 'sens2', 'time'])  # 初始化一个空dataframe
    csv_files = os.listdir("./")
    for f in csv_files:
        if "csv" in f:
            df_tmp = pd.read_csv(f, names=['idx', 'x', 'y', 'sens1', 'sens2', 'time'], sep=',')
            df_All = pd.concat([df_All, df_tmp], axis=0)

    idx_list = list(df_All.idx.drop_duplicates())

    for i in idx_list:
        df_i = df_All.loc[df_All['idx']==i, col_focus].reset_index(drop=True)
        plt.plot(df_i,label="i",color='blue')


    plt.show()



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



