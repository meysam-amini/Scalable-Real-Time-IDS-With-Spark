package meysam.kafka_receiver_sparkml;

import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import lombok.extern.slf4j.Slf4j;
import meysam.kafka_receiver_sparkml.model.NetFlow;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.linalg.DenseVector;
import org.apache.spark.sql.*;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.singletonList;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;
import static meysam.kafka_receiver_sparkml.Constants.*;


@Slf4j
@SpringBootApplication
public class SparkMLScoringOnline {

    private static final String MONGODB_OUTPUT_URI = "mongodb://localhost/NetFlow.NetFlow";
    private static final String APPLICATION_NAME = "Spark ML Scoring Online";
    private static final String CASE_SENSITIVE = "false";
    private static final String KAFKA_FORMAT = "kafka";
    private static final String KAFKA_TOPIC = "NetFlow";

    private static final String JSON_FORMAT = "json";
    private static final String QUERY_INTERVAL_SECONDS = "10 seconds";

    private static final String MODEL_LOGISTIC_REGRESSION = "Logistic_Regression";
    private static final String MODEL_RANDOM_FOREST = "Random_Forest";
    private static final String MODEL_DECISION_TREE = "Decision_Tree";
    private static final String MODEL_MLPC = "MLPC";

    private static final String MULTICLASS_CLASSIFICATION = "multi_classification";
    private static final String BINARY_CLASSIFICATION = "binary_classification";

    private static String MONGO_DATABASE_NAME="NetFlow";
    private static String MONGO_DATABASE_USERNAME="admin";
    private static String MONGO_DATABASE_PASSWORD="password";
    private static String MONGO_DATABASE_HOST="localhost";
    private static int MONGO_DATABASE_PORT=27017;



