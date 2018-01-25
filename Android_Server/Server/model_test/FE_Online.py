# -*- coding:utf-8 -*-
import math
import numpy as np
import pandas as pd
 

def File2Arr(file_dir):
    gravity_data = []
    with open(file_dir) as f:
        index = 0
        for line in f:
            raw_list = line.strip().split(',')
            index = index + 1
            status  = raw_list[3]
            acc_x = float(raw_list[0])
            acc_y = float(raw_list[1])
            acc_z = float(raw_list[2])
            if acc_x == 0 or acc_y == 0 or acc_z == 0:
                continue
            gravity = math.sqrt(math.pow(acc_x, 2)+math.pow(acc_y, 2)+math.pow(acc_z, 2))
            gravity_data.append(gravity)
        return gravity_data



def preprocess(gravity_data, Seg_granularity):
    # split data sample of gravity
    splited_data = []
    cur_cluster  = []
    counter      = 0

    for gravity in gravity_data:
        if (counter >= Seg_granularity-1):
            seg_data = {"placeholder": "P", "cur_cluster": cur_cluster}
            # print seg_data
            splited_data.append(seg_data)
            cur_cluster = []
            counter = 0
        cur_cluster.append(gravity)
        counter += 1

    # compute statistics of gravity data
    statistics_data = []
    for seg_data in splited_data:
        np_values = np.array(seg_data["cur_cluster"])
        seg_data["mean"] = np.mean(np_values)
        seg_data["max"]  = np.amax(np_values)
        seg_data["min"]  = np.amin(np_values)
        seg_data["std"]  = np.std(np_values)
        tmp_feat = [seg_data["mean"], seg_data["mean"], seg_data["mean"], seg_data["mean"]]
        statistics_data.append(tmp_feat)

    df_statistics = pd.DataFrame(statistics_data, columns=['mean','max','min','std'])
    return df_statistics

if __name__ == "__main__":
    gravity_data = File2Arr("labeled_motion.csv")
    print preprocess(gravity_data, 100)
