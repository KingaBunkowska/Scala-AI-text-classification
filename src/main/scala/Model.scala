import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{CountVectorizer, Tokenizer}
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.CountVectorizerModel

class Model(spark: SparkSession){

    private var model: NaiveBayesModel = _
    private var tokenizer: Tokenizer = _
    private var cvModel: CountVectorizerModel = _ 

    def train(texts: DataFrame, labels: DataFrame): Unit = {
        // Tokenizacja tekstu
        tokenizer = new Tokenizer()
        .setInputCol("text")
        .setOutputCol("words")
        val tokenized_df = tokenizer.transform(texts)

        // Definiowanie CountVectorizer
        val cv = new CountVectorizer()
        .setInputCol("words")
        .setOutputCol("features")
        .setVocabSize(10000)  // Rozmiar słownika
        .setMinDF(3)           // Minimalna liczba dokumentów, w których słowo musi wystąpić

        // Dopasowanie CountVectorizer do danych treningowych
        cvModel = cv.fit(tokenized_df)
        val featurized_df = cvModel.transform(tokenized_df)

        // Inicjalizacja modelu Naive Bayes
        val nb = new NaiveBayes()
        .setLabelCol("label")
        .setFeaturesCol("features")

        // Dopasowanie modelu Naive Bayes do danych treningowych
        val labeledData = featurized_df.join(labels, Seq("text"), "inner")
        model = nb.fit(labeledData)
    }

    def evaluate(test_X: DataFrame, test_Y: DataFrame): Double = {
        // Tokenizacja tekstu w danych testowych
        val tokenizedData = tokenizer.transform(test_X)
        val featurizedData = cvModel.transform(tokenizedData)

        // Predykcja na danych testowych
        val predictions = model.transform(featurizedData)

        // Ewaluacja modelu
        val evaluator = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
        val accuracy = evaluator.evaluate(predictions)

        accuracy
    }

    // def predict(text: String): Double = {
        
    //     val input = spark.createDataFrame(Seq(text))

    //     val tokenizedText = tokenizer.transform(input)
    //     val featurizedText = cvModel.transform(tokenizedText)

    //     // Predykcja na tekście
    //     val prediction: Double = model.predict(featurizedText.head().getAs[Vector]("features"))
    //     prediction
    // }
}