/*
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.tencent.angel.ml.kmeans;

import com.tencent.angel.conf.AngelConfiguration;
import com.tencent.angel.ml.clustering.kmeans.KMeansRunner;
import com.tencent.angel.ml.conf.MLConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

public class KmeansTest {
  private Configuration conf = new Configuration();
  private String LOCAL_FS = LocalFileSystem.DEFAULT_FS;
  private String TMP_PATH = System.getProperty("java.io.tmpdir", "/tmp");

  static {
    PropertyConfigurator.configure("../conf/log4j.properties");
  }

  @Before
  public void setup() {

    String inputPath = "./src/test/data/clustering/iris";
    String dataFmt = "libsvm";

    // Cluster center number
    int centerNum = 3;
    // Feature number of train data
    int featureNum = 4;
    // Total iteration number
    int epochNum = 20;
    // Sample ratio per mini-batch
    double spratio = 1.0;
    // C
    double c = 0.15;

    // Set local deploy mode
    conf.set(AngelConfiguration.ANGEL_DEPLOY_MODE, "LOCAL");

    // Set basic configuration keys
    conf.setBoolean("mapred.mapper.new-api", true);
    conf.set(AngelConfiguration.ANGEL_INPUTFORMAT_CLASS, CombineTextInputFormat.class.getName());
    conf.setBoolean(AngelConfiguration.ANGEL_JOB_OUTPUT_PATH_DELETEONEXIST, true);

    //set angel resource parameters #worker, #task, #PS
    conf.setInt(AngelConfiguration.ANGEL_WORKERGROUP_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_WORKER_TASK_NUMBER, 1);
    conf.setInt(AngelConfiguration.ANGEL_PS_NUMBER, 1);

    //set Kmeans algorithm parameters #cluster #feature #epoch
    conf.set(MLConf.KMEANS_CENTER_NUM(), String.valueOf(centerNum));
    conf.set(MLConf.ML_FEATURE_NUM(), String.valueOf(featureNum));
    conf.set(MLConf.ML_EPOCH_NUM(), String.valueOf(epochNum));
    conf.set(MLConf.KMEANS_SAMPLE_RATIO_PERBATCH(), String.valueOf(spratio));
    conf.set(MLConf.kMEANS_C(), String.valueOf(c));

    // Set trainning data path
    conf.set(AngelConfiguration.ANGEL_TRAIN_DATA_PATH, inputPath);
    // Set data format
    conf.set(MLConf.ML_DATAFORMAT(), dataFmt);
  }

  @Test
  public void trainOnLocalClusterTest() throws Exception {
    // Set save model path
    conf.set(AngelConfiguration.ANGEL_SAVE_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    // Set log sava path
    conf.set(AngelConfiguration.ANGEL_LOG_PATH, LOCAL_FS + TMP_PATH + "/LOG/log");
    // Set actionType train
    conf.set(AngelConfiguration.ANGEL_ACTION_TYPE, MLConf.ANGEL_ML_TRAIN());

    KMeansRunner runner = new KMeansRunner();
    runner.train(conf);
  }


  @Test
  public void preictOnLocalClusterTest() throws Exception {
    // Set load model path
    conf.set(AngelConfiguration.ANGEL_LOAD_MODEL_PATH, LOCAL_FS + TMP_PATH + "/model");
    // Set predict result path
    conf.set(AngelConfiguration.ANGEL_PREDICT_PATH, LOCAL_FS + TMP_PATH + "/predict");
    // Set actionType prediction
    conf.set(AngelConfiguration.ANGEL_ACTION_TYPE, MLConf.ANGEL_ML_PREDICT());
    // Set log sava path
    conf.set(AngelConfiguration.ANGEL_LOG_PATH, LOCAL_FS + TMP_PATH + "/LOG/log");

    KMeansRunner runner = new KMeansRunner();
    runner.predict(conf);
  }
}
