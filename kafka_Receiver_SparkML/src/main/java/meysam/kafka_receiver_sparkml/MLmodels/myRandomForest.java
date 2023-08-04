package meysam.kafka_receiver_sparkml.MLmodels;

import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.RandomForestClassifier;
import org.apache.spark.ml.feature.StringIndexer;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.IOException;

public class myRandomForest {

    private String[] non_feature_cols;
    private String[] cat_cols;
    private String[] cat_cols_indexed;


    public myRandomForest(String[] cat_cols,String[] non_feature_cols) {
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

    public void Train(Dataset<Row> data) throws IOException {


        StringIndexer indexer = new StringIndexer();
        indexer.setHandleInvalid("keep");
        indexer.setInputCols(cat_cols)
                .setOutputCols(cat_cols_indexed);


        String feature_cols[] = (String[]) ArrayUtils.addAll(
                (data.drop(non_feature_cols)).columns()
                , indexer.getOutputCols());

//        String feature_cols[]=(data.drop(non_feature_cols)).columns();

        VectorAssembler vectorAssembler = new VectorAssembler()
                .setInputCols(feature_cols)
                .setOutputCol("features");

        RandomForestClassifier rf = new RandomForestClassifier();
        rf.setImpurity("gini");

        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{
                      indexer,vectorAssembler, rf
                });

        // I picked 10,100:
        ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(rf.maxDepth(), new int[]{10})//,10,12,14,16,18,20,30})
                .addGrid(rf.numTrees(), new int[]{100})//10,20,30})
                .build();


        myCrossValidator crossValidator=
                new myCrossValidator(paramGrid,pipeline,
                        2,"Random_Forest",false);
        crossValidator.crossValidate(data);
    }
}
