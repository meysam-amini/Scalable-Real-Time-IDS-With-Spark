package meysam.kafka_receiver_sparkml.MLmodels;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class myLogisticRegression {

    private String[] cat_cols;
    private String[] cat_cols_indexed;
    private String[] cat_cols_one_hoted;
    private String[] non_feature_cols;

    public myLogisticRegression(String[] cat_cols, String[] non_feature_cols) {
        this.cat_cols = cat_cols;
        this.non_feature_cols = non_feature_cols;
        init_cat_cols_indexed();
    }

    private void init_cat_cols_indexed() {
        cat_cols_indexed = new String[cat_cols.length];
        cat_cols_one_hoted = new String[cat_cols.length];
        for (int i = 0; i < cat_cols.length; i++) {
            cat_cols_indexed[i] = cat_cols[i] + "_indexed";
            cat_cols_one_hoted[i] = cat_cols[i] + "_one_hoted";
        }
    }

    public void Train(Dataset<Row> data) throws IOException {


        StringIndexer indexer = new StringIndexer();
        indexer.setHandleInvalid("keep");

        indexer.setInputCols(cat_cols)
                .setOutputCols(cat_cols_indexed);


        OneHotEncoder encoder = new OneHotEncoder()
                .setInputCols(indexer.getOutputCols())
                .setOutputCols(cat_cols_one_hoted);


        String feature_cols[] = (String[]) ArrayUtils.addAll(
                (data.drop(non_feature_cols)).columns()
                , encoder.getOutputCols());

//        String feature_cols[]=(data.drop(non_feature_cols)).columns();

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(feature_cols)
                .setOutputCol("features");

        StandardScaler scaler = new StandardScaler()
                .setInputCol("features")
                .setOutputCol("scaledFeatures")
                .setWithStd(true)
                .setWithMean(false);

        LogisticRegression logisticRegression =
                new LogisticRegression().setFeaturesCol("scaledFeatures");

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                        indexer, encoder,
                        vectorAssembler, scaler, logisticRegression
                });


        // Training the data
        Dataset<Row>[] splitDF = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainDF = splitDF[0];
        Dataset<Row> evaluationDF = splitDF[1];

        //after cv I picked 0.01:
        ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(logisticRegression.regParam(), new double[]{0.01})//,0.1, 1.0, 10,20,50, 100})
                .build();

        myCrossValidator crossValidator=
                new myCrossValidator(paramGrid,pipeline,
                        4,"Logistic_Regression",false);
        crossValidator.crossValidate(data);


//        MulticlassClassificationEvaluator
//                binaryClassificationEvaluator = (MulticlassClassificationEvaluator) cvModel.getEvaluator();;


//        String metricName = binaryClassificationEvaluator.getMetricName();
//        boolean largerBetter = binaryClassificationEvaluator.isLargerBetter();
//        double evalValue = binaryClassificationEvaluator.evaluate(evaluationDF);
//
//        System.out.println("\n\nBinary Classification Evaluator:\n\nMetric name: " +
//                metricName + "\nIs larger better?: " + largerBetter + "\nValue: " + evalValue);

        // Save the model on disk
        //   pipelineModel.write().overwrite().save(MODEL_FOLDER_PATH);
    }
}
