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
    t_acc = 'z'
    
    df_All = pd.read_csv(r"Sample.csv",names=['ignore', 'cat', 'time', 'x', 'y','z'], sep=',')
    idx_list = list(df_All.cat.drop_duplicates())

    # df_i = df_All.loc[df_All['cat']=="Walking", t_acc].reset_index(drop=True).loc[1000:1100]
    # plt.plot(df_i,label="walk",color='blue')
    #
    #
    # df_i = df_All.loc[df_All['cat']=="Jogging", t_acc].reset_index(drop=True).loc[1000:1100]
    # plt.plot(df_i,label="jog",color='green')
    #
    # df_i = df_All.loc[df_All['cat']=="Downstairs", t_acc].reset_index(drop=True).loc[1000:1100]
    # plt.plot(df_i,label="down",color='red')
    
    
    df_i = df_All.loc[df_All['cat']=="Jogging", t_acc].reset_index(drop=True)
    plt.plot(df_i,label="i",color='red')


    plt.show()



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



