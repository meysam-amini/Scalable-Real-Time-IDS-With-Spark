package meysam.kafka_receiver_sparkml.MLmodels;

import meysam.kafka_receiver_sparkml.utils.MyFile;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator;
import org.apache.spark.ml.evaluation.Evaluator;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.CrossValidator;
import org.apache.spark.ml.tuning.CrossValidatorModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

import static meysam.kafka_receiver_sparkml.Constants.*;

public class myCrossValidator {

    private String MODEL_FOLDER_PATH = MODELS_FOLDER_PATH;
    public static final String OUTPUT_FILE = ".\\src\\main\\resources\\outputs\\outputs.txt";
    private boolean isBinaryClassification = false;
    private Evaluator evaluator;

    private ParamMap[] paramGrid;
    private Pipeline pipeline;
    private Long seed = 1234L;
    private int model_stage_in_pipe = 0;
    private String model_name = "";


    public myCrossValidator(ParamMap[] paramGrid, Pipeline pipeline
            , int model_stage_in_pipe, String model_name, boolean isBinaryClassification) {
        this.paramGrid = paramGrid;
        this.pipeline = pipeline;
        this.model_stage_in_pipe = model_stage_in_pipe;
        this.model_name = model_name;
        this.isBinaryClassification = isBinaryClassification;
    }

    public void crossValidate(Dataset<Row> data) throws IOException {


        if (isBinaryClassification) {
            evaluator =
                    new BinaryClassificationEvaluator();
            ((BinaryClassificationEvaluator) evaluator).setMetricName("areaUnderROC");
            MODEL_FOLDER_PATH += "/" + "binary_classification";
        } else {
            evaluator =
                    new MulticlassClassificationEvaluator();
            ((MulticlassClassificationEvaluator) evaluator).setMetricName("accuracy");
            MODEL_FOLDER_PATH += "/" + "multi_classification";
        }

        CrossValidator cv = new CrossValidator()
                .setEstimator(pipeline)
                .setEvaluator(evaluator)
                .setEstimatorParamMaps(paramGrid)
                .setSeed(seed)
                .setNumFolds(5)  // Use 3+ in practice
                .setParallelism(4);  // Evaluate up to 2 parameter settings in parallel


        Dataset<Row>[] splitDF = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainDF = splitDF[0];
        Dataset<Row> evaluationDF = splitDF[1];

        MyFile.SetFileAdress(OUTPUT_FILE);

        Long t1 = System.currentTimeMillis();
        // Run cross-validation, and choose the best set of parameters.
        CrossValidatorModel cvModel = cv.fit(trainDF);

        Long t2 = System.currentTimeMillis();


        print("@@@@@@@ " + model_name + " @@@@@@@");

        print("train_time: " + (t2 - t1));

        for (int i = 0; i < cvModel.avgMetrics().length; i++)
            print("Avg[" + i + "] : " + cvModel.avgMetrics()[i]);


        print("##################### BEST MODEL PARAMS #####################");
        print(((PipelineModel) cvModel.bestModel())
                .stages()[model_stage_in_pipe].explainParams());


        // compute accuracy and other metrics on the test set
        Dataset<Row> result = cvModel.bestModel().transform(evaluationDF);
        if (isBinaryClassification)
            evaluateBinary(result);
        else
            evaluateMultiClass(result);

        ((PipelineModel) cvModel.bestModel()).write().overwrite().save(MODEL_FOLDER_PATH + "\\" + model_name);

        Dataset<Row> predictionsDF = cvModel.bestModel().transform(data);
        predictionsDF.show(false);
    }

    private void evaluateBinary(Dataset<Row> result) throws IOException {
        Dataset<Row> predictionAndLabels = result.select("rawPrediction", "label");

        BinaryClassificationEvaluator evaluator1 = new BinaryClassificationEvaluator().setMetricName("areaUnderROC");
        String metrics[] = new String[1];
        metrics[0] = "areaUnderROC = " + evaluator1.evaluate(predictionAndLabels);
        print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  ALL BINARY METRICS  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < metrics.length; i++)
            print(metrics[i]);
        print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

    }

    private void evaluateMultiClass(Dataset<Row> result) throws IOException {
        Dataset<Row> predictionAndLabels = result.select("prediction", "label");

        MulticlassClassificationEvaluator evaluator1 = new MulticlassClassificationEvaluator().setMetricName("accuracy");
        MulticlassClassificationEvaluator evaluator2 = new MulticlassClassificationEvaluator().setMetricName("weightedPrecision");
        MulticlassClassificationEvaluator evaluator3 = new MulticlassClassificationEvaluator().setMetricName("weightedRecall");
        MulticlassClassificationEvaluator evaluator4 = new MulticlassClassificationEvaluator().setMetricName("f1");
        String metrics[] = new String[4];
        metrics[0] = "Accuracy = " + evaluator1.evaluate(predictionAndLabels);
        metrics[1] = "Precision = " + evaluator2.evaluate(predictionAndLabels);
        metrics[2] = "Recall = " + evaluator3.evaluate(predictionAndLabels);
        metrics[3] = "F1 = " + evaluator4.evaluate(predictionAndLabels);
        print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  ALL MULTICLASS METRICS  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < metrics.length; i++)
            print(metrics[i]);
        print("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

    }

    public static void print(String s) throws IOException {
        MyFile.add(s);
    }
}
