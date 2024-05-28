import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class Dataset(spark: SparkSession) {
  var df: DataFrame = _ 
  
  def load(filePath: String): Unit = {

    val removePunctuation = udf((text: String) => {
    if (text != null) text.replaceAll("""[\p{Punct}]""", "") else null
    })

    df = spark.read
      .format("csv")
      .option("header", true)
      .option("inferSchema", true)
      .option("sep", ",")
      .option("quote", "\"")
      .option("escape", "\"")
      .option("multiLine", true)
      .load(filePath)

    df = df.withColumn("text", lower(col("text")))
      .withColumn("text", removePunctuation(col("text")))
  }

  def split(testSize: Double, seed: Long): (DataFrame, DataFrame, DataFrame, DataFrame) = {
    val Array(trainingData, testData) = df.randomSplit(Array(1 - testSize, testSize), seed)

    val training_X = trainingData.select("text")
    val training_Y = trainingData.select("generated")

    val test_X = testData.select("text")
    val test_Y = testData.select("generated")

    (training_X, training_Y, test_X, test_Y)
  }
}
