import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class DatasetHandler(spark: SparkSession) {
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

  def split(testSize: Double, seed: Long): (DataFrame, DataFrame) = {
    val Array(trainingData, testData) = df.randomSplit(Array(1 - testSize, testSize), seed)
    (trainingData, testData)
  }
}
