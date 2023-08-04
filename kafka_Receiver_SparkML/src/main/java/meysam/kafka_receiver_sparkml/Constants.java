package meysam.kafka_receiver_sparkml;

public class Constants {
    public static final String FILES_BASE_DIRECTORY="/home/meysam/Meysam_Workspace/projects/Scalable-Real-Time-IDS-With-Spark";
//    public static final String HADOOP_HOME_DIR_VALUE = FILES_BASE_DIRECTORY+"/winutils";
    public static final String MODELS_FOLDER_PATH = FILES_BASE_DIRECTORY+"/sparkml/models";
    public static final String RESULT_FOLDER_PATH = FILES_BASE_DIRECTORY+"sparkml/results";
    public static final String CHECKPOINT_LOCATION = FILES_BASE_DIRECTORY+"/sparkml/checkpoints";
    public static final String RUN_LOCAL_WITH_AVAILABLE_CORES = "local[*]";
    public static final String MASTER_URI = "spark://192.168.225.181:7077";

    //kafka
    public static final String KAFKA_BROKERS = "localhost:9092";

}
