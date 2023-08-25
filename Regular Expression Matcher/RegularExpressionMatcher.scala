object RegularExpressionMatcher {

  abstract class Rexp
  case object ZERO extends Rexp
  case object ONE extends Rexp
  case class CHAR(c: Char) extends Rexp
  case class ALTs(rs: List[Rexp]) extends Rexp  // alternatives
  case class SEQs(rs: List[Rexp]) extends Rexp  // sequences
  case class STAR(r: Rexp) extends Rexp         // star

  //the usual binary choice and binary sequence can be defined
  //in terms of ALTs and SEQs
  def ALT(r1: Rexp, r2: Rexp) = ALTs(List(r1, r2))
  def SEQ(r1: Rexp, r2: Rexp) = SEQs(List(r1, r2))

  // some convenience for typing regular expressions
  import scala.language.implicitConversions
  import scala.language.reflectiveCalls

  def charlist2rexp(s: List[Char]): Rexp = s match {
    case Nil => ONE
    case c::Nil => CHAR(c)
    case c::s => SEQ(CHAR(c), charlist2rexp(s))
  }
  implicit def string2rexp(s: String): Rexp = charlist2rexp(s.toList)

  implicit def RexpOps (r: Rexp) = new {
    def | (s: Rexp) = ALT(r, s)
    def % = STAR(r)
    def ~ (s: Rexp) = SEQ(r, s)
  }

  implicit def stringOps (s: String) = new {
    def | (r: Rexp) = ALT(s, r)
    def | (r: String) = ALT(s, r)
    def % = STAR(s)
    def ~ (r: Rexp) = SEQ(s, r)
    def ~ (r: String) = SEQ(s, r)
  }

  // examples for the implicits:
  // ALT(CHAR('a'), CHAR('b'))
  // val areg : Rexp = "a" | "b"

  // SEQ(CHAR('a'), CHAR('b'))
  // val sreg : Rexp = "a" ~ "b"

  def nullable (r: Rexp) : Boolean = r match {
    case ZERO => false
    case ONE => true
    case CHAR(c) => false
    case ALTs(rs) => rs.exists(nullable)
    case SEQs(rs) => rs.forall(nullable)
    case STAR(r) => true
  }

  def der (c: Char, r: Rexp) : Rexp = {
    r match {
      case ZERO => ZERO
      case ONE => ZERO
      case SEQs(Nil) => ZERO
      case CHAR(ch) => if (ch == c) ONE else ZERO
      case ALTs(List(r1, r2)) => ALT(der(c, r1), der(c, r2))
      case SEQs(r1 :: r2) =>
        if (nullable(r1)) ALT(SEQs(der(c, r1) :: r2), der(c, SEQs(r2)))
        else SEQs(der(c, r1) :: r2)
      case STAR(r) => SEQ(der(c, r), STAR(r))
    }
  }

  def denest(rs: List[Rexp]) : List[Rexp] =  rs match {
    case Nil => Nil
    case ZERO :: rest => denest(rest)
    case ALTs(rs) :: rest => rs ::: denest(rest)
    case r :: rest => r :: denest(rest)
  }

  def flts(rs: List[Rexp], acc: List[Rexp] = Nil) : List[Rexp] =  rs match {
    case List() => acc
    case ZERO :: s => List(ZERO)
    case ONE :: s => flts(s,acc)
    case ALTs(x) :: s => x ::: flts(s,acc)
    case x :: s => x :: flts(s,acc)
  }

  def ALTs_smart(rs: List[Rexp]) : Rexp = rs match {
    case Nil => ZERO
    case r::Nil => r
    case r::rs => ALT(r, ALTs_smart(rs))
  }
  def ALTS(rs: Rexp*) = ALTs_smart(rs.toList)
  def SEQs_smart(rs: List[Rexp]) : Rexp = rs match {
    case Nil => ONE
    case r::Nil => r
    case r::rs => SEQ(r, SEQs_smart(rs))
  }
  def SEQs(rs: Rexp*) = SEQs_smart(rs.toList)

  def simp(r: Rexp): Rexp = r match {
    case ALTs(List(r1, r2)) =>
      val r1s = simp(r1)
      val r2s = simp(r2)
      if(r1s == ZERO) r2s
      else if(r2s == ZERO) r1s
      else if(r1s != r2s) ALT(r1s, r2s)
      else r1s
    case SEQs(List(r1, r2)) =>
      val r1s = simp(r1)
      val r2s = simp(r2)
      if(r1s == ZERO || r2s == ZERO) ZERO
      else if(r1s == ONE) r2s
      else if(r2s == ONE) r1s
      else SEQ(r1s, r2s)
    case _ => r
  }

  def ders (s: List[Char], r: Rexp) : Rexp = s match {
    case Nil => r
    case c::cs => ders(cs, simp(der(c, r)))
  }
  def matcher(r: Rexp, s: String): Boolean = {
    def matcherHelper(r: Rexp, s: List[Char]): Boolean = s match {
      case Nil => nullable(r)
      case c :: tail => matcherHelper(simp(der(c, r)), tail)
    }
    matcherHelper(r, s.toList)
  }

  def size(r: Rexp): Int = {
    r match {
      case ZERO | ONE | CHAR(_) => 1
      case ALTs(List(r1, r2)) => 1 + size(r1) + size(r2)
      case SEQs(List(r1, r2)) => 1 + size(r1) + size(r2)
      case STAR(r) => 1 + size(r)
    }
  }


  // Some testing data
  //===================
  /*
  simp(ALT(ONE | CHAR('a'), CHAR('a') | ONE))   // => ALTs(List(ONE, CHAR(a)))
  simp(((CHAR('a') | ZERO) ~ ONE) |
       (((ONE | CHAR('b')) | CHAR('c')) ~ (CHAR('d') ~ ZERO)))   // => CHAR(a)
  matcher(("a" ~ "b") ~ "c", "ab")   // => false
  matcher(("a" ~ "b") ~ "c", "abc")  // => true
  // the supposedly 'evil' regular expression (a*)* b
  val EVIL = SEQ(STAR(STAR(CHAR('a'))), CHAR('b'))
  matcher(EVIL, "a" * 1000)          // => false
  matcher(EVIL, "a" * 1000 ++ "b")   // => true
  // size without simplifications
  size(der('a', der('a', EVIL)))             // => 36
  size(der('a', der('a', der('a', EVIL))))   // => 83
  // size with simplification
  size(simp(der('a', der('a', EVIL))))           // => 7
  size(simp(der('a', der('a', der('a', EVIL))))) // => 7
  // Python needs around 30 seconds for matching 28 a's with EVIL.
  // Java 9 and later increase this to an "astonishing" 40000 a's in
  // 30 seconds.
  //
  // Lets see how long it really takes to match strings with
  // 5 Million a's...it should be in the range of a few
  // of seconds.
  def time_needed[T](i: Int, code: => T) = {
    val start = System.nanoTime()
    for (j <- 1 to i) code
    val end = System.nanoTime()
    "%.5f".format((end - start)/(i * 1.0e9))
  }
  for (i <- 0 to 5000000 by 500000) {
    println(s"$i ${time_needed(2, matcher(EVIL, "a" * i))} secs.")
  }
  // another "power" test case
  simp(Iterator.iterate(ONE:Rexp)(r => SEQ(r, ONE | ONE)).drop(50).next()) == ONE
  // the Iterator produces the rexp
  //
  //      SEQ(SEQ(SEQ(..., ONE | ONE) , ONE | ONE), ONE | ONE)
  //
  //    where SEQ is nested 50 times.
  */
}
