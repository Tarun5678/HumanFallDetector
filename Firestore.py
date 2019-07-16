import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from datetime import datetime
import numpy as np
import joblib
import time
import itertools
import threading
import sys
import pandas as pd
import os
import sys
from Twilio import sendMessage
from sklearn.tree import export_graphviz

cred = credentials.Certificate("./falldetector2-firebase-adminsdk-0oa9k-24cc73d332.json")
app = firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://falldetector2.firebaseio.com/'})
done = False

TRAIN_FALL = pd.read_csv(r'FALL_DETECTION_TRAINING.csv', index_col=False)
TRAIN_FALL.reset_index(drop=True)


def updateTrainFile(prediction, Time, eName, ePhone):
    label = ""
    refs = db.reference('sensorData')
    snapshots = refs.order_by_key().limit_to_last(1).get()
    for timeStamp, value in snapshots.items():
        if timeStamp == Time:
            fall = value['fall']
            print(fall)
            if fall is "1":
                label = prediction
                sendMessage(eName, ePhone)
            else:
                label = "NotFall"
    d = [val['accX'], val['accY'], val['accZ'], val['gyroX'],
         val['gyroY'], val['gyroZ'], label]
    newData = TRAIN_FALL.values.tolist()
    newData.append(d)
    NEW_TRAINED_DATA = pd.DataFrame(newData,
                                    columns=('acc_x', 'acc_y', 'acc_z', 'gyro_x', 'gyro_y', 'gyro_z', 'label'))
    NEW_TRAINED_DATA.to_csv(r'FALL_DETECTION_TRAINING.csv', index=False)
    print("***** Data Recorded in Training Set *******")
    os.system("Firestore.py")


def animate():
    for c in itertools.cycle(['|', '/', '-', '\\']):
        if done:
            break
        sys.stdout.write('\rWaiting for data ' + c)
        sys.stdout.flush()
        time.sleep(0.1)
    sys.stdout.write('\rFall Detected!!!     ')


t = threading.Thread(target=animate)
t.start()

while True:
    ref = db.reference('sensorData')
    snapshot = ref.order_by_key().limit_to_last(1).get()
    model = joblib.load('Fall_detection_model.pk1')
    for key, val in snapshot.items():
        if key == (datetime.now().strftime("%d-%m-%Y %I:%M")):
            x = val['accX']
            y = val['accY']
            z = val['accZ']
            a = val['gyroX']
            b = val['gyroY']
            c = val['gyroZ']
            n = val['ecName']
            p = val['ecPhone']
            print(x, y, z, c)
            features = tuple([x, y, z, a, b, c])
            x_train = np.reshape(features, [1, -1])
            predicted = model.predict(x_train)
            if predicted[0] == 'FOL' or predicted[0] == 'FKL' or predicted[0] == 'BSC' or predicted[0] == 'SDL':
                ref_child = ref.child(key)
                ref_child.update({'fall': '1'})
                print("Fall Detected at", key)
                done = True
                time.sleep(50)
                updateTrainFile(predicted[0], key, n, p)
                done = False
                t = threading.Thread(target=animate)
                t.start()
            else:
                print("Not a FALL")
                ref_child = ref.child(key)
                ref_child.update({'fall': '0'})
                print(predicted[0])
                done = False
                time.sleep(50)
                updateTrainFile(predicted[0], key, n, p)
                t = threading.Thread(target=animate)
                t.start()
        else:
            done = False
            time.sleep(1)
