import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, roc_curve, auc
if __name__ == "__main__":
    column_names = ['user-id', 'activity', 'timestamp', 'x-axis', 'y-axis', 'z-axis']
    df = pd.read_csv("data/WISDM_ar_v1.1_modify.txt", header=None, names=column_names)
    n = 10
    print "df"
    print df.head(n)

    print df["user-id"].value_counts().to_frame(name="Counts")

    print pd.DataFrame(df["activity"].value_counts())


    activity_of_subjects = pd.DataFrame(df.groupby("user-id")["activity"].value_counts())
    print "activity_of_subjects"

    # groupby后会出现层次化索引，把层次化索引重新安排到DataFrame中需要使用unstack()方法，想回去就用stack()
    print activity_of_subjects  # .unstack().head(n)
    activity_of_subjects.unstack().plot(kind='bar', stacked=True, colormap='Blues', title="Distribution")
    plt.show()



# df
#    user-id activity       timestamp    x-axis     y-axis        z-axis
# 0       33  Jogging  49105962326000 -0.694638  12.680544   0.50395286;
# 1       33  Jogging  49106062271000  5.012288  11.264028   0.95342433;

