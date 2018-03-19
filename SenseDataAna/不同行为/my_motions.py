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
 
#"Sitting", "Walking", "Upstairs", "Downstairs", "Jogging", "Standing"
if __name__ == '__main__':
    t_acc = 'z'
    
    df_walk = pd.read_csv(r"walk1.csv",names=['x', 'y', 'z', 'time'], sep=',')
    df_walk_slic = df_walk.loc[:, t_acc].reset_index(drop=True).loc[1000:2000] 
    
    plt.plot(df_walk_slic,label="walk",color='blue')
     
    
    df_jog = pd.read_csv(r"jog2.csv",names=['x', 'y', 'z', 'time'], sep=',')
    df_jog_slic = df_jog.loc[:, t_acc].reset_index(drop=True).loc[1000:2000] 
    plt.plot(df_jog_slic,label="jog",color='green')
    
    
    df_down = pd.read_csv(r"down1.csv",names=['x', 'y', 'z', 'time'], sep=',')
    df_down_slic = df_down.loc[:, t_acc].reset_index(drop=True).loc[1000:2000] 
    plt.plot(df_down_slic,label="down1",color='red')
     
    plt.show()
    
    
    
#合并文件    
    
    df_walk['cat'] = "Walking" 
    df_jog['cat'] = "Jogging" 
    df_down['cat'] = "Downstairs" 
     
     
     
    df_All = pd.concat([df_walk, df_jog], axis=0)
    df_All = pd.concat([df_All, df_down], axis=0)
    
    df_All.to_csv("my_labeled_motion.csv",index=False, header=False)
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



