package meysam.kafka_receiver_sparkml;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.sparkproject.guava.collect.ImmutableList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.types.DataTypes.*;

@SpringBootApplication
public class Test {

    private static final String HADOOP_HOME_DIR_VALUE = "C:/winutils";


    public static void main(String[] args) {
        SpringApplication.run(meysam.kafka_receiver_sparkml.Test.class, args);

        System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR_VALUE);

        initStatesMap();


        SparkSession spark = SparkSession
                .builder()
                .appName("SparkSample")
                .master("local[*]")
                .config("spark.sql.caseSensitive", true)
                .getOrCreate();


//        LongAccumulator accum = spark.sparkContext().longAccumulator();
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


//        spark.udf().register("sum", new UDF1<String,String>() {
//            @Override
//            public String call(String x) {
//
//                accum.add(1);
//                return "abcd";
//            }
//        }, StringType);


        StructType schemata = DataTypes.createStructType(
                new StructField[]{
                        createStructField("Proto", StringType, true),
                        createStructField("State", StringType, true),
                        createStructField("class", IntegerType, false),
                        createStructField("NUM_VALUE", IntegerType, false),
                });
        Row r1 = RowFactory.create("tcp","sSE f", 0, 1);
        Row r2 = RowFactory.create("udp","INT", 1, 2);
        Row r3 = RowFactory.create("tcp","sS", 0, null);
        Row r4 = RowFactory.create(null,"CON", 0, null);
        Row r5 = RowFactory.create("udp","INT", 1, null);
        List<Row> rowList = ImmutableList.of(r1, r2, r3, r4,r5);
        Dataset<Row> data = spark.sqlContext().createDataFrame(rowList, schemata);
        data.show(false);


        data = data
                .withColumn("udf", functions.callUDF("One_hoted", data.col("State"))).alias("udf");
//                .withColumn("1",functions.substring(col("udf"),0,1))
//                .withColumn("2",col("udf").substr(2,1))
//                .drop("udf");


        for (int i = 0; i < 16; i++) {
            data = data.withColumn("s_" + states[i],
                    col("udf").getItem(i));
        }

        data = data.drop(col("udf"))
                .drop(col("s_ "));


        /*Imputer imputer = new Imputer()
                .setInputCols(new String[]{"NUM_VALUE"})
                .setOutputCols(new String[]{"NUM_VALUE"});*/
/*
        imputer.setStrategy("mean");
        imputer.setMissingValue(0);
        ImputerModel model = imputer.fit(data);
        model.transform(data).show();*/

        data.show(false);

//        String input[]= (String[]) ArrayUtils.removeElement(data.columns(), "STRING_VALUE");
//        for(int i=0;i<input.length;i++)
//        System.out.print(input[i]+",");

        run_pipe(data);
    }

    static String keys[] = {"saddr", "daddr", "smac", "dmac", "sport", "dport"
            , "state", "proto", "dur", "srate", "drate", "swin", "dwin", "smeansz", "dmeansz"
            , "sttl", "dttl", "shops", "dhops"
    };
    static String states[] = {"E", "S", "C", "P", "D", "R", "O", "N", "X", "f", "Q", "s", "T", "I", "F", " "};

    static Map<String, Integer> MY_MAP = new HashMap<>();

    private static void initStatesMap() {
        for (int i = 0; i < states.length; i++) {
            MY_MAP.put(states[i], i);
        }
    }

    private static void run_pipe(Dataset<Row> dataset){

        // Data Preparation


//        Dataset<Row> yesnoDF = filteredDF.withColumn("response",
//		    when(col("response").equalTo("yes"), "1").otherwise("0"));
//
        Dataset<Row> preparedDF = dataset.withColumn("label",
                col("class").cast("double")).drop("class");
//			    .withColumnRenamed("responseNum", "label");

        String cat_cols[]={"Proto"};
        StringIndexer indexer = new StringIndexer()
                .setInputCol("Proto")
                .setOutputCol("cat_cols_indexed");
        indexer.setHandleInvalid("keep");

//        Dataset<Row> indexed = indexer.fit(preparedDF).transform(preparedDF);
//        indexed.show();

        OneHotEncoder encoder = new OneHotEncoder()
                .setInputCol(indexer.getOutputCol())
                .setOutputCol("one_hoted_cols");

//        OneHotEncoderModel model = encoder.fit(indexed);
//        Dataset<Row> encoded = model.transform(indexed);


        String non_feature_cols[]= (String[]) ArrayUtils.addAll(cat_cols, new String[]{"label","State"});

        String feature_cols[]= (String[]) ArrayUtils.addAll(
                (preparedDF.drop(non_feature_cols)).columns()
                , new String[]{encoder.getOutputCol()});

        System.out.println("*******************features**********************");
        for(int i=0;i<feature_cols.length;i++)
        System.out.print(feature_cols[i]+",");

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(feature_cols)
                .setOutputCol("features");

        // Choosing a Model
        LogisticRegression logisticRegression = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.00001)
                .setElasticNetParam(0.1)
                .setThreshold(0.1);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                        indexer,encoder,
                        vectorAssembler, logisticRegression
                });

        // Training the data
        Dataset<Row>[] splitDF = preparedDF.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainDF = splitDF[0];
        Dataset<Row> evaluationDF = splitDF[1];

        PipelineModel pipelineModel = pipeline.fit(trainDF);

        // Evaluation
        Dataset<Row> predictionsDF = pipelineModel.transform(evaluationDF);

        predictionsDF.show(false);
    }
	
//how to create test DF:
/*list = [("1", "name1"), ("2", "name2"), ("3", "name3"), ("4", "name4")]

 df = spark.createDataFrame(list, ["id", "name"])*/

}
