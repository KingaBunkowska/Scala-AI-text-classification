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

    val (train, test) = dataset.split(0.3, 42)

    val model = new Model(spark)

    model.train(train)
    println("Evalutation")
    println(model.evaluate(test))

    println("Prediction")
    println(model.predict("""Scala is a coding language short for “Scalable Language.” Many professionals consider Scala to be a modern version of Java, which is another popular programming language. In this article, we’ll explore what Scala is, when you might choose to use it, how it differs from Java, and the steps you can take to learn this exciting language.
                          While Java has established itself as a top programming language over the years, you might choose to use Scala to address some of Java’s limitations. Scala is a dynamic programming language that easily integrates with the Java platform, yet it offers superior performance, user-friendliness, and coding efficiency. Scala is also open-source, which removes obstacles associated with licensing constraints.

                          As a data scientist or data engineer, you might gravitate toward Scala because of its intrinsic connection with big data. Platforms such as Apache Spark, which is widely used in data analytics and machine learning, use Scala. The straightforward nature of Scala, combined with its inherent type safety, provides an environment that can be beneficial for developing complex data-intensive applications. 

                          Scala is a concise and scalable language compared to many alternatives. As a software programmer, you might choose Scala for the ability to move through software development and testing phases more quickly than when using other languages. Scala is also considered to be flexible when making changes, which can further improve your program development process."""))

    // End Spark session
    spark.stop()
  }
}