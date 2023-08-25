object PostfixConversion {

	// type of tokens
	type Toks = List[String]

	// the operations in the basic version of the algorithm
	val ops = List("+", "-", "*", "/")

	// the precedences of the operators
	val precs = Map(
		"+" -> 1,
		"-" -> 1,
		"*" -> 2,
		"/" -> 2
	)

	// helper function for splitting strings into tokens
	def split(s: String) : Toks = s.split(" ").toList

	def is_op(op: String) : Boolean = ops.contains(op)

	def prec(op1: String, op2: String) : Boolean = {
		val operatorsType1 = precs.getOrElse(op1, 0)
		val operatorsType2 = precs.getOrElse(op2, 0)
		operatorsType1 > operatorsType2
	}

	def syard(toks: Toks, st: Toks = Nil, out: Toks = Nil) : Toks = {
		toks match {
			case Nil => (out ++ st.reverse)
			case x :: xs if (x.forall(Character.isDigit)) => syard(xs, st, out :+ x)
			case x :: xs if (is_op(x) && (st.isEmpty || st.last == "(" || prec(x, st.last))) => syard(xs, st :+ x, out)
			case x :: xs if (is_op(x) && !prec(x, st.last)) => syard(xs, st.dropRight(1) :+ x, out :+ st.takeRight(1).head)
			case x :: xs if (x == "(") => syard(xs, st :+ x, out)
			case x :: xs if (x == ")") => syard(xs, st.reverse.dropWhile(_ != "(").reverse.dropRight(1), {if (st.reverse.takeWhile(_ != "(").nonEmpty) {(out :+ (st.reverse.takeWhile(_ != "(")).head)} else out})
			case x :: xs => syard(xs, st, out)
		}
	}


// test cases
//syard(split("3 + 4 * ( 2 - 1 )"))  // 3 4 2 1 - * +
//syard(split("10 + 12 * 33"))       // 10 12 33 * +
//syard(split("( 5 + 7 ) * 2"))      // 5 7 + 2 *
//syard(split("5 + 7 / 2"))          // 5 7 2 / +
//syard(split("5 * 7 / 2"))          // 5 7 * 2 /
//syard(split("9 + 24 / ( 7 - 3 )")) // 9 24 7 3 - / +

//syard(split("3 + 4 + 5"))           // 3 4 + 5 +
//syard(split("( ( 3 + 4 ) + 5 )"))    // 3 4 + 5 +
//syard(split("( 3 + ( 4 + 5 ) )"))    // 3 4 5 + +
//syard(split("( ( ( 3 ) ) + ( ( 4 + ( 5 ) ) ) )")) // 3 4 5 + +

	def compute(toks: Toks, st: List[Int] = Nil) : Int = {
		toks.foldLeft(List[Int]())(
			(st, toEvaluate) => (st, toEvaluate) match {
				case (a :: b :: rest, "+") => (b + a) :: rest
				case (a :: b :: rest, "-") => (b - a) :: rest
				case (a :: b :: rest, "*") => (b * a) :: rest
				case (a :: b :: rest, "/") => (b / a) :: rest
				case (_, _) => java.lang.Integer.parseInt(toEvaluate) :: st
			}).head
	}


// test cases
// compute(syard(split("3 + 4 * ( 2 - 1 )")))  // 7
// compute(syard(split("10 + 12 * 33")))       // 406
// compute(syard(split("( 5 + 7 ) * 2")))      // 24
// compute(syard(split("5 + 7 / 2")))          // 8
// compute(syard(split("5 * 7 / 2")))          // 17
// compute(syard(split("9 + 24 / ( 7 - 3 )"))) // 15
}
