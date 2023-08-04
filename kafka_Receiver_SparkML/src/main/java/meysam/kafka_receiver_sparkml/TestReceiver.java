package meysam.kafka_receiver_sparkml;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.lang.model.type.ArrayType;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.*;

@SpringBootApplication
public class TestReceiver {

    private static final String MONGODB_OUTPUT_URI = "mongodb://localhost/NetFlow.NetFlow";


    private static final String HADOOP_HOME_DIR_VALUE = "C:/winutils";

        private static final String RUN_LOCAL_WITH_AVAILABLE_CORES = "local[*]";
        private static final String APPLICATION_NAME = "Spark ML Scoring Online";
        private static final String CASE_SENSITIVE = "false";
		
        private static final String MODEL_FOLDER_PATH = "E:\\spark-ml\\model";
        private static final String RESULT_FOLDER_PATH = 
            "E:\\spark-ml\\results";
	
	private static final String KAFKA_FORMAT = "kafka";
	private static final String KAFKA_BROKERS = "localhost:9092";
	private static final String KAFKA_TOPIC = "NetFlow";
	
	private static final String JSON_FORMAT = "json";
	private static final String CHECKPOINT_LOCATION = "E://countck";
	private static final String QUERY_INTERVAL_SECONDS = "10 seconds";


    public static void main(String[] args) throws InterruptedException, StreamingQueryException, TimeoutException {
        SpringApplication.run(TestReceiver.class, args);

        System.setProperty("hadoop.home.dir", HADOOP_HOME_DIR_VALUE);

        // * the schema can be written on disk, and read from disk
        // * the schema is not mandatory to be complete, it can contain only the needed fields    

        //* the schema can be written on disk, and read from disk
        // * the schema is not mandatory to be complete, it can contain only the needed fields

        StructType ARGUS_SCHEMA =
                new StructType()

                        /* as argus doesn't send numbers in right format, we
                         should define numeric fields also in String type: */

                        .add("saddr", StringType, true).add("daddr", StringType, true)
                        .add("smac", StringType, true).add("dmac", StringType, true)
                        .add("sport", StringType, true).add("dport", StringType, true)
                        .add("state", StringType, true).add("proto", StringType, true)
                        .add("dur", StringType, true).add("srate", StringType, true)
                        .add("drate", StringType, true).add("swin", StringType, true)
                        .add("dwin", StringType, true).add("smeansz", StringType, true)
                        .add("dmeansz", StringType, true).add("sttl", StringType, true)
                        .add("dttl", StringType, true).add("shops", StringType, true)
                        .add("dhops", StringType, true).add("label", IntegerType, false);

        initStatesMap();

        MongoClient mongo = MongoClients.create();
        MongoTemplate template = new MongoTemplate( mongo,"NetFlow");
        try {
            CollectionOptions options = CollectionOptions.empty()
                    .capped().size(2000000000L)
                    .maxDocuments(2000000000L);
            template.createCollection("NetFlow",options);
        } catch (Exception e) {
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("Collection Exists");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.err.println("::::::::::::::::::::::::::::::::::::::::::::::::::::");

        }





        final SparkConf conf = new SparkConf()
                .setMaster(RUN_LOCAL_WITH_AVAILABLE_CORES)
                .setAppName(APPLICATION_NAME)
                .set("spark.mongodb.output.uri", MONGODB_OUTPUT_URI)///???
//                .set("spark.sql.jsonGenerator.ignoreNullFields","false")
                .set("spark.sql.caseSensitive", CASE_SENSITIVE);

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        PipelineModel pipelineModel = PipelineModel.load(MODEL_FOLDER_PATH);
       
        Dataset<Row> gatheredDF = spark.readStream()
                .format(KAFKA_FORMAT)
                .option("kafka.bootstrap.servers", KAFKA_BROKERS)
                .option("subscribe", KAFKA_TOPIC)
//                .option("includeHeaders", "true")
                // .option("failOnDataLoss" , "false")
                .load()
            //    .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)", "headers")
                .select(col("timestamp"),
                from_json(col("value").cast("string"), ARGUS_SCHEMA)
                        .alias("NetFlow"))
                .alias("data")
                .select("data.timestamp","data.NetFlow.*");

        gatheredDF.printSchema();

        ///////////new codes
        // Data Preparation



//        gatheredDF.select(col("udf")).foreach(v1 -> {
//            System.out.println("#########################################");
//            for (int i=0;i<16;i++)
//            System.out.print(v1.getList(0).toArray()[i]);
//        });



        ///////////end new codes

        /*String attacks[]={"normal","syn_flooding","ack_flooding",
        "host_bruteforce","http_flooding","udp_flooding",
        "arp_spoofing", "port_scanning", "scan_os",
        "scan_host_discovery"};*/

        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.err.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        StreamingQuery query = gatheredDF.writeStream()
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
                                .option("uri",MONGODB_OUTPUT_URI)
                                .save();
////
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//                        rowDataset.select(col("headers").getItem("value")).show(20);
                        rowDataset.show(20);
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        //select("meetup.*")
                    }

                })
                .start();

       // MongoSpark.save(ddd);

       // Dataset<Row> filtered = dataset.filter(dataset.col(highWaterCol).$greater(old)).persist();
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
