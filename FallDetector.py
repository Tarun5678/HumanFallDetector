import pandas as pd
import joblib
from sklearn.ensemble import RandomForestClassifier
from sklearn_porter import Porter
import numpy as np
from sklearn.model_selection import train_test_split
import itertools
import time
import sys
import threading
import os
from sklearn.tree import export_graphviz

start = time.time()
path = r'FALL_DETECTION_TRAINING.csv'
df = pd.read_csv(path, encoding="ISO-8859-1", engine='python')
done = False


def animate():
    for c in itertools.cycle(['|', '/', '-', '\\']):
        if done:
            break
        sys.stdout.write('\rTraining the model ' + c)
        sys.stdout.flush()
        time.sleep(0.1)
    sys.stdout.write('\rFinished Training in')
    print(":", time.time() - start)


t = threading.Thread(target=animate)
t.start()


# Training
# Features selected accX,accY,accZ,gyroX,gyroY,gyroZ
def Train(data):
    features = zip(data['acc_x'], data['acc_y'], data['acc_z'], data['gyro_x'], data['gyro_y'], data['gyro_z'])
    x_train = list(features)
    y_train = data['label']
    '''Number of trees in random forest classifier are 50'''
    model = RandomForestClassifier(n_estimators=100, max_depth=3, bootstrap=False, random_state=20)
    # Train function
    model.fit(x_train, y_train)

    # print(model.feature_importances_)
    # Saving the trained model
    joblib.dump(model, 'Fall_detection_model.pk1')
    estimator = model.estimators_[10]
    columns = data.columns.values
    export_graphviz(estimator, feature_names=columns[0:6], class_names=y_train, out_file='tree.dot', filled=True,
                    proportion=False,

                    precision=2, rounded=True)

    # os.system('dot -T png tree.dot -o tree.png -Gdpi=1080')
    # porter = Porter(model, language='java')
    # output = porter.export(embed_data=True)
    # with open('RandomForestClassifier.java', 'w') as f:
    #     f.write(output)


if __name__ == '__main__':
    Train(df)
    done = True
    time.sleep(40)
    done = False
    t = threading.Thread(target=animate)
    t.start()
    os.system('FallDetector.py')
