# -*- coding: utf-8 -*-
import pandas as pd
from sklearn.cross_validation import train_test_split
from sklearn.metrics import recall_score, precision_score
import numpy as np
#导入随机森林算法库
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression 
from sklearn.grid_search import GridSearchCV
from sklearn.grid_search import RandomizedSearchCV
from sklearn.metrics import confusion_matrix
from sklearn.utils import shuffle 
from sklearn.ensemble import GradientBoostingClassifier
from sklearn.externals import joblib  
from sklearn.metrics import classification_report  
  
df_All = pd.read_csv("WISDM_FE_save.csv", sep=',')
df_All = df_All.fillna(-1)

df_All = shuffle(df_All) 


df_X = df_All.drop(["label"], axis=1,inplace=False)

df_y = df_All["label"]

clf = joblib.load("GBDT_trained.m")  

pred = clf.predict(df_X) 

cm1=confusion_matrix(df_y,pred)
print  cm1
 
print(classification_report(df_y,pred))

