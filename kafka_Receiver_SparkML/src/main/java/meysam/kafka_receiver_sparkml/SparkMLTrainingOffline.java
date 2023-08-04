package meysam.kafka_receiver_sparkml;

import meysam.kafka_receiver_sparkml.MLmodels.myRandomForest;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.types.DataTypes.*;
import static meysam.kafka_receiver_sparkml.Constants.*;


public class SparkMLTrainingOffline {

    private static final Logger logger = Logger.getLogger(meysam.kafka_receiver_sparkml.SparkMLTrainingOffline.class.getName());

    private static final String APPLICATION_NAME = "Spark ML Training Offline";
    private static final String CASE_SENSITIVE = "false";

    private static final String JSON_FORMAT = "json";
    private static final String DATASET_FOLDER_PATH = "../src/main/resources/data/";

    public static void main(String[] args)
	    throws InterruptedException, StreamingQueryException, IOException {

//        System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR_VALUE);

	// * the schema can be written on disk, and read from disk
        // * the schema is not mandatory to be complete, it can contain only the needed fields
        StructType ARGUS_SCHEMA =
                new StructType()

                        .add("SrcAddr", StringType, true).add("DstAddr", StringType, true)
                        .add("SrcMac", StringType, true).add("DstMac", StringType, true)
                        .add("Sport", StringType, true).add("Dport", StringType, true)
                        .add("State", StringType, true).add("Proto", StringType, true)
                        .add("SrcRate",DoubleType, true).add("DstRate", DoubleType, true)
                        .add("class", IntegerType, true);

//                        .add("SrcAddr", StringType, true).add("DstAddr", StringType, true)
//                        .add("SrcMac", StringType, true).add("DstMac", StringType, true)
//                        .add("Sport", StringType, true).add("Dport", StringType, true)
//                        .add("State", StringType, true).add("Proto", StringType, true)
//                        .add("Dur", DoubleType, true).add("SrcRate", DoubleType, false)
//                        .add("DstRate", DoubleType, false).add("SrcWin", DoubleType, false)
//                        .add("DstWin", DoubleType, false).add("sMeanPktSz", DoubleType, false)
//                        .add("dMeanPktSz", DoubleType, false).add("sTtl", DoubleType, false)
//                        .add("dTtl", DoubleType, false).add("sHops", DoubleType, false)
//                        .add("dHops", DoubleType, false).add("class", IntegerType, true);
//


        initStatesMap();

        final SparkConf conf = new SparkConf()
                .setMaster(RUN_LOCAL_WITH_AVAILABLE_CORES)
                .setAppName(APPLICATION_NAME)
                .set("spark.sql.caseSensitive", CASE_SENSITIVE);

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();


        // Gathering Data
        Dataset<Row> gatheredDF = spark.read()
                .format("csv")
//                .option("sep", ",")
//                .option("inferSchema", "true")
                .option("header", "true")
                .schema(ARGUS_SCHEMA)
                .load(DATASET_FOLDER_PATH);

        // Data Preparation

        spark.udf().register("One_hoted", new UDF1<String, int[]>() {
            @Override
            public int[] call(String x) {
                int a[] = new int[16];

                if (x != null)
                    for (int i = 0; i < x.length(); i++) {
                        a[MY_MAP.get(x.charAt(i) + "")] = 1;
                    }

                return a;

            }
        }, DataTypes.createArrayType(IntegerType));


        gatheredDF.show(false);

        gatheredDF = gatheredDF
                .withColumn("udf",
                        functions.callUDF("One_hoted",
                                gatheredDF.col("state"))).alias("udf");

//        gatheredDF.select(col("udf")).foreach(v1 -> {
//            System.out.println("#########################################");
//            for (int i=0;i<16;i++)
//            System.out.print(v1.getList(0).toArray()[i]);
//        });

        for (int i = 0; i < 16; i++) {
            gatheredDF = gatheredDF.withColumn("s_" + states[i],
                    col("udf").getItem(i));
        }

        gatheredDF = gatheredDF.drop(col("udf"))
                .drop(col("s_ "));

        Dataset<Row> preparedDF = gatheredDF.withColumn("label",
		    col("class").cast("int")).drop("class");

        String cat_cols[]={"Proto"};


        String non_feature_cols[]= (String[]) ArrayUtils.addAll(cat_cols,
                new String[]{"label","State","SrcAddr",
                        "DstAddr","SrcMac","DstMac","Sport","Dport"});

        preparedDF=preparedDF.na().fill(0);
        preparedDF=preparedDF.na().fill(" ");


//
        myRandomForest randomForest =new
                myRandomForest(cat_cols,non_feature_cols);
        randomForest.Train(preparedDF);
//u should uncomment below models definotion to compare them all:
//       myLogisticRegression myLogisticRegression= new
//                myLogisticRegression(cat_cols,non_feature_cols);
//        myLogisticRegression.Train(preparedDF);
//
//        myDecisionTree DT =new
//                myDecisionTree(cat_cols,non_feature_cols);
//        DT.Train(preparedDF);
//
//        myMLPC mlpc=new myMLPC(cat_cols,non_feature_cols);
//        mlpc.Train(preparedDF);

//        myNaiveBayes naiveBayes=new myNaiveBayes(cat_cols,non_feature_cols);
//        naiveBayes.Train(preparedDF);




        // Save the model on disk
     //   pipelineModel.write().overwrite().save(MODEL_FOLDER_PATH);
    }


    static String states[] = {"E", "S", "C",
            "P", "D", "R", "O", "N", "X", "f",
            "Q", "s", "T", "I", "F", " "};

    static Map<String, Integer> MY_MAP = new HashMap<>();

    private static void initStatesMap() {
        for (int i = 0; i < states.length; i++) {
            MY_MAP.put(states[i], i);
        }
    }
}
