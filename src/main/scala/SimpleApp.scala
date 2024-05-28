import org.apache.spark.sql.SparkSession

object SimpleApplication {
  def main(args: Array[String]): Unit = {
    // Spark config
    val spark = SparkSession.builder()
      .appName("Simple Application")
      .master("local")
      .getOrCreate()

    val dataset = new Dataset(spark)
    dataset.load("dataset/AI_Human_text.csv")

    // Show the first row of the DataFrame
    dataset.df.show(10)
    println(dataset.df.take(1001).tail(1).getString(0))

    val (train_df, test_df) = dataset.split(0.3, 42)

    println("Train")
    println(train_df.count())
    println("Test")
    println(test_df.count())
    println("All")
    println(dataset.df.count())

    // End Spark session
    spark.stop()
  }
}