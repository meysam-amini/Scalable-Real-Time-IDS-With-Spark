package meysam.kafka_receiver_sparkml.MLmodels;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.NaiveBayes;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

public class myNaiveBayes {

    private String[] non_feature_cols;
    private String[] cat_cols;
    private String[] cat_cols_indexed;


    public myNaiveBayes(String[] cat_cols, String[] non_feature_cols) {
        this.non_feature_cols = non_feature_cols;
        this.cat_cols = cat_cols;
        init_cat_cols_indexed();
    }

    private void init_cat_cols_indexed() {
        cat_cols_indexed = new String[cat_cols.length];
        for (int i = 0; i < cat_cols.length; i++) {
            cat_cols_indexed[i] = cat_cols[i] + "_indexed";
        }
    }

    public void Train(Dataset<Row> data){


        StringIndexer indexer = new StringIndexer();
        indexer.setHandleInvalid("keep");
        indexer.setInputCols(cat_cols)
                .setOutputCols(cat_cols_indexed);


        String feature_cols[] = (String[]) ArrayUtils.addAll(
                (data.drop(non_feature_cols)).columns()
                , indexer.getOutputCols());

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(feature_cols)
                .setOutputCol("features")
                .setHandleInvalid("keep");

        /*MinMaxScaler scaler = new MinMaxScaler()
                .setInputCol(vectorAssembler.getOutputCol())
                .setOutputCol("scaledFeatures");*/

        StandardScaler scaler = new StandardScaler()
                .setInputCol(vectorAssembler.getOutputCol())
                .setOutputCol("scaledFeatures")
                .setWithStd(true)
                .setWithMean(false);

        NaiveBayes nb = new NaiveBayes()
                .setModelType("")
                .setFeaturesCol(scaler.getOutputCol());

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                        indexer, vectorAssembler,scaler, nb
                });

        // Training the data
        Dataset<Row>[] splitDF = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainDF = splitDF[0];
        Dataset<Row> evaluationDF = splitDF[1];

        // train the model
        PipelineModel model=pipeline.fit(trainDF);


        // compute accuracy and other metrics on the test set
        Dataset<Row> result = model.transform(evaluationDF);
        Dataset<Row> predictionAndLabels = result.select("prediction", "label");


        MulticlassClassificationEvaluator evaluator1 = new MulticlassClassificationEvaluator().setMetricName("accuracy");
        MulticlassClassificationEvaluator evaluator2 = new MulticlassClassificationEvaluator().setMetricName("weightedPrecision");
        MulticlassClassificationEvaluator evaluator3 = new MulticlassClassificationEvaluator().setMetricName("weightedRecall");
        MulticlassClassificationEvaluator evaluator4 = new MulticlassClassificationEvaluator().setMetricName("f1");
        String metrics[]=new String[4];
        metrics[0]="Accuracy = " + evaluator1.evaluate(predictionAndLabels);
        metrics[1]="Precision = " + evaluator2.evaluate(predictionAndLabels);
        metrics[2]="Recall = " + evaluator3.evaluate(predictionAndLabels);
        metrics[3]="F1 = " + evaluator4.evaluate(predictionAndLabels);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%  ALL METRICS  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i=0;i<4;i++)
            System.out.println(metrics[i]);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");


        result.show(false);
    }
}
