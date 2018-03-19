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
import math

#from month_cnt_func import Month_Cnt_class
 
from mpl_toolkits.mplot3d import Axes3D
if __name__ == '__main__':
    t_acc = 'y'
    #df_All = pd.read_csv(r"StandingBy_01211907.csv",names=['x','y','z','time'], sep=',')
    #df_All = pd.read_csv(r"Jogging_01201845.csv", names=['x', 'y', 'z', 'time'], sep=',')
    #df_All = pd.read_csv(r"Upstairs_01201906.csv", names=['x', 'y', 'z', 'time'], sep=',')
    #df_All = pd.read_csv(r"Walking_01201836.csv", names=['x', 'y', 'z', 'time'], sep=',')
    #df_All = pd.read_csv(r"Downstairs_01201750.csv", names=['x', 'y', 'z', 'time'], sep=',')
    df_All = pd.read_csv(r"Static_01220850.csv", names=['x', 'y', 'z', 'time'], sep=',')

    #plt.plot(df_i,label="walk",color='blue')

    # figure = plt.figure()
    # ax = figure.add_subplot(111, projection='3d')
    #
    # ax.plot(df_All.loc[:, "x"],df_All.loc[:, 'y'],df_All.loc[:, 'z'],'g^' )


    def getGrav(x):
        return math.sqrt(x["x"] * x["x"] + x["y"] * x["y"] +x["z"] * x["z"])

    df_All["grav"] = df_All.apply(getGrav, axis=1)
    plt.plot(df_All["grav"], label="walk", color='blue')

    plt.show()

    # used_cols = ['x','y','z','label']
    # df_All = pd.DataFrame(columns=used_cols)  # 初始化一个空dataframe
    #
    # df_Jogging = pd.read_csv(r"Jogging_01201845.csv", names=used_cols, sep=',')
    # df_Jogging['label'] = "Jogging"
    # df_Upstairs = pd.read_csv(r"Upstairs_01201906.csv", names=used_cols, sep=',')
    # df_Upstairs['label'] = "Upstairs"
    # df_Walking = pd.read_csv(r"Walking_01201836.csv", names=used_cols, sep=',')
    # df_Walking['label'] = "Walking"
    # df_Downstairs = pd.read_csv(r"Downstairs_01201750.csv", names=used_cols, sep=',')
    # df_Downstairs['label'] = "Downstairs"
    # df_Static = pd.read_csv(r"Static_01220850.csv", names=used_cols, sep=',')
    # df_Static['label'] = "Static"
    # df_StandingBy = pd.read_csv(r"StandingBy_01211907.csv", names=used_cols, sep=',')
    # df_StandingBy['label'] = "StandingBy"
    #
    #
    # df_All = pd.concat([df_All, df_Jogging[used_cols]], axis=0)
    # df_All = pd.concat([df_All, df_Upstairs[used_cols]], axis=0)
    # df_All = pd.concat([df_All, df_Walking[used_cols]], axis=0)
    # df_All = pd.concat([df_All, df_Downstairs[used_cols]], axis=0)
    # df_All = pd.concat([df_All, df_Static[used_cols]], axis=0)
    # df_All = pd.concat([df_All, df_StandingBy[used_cols]], axis=0)
    #
    # df_All.to_csv("labeled_motion.csv",index=False, header=False)



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



