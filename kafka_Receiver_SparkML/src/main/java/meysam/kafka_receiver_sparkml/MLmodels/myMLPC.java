package meysam.kafka_receiver_sparkml.MLmodels;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.feature.OneHotEncoder;
import org.apache.spark.ml.feature.StandardScaler;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class myMLPC {

    private String[] cat_cols;
    private String[] cat_cols_indexed;
    private String[] cat_cols_one_hoted;
    private String[] non_feature_cols;

    public myMLPC(String[] cat_cols, String[] non_feature_cols) {
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


//        'error': throws an exception (which is the default)
//        'skip': skips the rows containing the unseen labels entirely (removes the rows on the output!)
//        'keep': puts unseen labels in a special additional bucket, at index numLabels


        StringIndexer indexer = new StringIndexer();
        indexer.setHandleInvalid("skip");
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
                .setInputCol(vectorAssembler.getOutputCol())
                .setOutputCol("scaledFeatures")
                .setWithStd(true)
                .setWithMean(false);

        int num_classes=2;

        int[] layers = new int[] {21,20,30,10, num_classes};
        MultilayerPerceptronClassifier mlpc = new MultilayerPerceptronClassifier()
                .setFeaturesCol("scaledFeatures")
                .setLayers(layers)
                .setBlockSize(32)
                .setSeed(1234L);

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                        indexer, encoder,
                        vectorAssembler, scaler, mlpc
                });

        ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(mlpc.maxIter(), new int[]{200})//, 0.1, 1.0, 10,20,50, 100})
                .build();

        myCrossValidator crossValidator=
                new myCrossValidator(paramGrid,pipeline,
                        4,"MLPC",true);
        crossValidator.crossValidate(data);


        /*// Training the data
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
*/
        // Save the model on disk
        //   pipelineModel.write().overwrite().save(MODEL_FOLDER_PATH);
    }
}
