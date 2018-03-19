# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
import numpy as np
import os                          #python miscellaneous OS system tool
from collections import Counter
from sklearn import preprocessing
from dateutil.parser import parse

import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from matplotlib.dates import AutoDateLocator

from datetime import datetime

if __name__ == '__main__':
    s1 = r"01-10 11:16:26:919"
    s2 = r"01-10 11:16:26:952"
    t1 = datetime.strptime(s1, "%m-%d %H:%M:%S:%f")
    t2 = datetime.strptime(s2, "%m-%d %H:%M:%S:%f")

    print (t2 - t1)
    print (t2 - t1).seconds
    
    




    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    



