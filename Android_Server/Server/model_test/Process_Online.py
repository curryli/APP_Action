# -*- coding:utf-8 -*-
import math
import numpy as np
import pandas as pd
from sklearn.cross_validation import train_test_split
from sklearn.metrics import recall_score, precision_score
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.grid_search import GridSearchCV
from sklearn.grid_search import RandomizedSearchCV
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.externals import joblib
from sklearn.metrics import classification_report
import FE_Online as feo
 

if __name__ == "__main__":
    # gravity_data = feo.File2Arr("test_one_seg.csv")
    # Seg_num = 100
    # df_X = feo.preprocess(gravity_data, Seg_num)
    # print "Feature:"
    # print df_X
    # print "Stat:"
    clf = joblib.load("train_model.m")

    statistics_data = [[9.831092,9.873051,9.79706,0.014164]]
    df_X = pd.DataFrame(statistics_data, columns=['mean', 'max', 'min', 'std'])


    pred = clf.predict(df_X)
    print pred
    # for i in pred:
    #     print i