    public static void main(String[] args) throws StreamingQueryException, TimeoutException {
        SpringApplication.run(meysam.kafka_receiver_sparkml.SparkMLScoringOnline.class, args);

//        System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR_VALUE);

        // * the schema can be written on disk, and read from disk
        // * the schema is not mandatory to be complete, it can contain only the needed fields    

        StructType ARGUS_SCHEMA =
                new StructType()

                        /* as argus doesn't send numbers in right format, we
                         should define numeric fields also in String type: */

                        .add("SrcAddr", StringType, true).add("DstAddr", StringType, true)
                        .add("SrcMac", StringType, true).add("DstMac", StringType, true)
                        .add("Sport", StringType, true).add("Dport", StringType, true)
                        .add("State", StringType, true).add("Proto", StringType, true)
                        .add("SrcRate", StringType, true).add("DstRate", StringType, true);
        //  .add("class", IntegerType, false);
        initStatesMap();

        MongoCredential credential = MongoCredential.createCredential(MONGO_DATABASE_USERNAME, MONGO_DATABASE_NAME, MONGO_DATABASE_PASSWORD.toCharArray());
        Block<ClusterSettings.Builder> localhost = builder -> builder.hosts(singletonList(new ServerAddress(MONGO_DATABASE_HOST, MONGO_DATABASE_PORT)));
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(localhost)
                .credential(credential)
                .build();
        MongoClient mongo = MongoClients.create(settings);
//        MongoClient mongo = MongoClients.create();
        MongoTemplate template = new MongoTemplate(mongo, MONGO_DATABASE_NAME);
        try {
            CollectionOptions options = CollectionOptions.empty()
                    .capped().size(2000000000L)
                    .maxDocuments(2000000000L);
            template.createCollection("NetFlow", options);
        } catch (Exception e) {
            log.error("exception on creating collection : {}",e);
//            log.error(":::::::::::::::::::::::::::::::::::::::::::::::::::Collection Exists::::::::::::::::::::::::::::::::::::::::::::::::::::");
        }
//        template.save(new NetFlow());


        final SparkConf conf = new SparkConf()
                // .setMaster(MASTER_URI)
                .setMaster(RUN_LOCAL_WITH_AVAILABLE_CORES)
                .setAppName(APPLICATION_NAME)
//                .set("spark.driver.extraJavaOptions","-Dlog4jspark.root.logger=ERROR,console")
                .set("spark.mongodb.output.uri", MONGODB_OUTPUT_URI)///???
//                .set("spark.sql.jsonGenerator.ignoreNullFields","false")
                .set("spark.sql.caseSensitive", CASE_SENSITIVE);

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        String model_name = MODEL_LOGISTIC_REGRESSION;
        String binary_or_multi_classification = MULTICLASS_CLASSIFICATION;
        String model_path = MODELS_FOLDER_PATH + "/" +
                binary_or_multi_classification + "/" + model_name;


        PipelineModel pipelineModel = PipelineModel.load(model_path);

        Dataset<Row> gatheredDF = spark.readStream()
                .format(KAFKA_FORMAT)
                .option("kafka.bootstrap.servers", KAFKA_BROKERS)
                .option("subscribe", KAFKA_TOPIC)
                // .option("failOnDataLoss" , "false")
                .load().select(col("timestamp"),
                        from_json(col("value").cast("string"), ARGUS_SCHEMA)
                                .alias("NetFlow"))
                .alias("data")
                .select("data.timestamp", "data.NetFlow.*");

        gatheredDF.printSchema();

        spark.udf().register("One_hoted", new UDF1<String, int[]>() {
            @Override
            public int[] call(String x) {
                int a[] = new int[16];
                if (x != null)
                    for (int i = 0; i < x.length(); i++) {
                        if (MY_MAP.get(x.charAt(i)) != null)
                            a[MY_MAP.get(x.charAt(i) + "")] = 1;
                    }

                return a;

            }
        }, DataTypes.createArrayType(IntegerType));

        spark.udf().register("max_proba", new UDF1<DenseVector, Double>() {
            @Override
            public Double call(DenseVector denseVector) throws Exception {
                return denseVector.toArray()[denseVector.argmax()];
            }
        }, DoubleType);


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


        /*just because numbers are sent from argus in
        String format, at here we're converting them to number:*/
        gatheredDF = gatheredDF
                // .withColumn("dur", gatheredDF.col("dur").cast("double"))
                .withColumn("SrcRate", gatheredDF.col("SrcRate").cast("double"))
                .withColumn("DstRate", gatheredDF.col("DstRate").cast("double"))
        /*.withColumn("swin", gatheredDF.col("swin").cast("double"))
        .withColumn("dwin", gatheredDF.col("dwin").cast("double"))
        .withColumn("smeansz", gatheredDF.col("smeansz").cast("double"))
        .withColumn("dmeansz", gatheredDF.col("dmeansz").cast("double"))
        .withColumn("sttl", gatheredDF.col("sttl").cast("double"))
        .withColumn("dttl", gatheredDF.col("dttl").cast("double"))
        .withColumn("shops", gatheredDF.col("shops").cast("double"))
        .withColumn("dhops", gatheredDF.col("dhops").cast("double"))*/
//        .withColumn("label", gatheredDF.col("label").cast("int"))
        ;

        gatheredDF = gatheredDF.na().fill(0);
        gatheredDF = gatheredDF.na().fill(" ");

//        gatheredDF.printSchema();

        Dataset<Row> predictionDF = pipelineModel.transform(gatheredDF);

        Dataset<Row> targetDf = predictionDF
                .withColumn("probability",
                        functions.callUDF("max_proba",
                                predictionDF.col("probability"))).alias("probability");

        //  targetDf=targetDf.where(" (prediction!=0) OR (prediction=0 AND probability < 0.5)");
//        targetDf=targetDf.where(" (prediction!=0)");

//
        Column[] columns = {col("SrcAddr"), col("DstAddr"),
                col("SrcMac"), col("DstMac"), col("state"),
                col("Sport"), col("Dport"),
                col("Proto"),//col("dur"),
                col("SrcRate"), col("DstRate"),
                /*col("swin"), col("dwin"),
                col("smeansz"),col("dmeansz"),
                col("sttl"), col("dttl"),
                col("shops"),col("dhops"),*/
                col("probability"), col("prediction"),
                col("timestamp")};

        targetDf = targetDf.select(columns);


        /*String attacks[]={"normal","syn_flooding","ack_flooding",
        "host_bruteforce","http_flooding","udp_flooding",
        "arp_spoofing", "port_scanning", "scan_os",
        "scan_host_discovery"};*/

        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        StreamingQuery query = targetDf.writeStream()
//                .format("console")
//                .option("path", RESULT_FOLDER_PATH)
                .option("checkpointLocation", CHECKPOINT_LOCATION)
                .trigger(Trigger.ProcessingTime(QUERY_INTERVAL_SECONDS))
                .option("truncate", false)
                .foreachBatch(new VoidFunction2<Dataset<Row>, Long>() {
                    @Override
                    public void call(Dataset<Row> rowDataset, Long aLong) throws Exception {
                        rowDataset.write()
                                .format("mongo")
                                .mode("append")
                                .option("uri", MONGODB_OUTPUT_URI)
                                .save();
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        rowDataset.show(20);
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        //select("meetup.*")
                    }

                })
                .start();

        // MongoSpark.save(ddd);

        query.awaitTermination();
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
