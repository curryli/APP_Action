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
  
df_All = pd.read_csv("WISDM_FE.csv", sep=',')
df_All = df_All.fillna(-1)

df_All = shuffle(df_All) 


df_X = df_All.drop(["label"], axis=1,inplace=False)

df_y = df_All["label"]

X_train, X_test, y_train, y_test = train_test_split(df_X, df_y, test_size=0.2)


#n_estimators树的数量一般大一点。 max_features 对于分类的话一般特征束的sqrt，auto自动
clf = GradientBoostingClassifier(learning_rate=0.1, n_estimators=250,max_depth=50, min_samples_leaf =5, min_samples_split =2, max_features="auto", subsample=0.8, random_state=10)
#clf = GradientBoostingClassifier(random_state=100, n_estimators=100)
clf = clf.fit(X_train, y_train)
  
#print clf.score(X_test, y_test)

pred = clf.predict(X_test) 

cm1=confusion_matrix(y_test,pred)
print  cm1

# [[111   0   2   0   0  15]
#  [  0 758  35  38  25   0]
#  [  0  74 125  34  14   1]
#  [  0  77  62  73   7   0]
#  [  0  12  11   1 617   0]
#  [ 13   0   1   1   0  83]]
