# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
import numpy as np
import os                          #python miscellaneous OS system tool
from collections import Counter
import matplotlib.pyplot as plt
from sklearn import preprocessing
from dateutil import parser
 
#from month_cnt_func import Month_Cnt_class
 

if __name__ == '__main__':

    df_All = pd.DataFrame(columns=['idx', 'x', 'y', 'sens1', 'sens2','time'])   #初始化一个空dataframe
    csv_files = os.listdir("./")
    for f in csv_files:
        if "csv" in f:
            df_tmp = pd.read_csv(f,names=['idx', 'x', 'y', 'sens1', 'sens2','time'], sep=',')
            df_All = pd.concat([df_All,df_tmp], axis=0)

    print df_All.shape
    print df_All.head(5)
    print df_All.loc[df_All['idx']==5,'sens1'].plot()
    plt.show()



    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



