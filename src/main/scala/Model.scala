import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{CountVectorizer, Tokenizer}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.CountVectorizerModel
import org.apache.spark.sql.functions.lit
import org.apache.spark.sql.functions.{col, split}
import org.apache.spark.ml.feature.{Tokenizer}
import org.apache.spark.sql.{SparkSession, Row}

class Model(spark: SparkSession){

    private var model: NaiveBayesModel = _
    private var tokenizer: Tokenizer = _
    private var cvModel: CountVectorizerModel = _ 

    def train(texts: DataFrame): Unit = {
        import spark.implicits._

        println("Texts")
        texts.columns.foreach(println)

        tokenizer = new Tokenizer().setInputCol("text").setOutputCol("tokenizedWords")

        var tokenizedData = tokenizer
                            // .transform(texts.select("text"))
                            .transform(texts)
                            // .withColumn("index", monotonically_increasing_id())
        tokenizedData.show(3)

        println("TokenizedData")
        tokenizedData.columns.foreach(println)

        val cv = new CountVectorizer()
        .setInputCol("tokenizedWords")
        .setOutputCol("features")
        .setVocabSize(10000)  // Rozmiar słownika
        .setMinDF(3)           

        cvModel = cv.fit(tokenizedData.select("tokenizedWords"))


        // val featurizedDF = cvModel.transform(tokenizedData.select("tokenizedWords"))
        val featurizedDF = cvModel.transform(tokenizedData)
        println("FeaturizedDF")
        featurizedDF.columns.foreach(println)
        

        val nb = new NaiveBayes()
        .setLabelCol("generated")
        .setFeaturesCol("features")
        .setPredictionCol("prediction")

        model = nb.fit(featurizedDF)

        // model.save("model")

        val predictions = model.transform(featurizedDF)
        predictions.show(5)


}

    // def load(filePath: String): Unit = {
    //     model = NaiveBayesModel.load("model")
    // }

    // }

    // def evaluate(test_X: DataFrame, test_Y: DataFrame): Double = {
    //     // Tokenizacja tekstu w danych testowych
    //     val tokenizedData = tokenizer.transform(test_X)
    //     val featurizedData = cvModel.transform(tokenizedData)

    //     // Predykcja na danych testowych
    //     val predictions = model.transform(featurizedData)

    //     // Ewaluacja modelu
    //     val evaluator = new MulticlassClassificationEvaluator()
    //     .setLabelCol("label")
    //     .setPredictionCol("prediction")
    //     .setMetricName("accuracy")
    //     val accuracy = evaluator.evaluate(predictions)

    //     accuracy
    // }

    // def predict(text: String): Double = {
        
    //     val input = spark.createDataFrame(Seq(text))

    //     val tokenizedText = tokenizer.transform(input)
    //     val featurizedText = cvModel.transform(tokenizedText)

    //     // Predykcja na tekście
    //     val prediction: Double = model.predict(featurizedText.head().getAs[Vector]("features"))
    //     prediction
    // }
}