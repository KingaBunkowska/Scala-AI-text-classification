import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{CountVectorizer, Tokenizer}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.DataFrame
import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.{col, split}
import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.functions._

class Model(spark: SparkSession){

    private var model: NaiveBayesModel = _
    private var tokenizer: Tokenizer = _
    private var cvModel: CountVectorizerModel = _ 

    def train(texts: DataFrame): Unit = {
        import spark.implicits._

        tokenizer = new Tokenizer().setInputCol("text").setOutputCol("tokenizedWords")

        var tokenizedData = tokenizer
                            .transform(texts)

        // println("TokenizedData")
        // tokenizedData.columns.foreach(println)

        val cv = new CountVectorizer()
        .setInputCol("tokenizedWords")
        .setOutputCol("features")
        .setVocabSize(10000)
        .setMinDF(3)           

        cvModel = cv.fit(tokenizedData.select("tokenizedWords"))

        val featurizedDF = cvModel.transform(tokenizedData)
        // println("FeaturizedDF")
        // featurizedDF.columns.foreach(println)
        

        val nb = new NaiveBayes()
        .setLabelCol("generated")
        .setFeaturesCol("features")
        .setPredictionCol("prediction")

        model = nb.fit(featurizedDF)


        val predictions = model.transform(featurizedDF)
        // predictions.show(5)

        val evaluator = new MulticlassClassificationEvaluator()
        .setLabelCol("generated")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
        val accuracy = evaluator.evaluate(predictions)
        println(s"Training accuracy: $accuracy")


}


    def evaluate(test: DataFrame): Double = {
        val featurizedDF = cvModel.transform(tokenizer.transform(test))
        val predictions = model.transform(featurizedDF)

        val evaluator = new MulticlassClassificationEvaluator()
        .setLabelCol("generated")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
        val accuracy = evaluator.evaluate(predictions)
        accuracy
    }

    def predict(text: String): Double = {

        val removePunctuation = udf((text: String) => {
            if (text != null) text.replaceAll("""[\p{Punct}]""", "") else null
            })


        val schema = StructType(Array(StructField("text", StringType, true)))
        val rdd = spark.sparkContext.parallelize(Seq(Row(text)))
        var input = spark.createDataFrame(rdd, schema)

        input = input.withColumn("text", lower(col("text")))
                .withColumn("text", removePunctuation(col("text")))
                .withColumn("text", regexp_replace(col("text"), "\n", ""))

        val featurizedDF = cvModel.transform(tokenizer.transform(input))
        // featurizedDF.show(1)
        val predictions = model.transform(featurizedDF)
        // predictions.show(1)

        predictions.first().getAs[Double]("prediction")
    }
}