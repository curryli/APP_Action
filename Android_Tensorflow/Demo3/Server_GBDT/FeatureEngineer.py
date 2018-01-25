# -*- coding:utf-8 -*-
import math
import numpy as np
 
FEATURE = ("mean", "max", "min", "std")
STATUS  = ("Jogging","StandingBy","Static","Walking")
def preprocess(file_dir, Seg_granularity):
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
            gravity_tuple = {"gravity": gravity, "status": status}
            gravity_data.append(gravity_tuple)
    # split data sample of gravity
    splited_data = []
    cur_cluster  = []
    counter      = 0
    last_status  = gravity_data[0]["status"]  #初始化
    for gravity_tuple in gravity_data:
        if (counter >= (Seg_granularity-1) or gravity_tuple["status"] != last_status):
            seg_data = {"status": last_status, "values": cur_cluster}
            # print seg_data
            splited_data.append(seg_data)
            cur_cluster = []
            counter = 0
        cur_cluster.append(gravity_tuple["gravity"])
        last_status = gravity_tuple["status"]
        counter += 1

    print splited_data[2]  #选一条记录看看是什么样子： values包含100个元素，也就是设定的间隔  {'status': 'Jogging', 'values': [15.965336783203556, 4.438687163199494, 8.07636041743503, 22.08517498731183, 9.156744142339164, 8.341804259546842, 14.731581696268357, 19.972321581215958, 5.67705509086633, 9.859533005147286, 19.679375157498225, 11.376709360627965, 17.516027647348487, 14.442925899810495, 8.681548065676605, 2.2003923599370654, 21.300229696770057, 5.03308243654559, 19.279437958434823, 21.17894836371291, 17.90599770036391, 5.698223414140311, 18.83020361724449, 12.576730131439136, 11.869740303126019, 11.669331832043643, 15.655053613791887, 6.405098055194291, 10.696144918001359, 20.119249989374826, 15.768920937447458, 18.132100510644417, 11.533547020002906, 4.8932699068014545, 22.984844536991176, 11.50159916792927, 11.644687959301132, 14.722714204166168, 9.234595788200592, 15.237279267514449, 3.1456517170193803, 21.842159168238354, 4.432748302264233, 19.924515934737357, 10.922966075290551, 15.325469935482024, 3.0072604495909, 22.687076467358658, 3.460131332333085, 22.330513345651553, 7.323657154081991, 14.0025656491593, 4.064390247438931, 20.22519400074123, 8.999133943493417, 2.2445903133796534, 19.798009307237066, 17.231144415180236, 9.109314553125161, 2.5092880181885255, 20.48792754268834, 7.349701701637084, 18.815819405904602, 8.232044008319061, 4.688605497800229, 6.459980471205635, 12.496518789942252, 12.926749726454013, 20.014595576738735, 5.333144514886505, 4.740820538075528, 7.806063772877907, 2.989565557550317, 19.65176396982796, 4.980262833650723, 8.372515526406197, 12.088586015026197, 3.1827928889074095, 21.817228493645338, 6.283971579585293, 20.234469722732484, 5.9525128361623185, 14.187408810618141, 3.250843477456675, 21.16802717987735, 3.06045902620949, 16.051480478690753, 19.532632013716423, 12.86730812404972, 12.074582212441452, 21.624897186342782, 2.426869253916614, 19.61678621693656, 4.937803361825106, 16.85068790630294, 2.7474980378578526, 23.599974264760363, 5.921359039938819, 5.605993689513944, 12.805429971373927]}

    # compute statistics of gravity data
    statistics_data = []
    for seg_data in splited_data:
        np_values = np.array(seg_data.pop("values"))
        seg_data["max"]  = np.amax(np_values)
        seg_data["min"]  = np.amin(np_values)
        seg_data["std"]  = np.std(np_values)
        seg_data["mean"] = np.mean(np_values)
        statistics_data.append(seg_data)
    # write statistics result into a file in format of LibSVM
    with open("motion_FE.csv", "w") as the_file:
        Head = "label" + "," + "mean" + "," + "max" + "," + "min" + "," + "std" + "\n"
        the_file.write(Head)
        for seg_data in statistics_data:
            if seg_data["status"] in STATUS:
                row = str(STATUS.index(seg_data["status"])) + "," + \
                      str(seg_data["mean"]) + "," + \
                      str(seg_data["max"]) + "," + \
                      str(seg_data["min"]) + "," + \
                      str(seg_data["std"]) + "\n"
                # print row
                the_file.write(row)
if __name__ == "__main__":
    preprocess("labeled_motion.csv", 100)
    pass