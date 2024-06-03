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

    def train(texts: DataFrame, labels: DataFrame): Unit = {

        import spark.implicits._
        
        // var train_texts = texts.withColumn("words", lit(null: String))
        // var train_texts = texts
        // println("Dostępne kolumny w DataFrame:")
        // train_texts.columns.foreach(println) // czyli jest kolumna text
        // println(train_texts.take(1).tail(1).getString(0)) // pierwszy tekst

        tokenizer = new Tokenizer().setInputCol("text").setOutputCol("tokenizedWords")

        var tokenizedData = tokenizer.transform(texts.select("text"))
        // tokenizedData.show(3)

        // tokenizedData.select("tokenizedWords").show(3)

        val cv = new CountVectorizer()
        .setInputCol("tokenizedWords")
        .setOutputCol("features")
        .setVocabSize(10000)  // Rozmiar słownika
        .setMinDF(3)           

        cvModel = cv.fit(tokenizedData.select("tokenizedWords"))

        val featurized_df = cvModel.transform(tokenizedData.select("tokenizedWords")).show(6)

        // val firstRow_2:Row = featurized_df.select("words").head()
        // println("A teraz drugi leci dziki kod")
        // println(firstRow_2)


}


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