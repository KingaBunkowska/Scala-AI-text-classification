import org.apache.spark.sql.SparkSession

object SimpleApplication {
  def main(args: Array[String]): Unit = {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    // Load CSV file
    val df = spark.read
        .format("csv")
        .option("header", "true")
        .option("quote", "\"")
        .option("escape", "\"")
        .option("sep", ",")
        .option("multiLine", "true")
        .load("dataset/AI_Human_text.csv")

    // Show the first row of the DataFrame
    df.show(2000)

    // End Spark session
    spark.stop()
  }
}