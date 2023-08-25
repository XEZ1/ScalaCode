object EvilWordleGame {

  import io.Source
  import scala.util._

  def get_wordle_list_from_url(url: String) : List[String] = {
    try {
      val data = io.Source.fromURL(url)
      val secrets = data.getLines().toList
      data.close()
      secrets
    } catch {
      case e: Exception => Nil
    }
  }

  def get_wordle_list_from_file(fileName: String): List[String] = {
    try {
      val data = io.Source.fromFile(fileName)
      val secrets = data.getLines().toList
      data.close()
      secrets
    } catch {
      case e: Exception => Nil
    }
  }

  val secrets = get_wordle_list_from_file("words.txt")
  // print(secrets)
  // secrets.length // => 12972
  // secrets.filter(_.length != 5) // => Nil

  def removeN[A](xs: List[A], elem: A, n: Int) : List[A] = {
    n match {
      case 0 => xs
      case _ => {
        val idx = xs.indexOf(elem)
        idx match {
          case -1 => xs
          case _ => {
            val (first, last) = xs.splitAt(idx)
            removeN(first ++ last.tail, elem, n - 1)
          }
        }
      }
    }
  }

  // removeN(List(1,2,3,2,1), 3, 1)  // => List(1, 2, 2, 1)
  // removeN(List(1,2,3,2,1), 2, 1)  // => List(1, 3, 2, 1)
  // removeN(List(1,2,3,2,1), 1, 1)  // => List(2, 3, 2, 1)
  // removeN(List(1,2,3,2,1), 0, 2)  // => List(1, 2, 3, 2, 1)

  abstract class Tip
  case object Absent extends Tip
  case object Present extends Tip
  case object Correct extends Tip

  def pool(secret: String, word: String) : List[Char] = secret.indices.toList.filter(characterIDX => secret(characterIDX) != word(characterIDX)).map(characterIDX => secret(characterIDX))

  def aux_recursive_helper(secret: List[Char], word: List[Char], pool: List[Char], idx: Int = 0): List[Tip] = {
    if (idx == secret.length) Nil else
      if (secret(idx) == word(idx)) Correct :: aux_recursive_helper(secret, word, pool, idx + 1)
      else if (pool.contains(word(idx))) {
        val (first, last) = pool.splitAt(pool.indexOf(word(idx)))
        Present :: aux_recursive_helper(secret, word, first ++ last.tail, idx + 1)
      } else Absent :: aux_recursive_helper(secret, word, pool, idx + 1)

  }

  def aux(secret: List[Char], word: List[Char], pool: List[Char]) : List[Tip] = aux_recursive_helper(secret, word, pool)

  def score(secret: String, word: String) : List[Tip] = aux(secret.toList, word.toList, pool(secret, word))


  // score("chess", "caves") // => List(Correct, Absent, Absent, Present, Correct)
  // score("doses", "slide") // => List(Present, Absent, Absent, Present, Present)
  // score("chess", "swiss") // => List(Absent, Absent, Absent, Correct, Correct)
  // score("chess", "eexss") // => List(Present, Absent, Absent, Correct, Correct)

  def eval(t: Tip) : Int = if (t==Correct) 10 else if (t==Present) 1 else 0

  def iscore(secret: String, word: String) : Int = score(secret, word).map(eval).sum

  //iscore("chess", "caves") // => 21
  //iscore("chess", "swiss") // => 20

  def lowest_recursive_helper(secrets: List[String], word: String, idx: Int = 0, current: Int, lstFinal: List[String]): List[String] = {
    if (secrets.length == idx) lstFinal else {
      if (iscore(secrets(idx), word) < current) lowest_recursive_helper(secrets, word, idx + 1, iscore(secrets(idx), word), secrets(idx) :: Nil) else if (iscore(secrets(idx), word) == current) lowest_recursive_helper(secrets, word, idx + 1, current, lstFinal :+ secrets(idx)) else lowest_recursive_helper(secrets, word, idx + 1, current, lstFinal)
    }
  }

  def lowest(secrets: List[String], word: String, current: Int, acc: List[String]) : List[String] = {
    lowest_recursive_helper(secrets, word, 0, current, acc)
  }

  def evil(secrets: List[String], word: String) = lowest(secrets, word, Int.MaxValue, Nil)


  //evil(secrets, "stent").length
  //evil(secrets, "hexes").length
  //evil(secrets, "horse").length
  //evil(secrets, "hoise").length
  //evil(secrets, "house").length

  def frequencies(secrets: List[String]) : Map[Char, Double] = {
    val sorted = secrets.flatten.groupBy(value => value)
    sorted.map(value => (value._1, 1-value._2.length.toDouble/secrets.flatten.length))
  }

  def rank(frqs: Map[Char, Double], s: String) = s.map(frqs).sum

  def ranked_evil(secrets: List[String], word: String) = {
    val evilVal = evil(secrets, word)
    val frequenciesVal = frequencies(secrets)
    val evalByRank = evilVal.map(x => (x, rank(frequenciesVal, x)))
    val maximumEvalByRank = evalByRank.maxBy(_._2)._2
    val filterdSecondEvalByRank = evalByRank.filter(x => x._2 == maximumEvalByRank)
    val toReturn = filterdSecondEvalByRank.map(_._1)
    toReturn
  }
}
