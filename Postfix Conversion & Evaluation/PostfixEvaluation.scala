object PostfixEvaluation {

  // type of tokens
  type Toks = List[String]

  // helper function for splitting strings into tokens
  def split(s: String) : Toks = s.split(" ").toList

  // left- and right-associativity
  abstract class Assoc
  case object LA extends Assoc
  case object RA extends Assoc

  // power is right-associative,
  // everything else is left-associative
  def assoc(s: String) : Assoc = s match {
    case "^" => RA
    case _ => LA
  }

  // the precedences of the operators
  val precs = Map("+" -> 1,
    "-" -> 1,
    "*" -> 2,
    "/" -> 2,
    "^" -> 4)

  // the operations in the basic version of the algorithm
  val ops = List("+", "-", "*", "/")
  val power = List("^")

  def is_op(op: String) : Boolean = ops.contains(op)

  def is_op_power(op: String) : Boolean = power.contains(op)

  def prec(op1: String, op2: String) : Boolean = {
    val operatorsType1 = precs.getOrElse(op1, 0)
    val operatorsType2 = precs.getOrElse(op2, 0)
    operatorsType1 > operatorsType2
  }

  def precForPower(op1: String, op2: String) : Boolean = {
    val operatorsType1 = precs.getOrElse(op1, 0)
    val operatorsType2 = precs.getOrElse(op2, 0)
    operatorsType1 >= operatorsType2
  }

  def syard(toks: Toks, st: Toks = Nil, out: Toks = Nil) : Toks = {
    toks match {
      case Nil => (out ++ st.reverse)
      case x :: xs if (x.forall(Character.isDigit)) => syard(xs, st, out :+ x)
      case x :: xs if (is_op(x) && (st.isEmpty || st.last == "(" || prec(x, st.last))) => syard(xs, st :+ x, out)
      case x :: xs if (is_op(x) && !prec(x, st.last)) => syard(xs, st.dropRight(1) :+ x, out :+ st.takeRight(1).head)
      case x :: xs if (x == "(") => syard(xs, st :+ x, out)
      case x :: xs if (x == ")") => syard(xs, st.reverse.dropWhile(_ != "(").reverse.dropRight(1), {if (st.reverse.takeWhile(_ != "(").nonEmpty) {(out :+ (st.reverse.takeWhile(_ != "(")).head)} else out})
      case x :: xs if (is_op_power(x) && (st.isEmpty || st.last == "(" || precForPower(x, st.last))) => syard(xs, st :+ x, out)
      case x :: xs if (is_op_power(x) && !precForPower(x, st.last)) => syard(xs, st.dropRight(1) :+ x, out :+ st.takeRight(1).head)
      case x :: xs => syard(xs, st, out)
    }
  }


// test cases
// syard(split("3 + 4 * 8 / ( 5 - 1 ) ^ 2 ^ 3"))  // 3 4 8 * 5 1 - 2 3 ^ ^ / +

  def compute(toks: Toks, st: List[Int] = Nil) : Int = {
    toks.foldLeft(List[Int]())(
      (st, toEvaluate) => (st, toEvaluate) match {
        case (a :: b :: rest, "^") => (BigInt(b).pow(a).toInt) :: rest
        case (a :: b :: rest, "+") => (b + a) :: rest
        case (a :: b :: rest, "-") => (b - a) :: rest
        case (a :: b :: rest, "*") => (b * a) :: rest
        case (a :: b :: rest, "/") => (b / a) :: rest
        case (_, _) => java.lang.Integer.parseInt(toEvaluate) :: st
      }).head
  }


// test cases
// compute(syard(split("3 + 4 * ( 2 - 1 )")))   // 7
// compute(syard(split("10 + 12 * 33")))       // 406
// compute(syard(split("( 5 + 7 ) * 2")))      // 24
// compute(syard(split("5 + 7 / 2")))          // 8
// compute(syard(split("5 * 7 / 2")))          // 17
// compute(syard(split("9 + 24 / ( 7 - 3 )"))) // 15
// compute(syard(split("4 ^ 3 ^ 2")))      // 262144
// compute(syard(split("4 ^ ( 3 ^ 2 )")))  // 262144
// compute(syard(split("( 4 ^ 3 ) ^ 2")))  // 4096
// compute(syard(split("( 3 + 1 ) ^ 2 ^ 3")))   // 65536
}

