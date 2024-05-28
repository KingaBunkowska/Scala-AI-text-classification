import org.apache.spark.sql.SparkSession

object SimpleApplication {
  def main(args: Array[String]): Unit = {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    val datasetHandler = new DatasetHandler(spark)
    datasetHandler.load("dataset/AI_Human_text.csv")

    // Show the first row of the DataFrame
    datasetHandler.df.show(10)
    println(datasetHandler.df.take(1001).tail(1).getString(0))

    // End Spark session
    spark.stop()
  }
}