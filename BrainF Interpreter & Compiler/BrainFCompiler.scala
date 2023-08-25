object BrainFCompiler {
  // "Compiler" for the Brainf*** language

  // DEBUGGING INFORMATION FOR COMPILERS!!!
  //
  // Compiler, even real ones, are fiendishly difficult to get
  // to produce correct code. One way to debug them is to run
  // example programs ``unoptimised''; and then optimised. Does
  // the optimised version still produce the same result?

  // for timing purposes
  def time_needed[T](n: Int, code: => T) = {
    val start = System.nanoTime()
    for (i <- 0 until n) code
    val end = System.nanoTime()
    (end - start)/(n * 1.0e9)
  }

  type Mem = Map[Int, Int]

  import io.Source
  import scala.util._
  import java.util.regex.MatchResult

  def load_bff(name: String) : String = Try(Source.fromFile(name).mkString).getOrElse("")

  def sread(mem: Mem, mp: Int) : Int = Try(mem(mp)).getOrElse(0)

  def write(mem: Mem, mp: Int, v: Int) : Mem = mem + (mp -> v)

  def jumpRight(prog: String, pc: Int, level: Int) : Int = prog.toList.drop(pc) match {
    case c::cs => c match {
      case '[' => jumpRight(prog, pc + 1, level + 1)
      case ']' => jumpRight(prog, pc + 1, level - 1)
      case _ => if (level == -1) pc else jumpRight(prog, pc + 1, level)
    }
    case Nil => pc
  }

  def jumpLeft(prog: String, pc: Int, level: Int) : Int = prog.toList.drop(pc) match {
    case c::cs => c match {
      case ']' => if (level == -1) pc + 2 else if (pc == -1) -1 else jumpLeft(prog, pc - 1, level + 1)
      case '[' => if (level == -1) pc + 2 else if (pc == -1) -1 else jumpLeft(prog, pc - 1, level - 1)
      case _ => if (level == -1) pc + 2 else if (pc == -1) -1 else jumpLeft(prog, pc - 1, level)
    }
    case Nil => -1
  }

  // testcases
  //jumpRight("""--[..+>--],>,++""", 3, 0)         // => 10
  //jumpLeft("""--[..+>--],>,++""", 8, 0)          // => 3
  //jumpRight("""--[..[+>]--],>,++""", 3, 0)       // => 12
  //jumpRight("""--[..[[-]+>[.]]--],>,++""", 3, 0) // => 18
  //jumpRight("""--[..[[-]+>[.]]--,>,++""", 3, 0)  // => 22 (outside)
  //jumpLeft("""[******]***""", 7, 0)              // => -1 (outside)

  def compute(prog: String, pc: Int, mp: Int, mem: Mem) : Mem = prog.toList.drop(pc) match {
    case c::cs => c match {
      case '>' => compute(prog, pc + 1, mp + 1, mem)
      case '<' => compute(prog, pc + 1, mp - 1, mem)
      case '+' => compute(prog, pc + 1, mp, mem + (mp -> (sread(mem, mp) + 1)))
      case '-' => compute(prog, pc + 1, mp, mem + (mp -> (sread(mem, mp) - 1)))
      case '.' => {
        println(sread(mem, mp))
        compute(prog, pc + 1, mp, mem)
      }
      case ',' => {
        val in = Console.in.read().toByte
        compute(prog, pc + 1, mp, mem + (mp -> in))
      }
      case '[' => if (sread(mem, mp) == 0) compute(prog, jumpRight(prog, pc + 1, 0), mp, mem) else compute(prog, pc + 1, mp, mem)
      case ']' => if (sread(mem, mp) == 0) compute(prog, pc + 1, mp, mem) else compute(prog, jumpLeft(prog, pc - 1, 0), mp, mem)
      case _ => compute(prog, pc + 1, mp, mem)
    }
    case Nil => mem
  }

  def run(prog: String, m: Mem = Map()) = compute(prog, 0, 0, m)

  def generate(msg: List[Char]): String = {
    val sb = new StringBuilder
    for (c <- msg) {
      val asciiCode = c.toInt
      for (i <- 1 to asciiCode) sb.append("+")
      sb.append(".[-]")
    }
    sb.toString
  }

  def jtable(pg: String) : Map[Int, Int] = {
    jj(pg,0,Map[Int, Int]())
  }

  def jj(pg: String, pc: Int=0, mp:Map[Int, Int]) : Map[Int,Int] = {
    if(pc <= pg.length-1) pg.charAt(pc) match{
      case '[' => jj( pg,pc+1, mp + (pc -> jumpRight(pg,pc+1,0 ) ) )
      case ']' => jj( pg,pc+1, mp + (pc -> jumpLeft(pg, pc-1,0)  ) )
      case _ => jj(pg, pc+1,mp)

    }
    else mp
  }
  def jtable1(pg: String, pc: Int, mp:Map[Int, Int]) : Map[Int, Int] = {
    val open = pg.indexOf("[", pc)
    val close = pg.indexOf("]", pc) + 1
    //println(mp)
    if(close == -1 || open == -1) mp else jtable1(pg,close,mp+(open -> close))

  }

  // testcase
  //
  // jtable("""+++++[->++++++++++<]>--<+++[->>++++++++++<<]>>++<<----------[+>.>.<+<]""")
  // =>  Map(69 -> 61, 5 -> 20, 60 -> 70, 27 -> 44, 43 -> 28, 19 -> 6)

  def compute2(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem) : Mem = {
    if (pc >= pg.length()) mem
    else pg.charAt(pc) match {
      case '>' => compute2(pg,tb, pc + 1, mp + 1, mem)
      case '<' => compute2(pg,tb, pc + 1, mp - 1, mem)
      case '+' => compute2(pg,tb, pc + 1, mp, write(mem, mp, sread(mem, mp) + 1))
      case '-' => compute2(pg, tb, pc + 1, mp, write(mem, mp, sread(mem, mp) - 1))
      case '.' => print(sread(mem, mp).toChar); compute2(pg, tb, pc + 1, mp, mem)
      case ',' => compute2(pg, tb, pc + 1, mp, mem + (mp -> Console.in.read().toByte))
      case '[' => if (sread(mem, mp) == 0) compute2(pg, tb, tb(pc)/*jumpRight(pg, pc + 1, 0)*/, mp, mem) else compute2(pg, tb, pc + 1, mp, mem)
      case ']' => if (sread(mem, mp) != 0) compute2(pg, tb, tb(pc)/*jumpLeft(pg, pc - 1, 0)*/, mp, mem) else compute2(pg, tb, pc + 1, mp, mem)
      case _ => compute2(pg, tb, pc + 1, mp, mem)
    }
  }

  def run2(pg: String, m: Mem = Map()) = {
    compute2(pg,jtable(pg) ,0,0,m )
  }

  // testcases
  // time_needed(1, run2(load_bff("benchmark.bf")))
  // time_needed(1, run2(load_bff("sierpinski.bf")))

  def optimise(s: String) : String = {
    val newS = s.replaceAll("""[^<>+-.,\[\]]""", "").replaceAll("""\[-\]""", "0")
    newS
  }

  def compute3(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem) : Mem = {
    if (pc >= pg.length()) mem
    else pg.charAt(pc) match {
      case '>' => compute3(pg,tb, pc + 1, mp + 1, mem)
      case '<' => compute3(pg,tb, pc + 1, mp - 1, mem)
      case '+' => compute3(pg,tb, pc + 1, mp, write(mem, mp, sread(mem, mp) + 1))
      case '-' => compute3(pg,tb, pc + 1, mp, write(mem, mp, sread(mem, mp) - 1))
      case '.' => print(sread(mem, mp).toChar); compute3(pg,tb, pc + 1, mp, mem)
      case ',' => compute3(pg,tb, pc + 1, mp, mem + (mp -> Console.in.read().toByte))
      case '[' => if (sread(mem, mp) == 0) compute3(pg,tb, tb(pc)/*jumpRight(pg, pc + 1, 0)*/, mp, mem) else compute3(pg,tb, pc + 1, mp, mem)
      case ']' => if (sread(mem, mp) != 0) compute3(pg,tb, tb(pc)/*jumpLeft(pg, pc - 1, 0)*/, mp, mem) else compute3(pg,tb, pc + 1, mp, mem)
      case '0' => compute3(pg,tb, pc + 1, mp, write(mem, mp, 0))
      case _ => compute3(pg,tb, pc + 1, mp, mem)
    }
  }

  def run3( pg: String, m: Mem = Map() ) = {
    compute3(pg,jtable(pg),0,0,m )
  }

  // testcases
  //
  // optimise(load_bff("benchmark.bf"))          // should have inserted 0's
  // optimise(load_bff("mandelbrot.bf")).length  // => 11203
  //
  // time_needed(1, run3(load_bff("benchmark.bf")))

  def combine(s: String) : String = {
    def combine_helper(strVal: String, toReturn: String, idx: Int, count: Int, LETTERSINALPHABET: Int = 26, ASCIIROUNDER: Int = 64): String = {
      if (idx == strVal.length-1 && (strVal(idx) == '+' || strVal(idx) == '-' || strVal(idx) == '>' || strVal(idx) == '<')) toReturn+strVal.last+(count+ASCIIROUNDER).toChar
      else if (idx == strVal.length-1) toReturn+strVal.last
      else if (strVal(idx) == strVal(idx+1) && (strVal(idx) == '+' || strVal(idx) == '-' || strVal(idx) == '>' || strVal(idx) == '<') && count<LETTERSINALPHABET) combine_helper(strVal, toReturn, idx+1, count+1)
      else if (strVal(idx) != strVal(idx+1) && (strVal(idx) == '+' || strVal(idx) == '-' || strVal(idx) == '>' || strVal(idx) == '<')) combine_helper(strVal, toReturn+strVal(idx)+(count+ASCIIROUNDER).toChar, idx+1, 1)
      else if (strVal(idx) == strVal(idx+1) && (strVal(idx) == '+' || strVal(idx) == '-' || strVal(idx) == '>' || strVal(idx) == '<')) combine_helper(strVal, toReturn+strVal(idx)+(count+ASCIIROUNDER).toChar, idx+1, 1)
      else combine_helper(strVal, toReturn+strVal(idx), idx+1, 1)
    }
    combine_helper(s, "", 0, 1)
  }

  // testcase
  // combine(load_bff("benchmark.bf"))

  def compute4(pg: String, tb: Map[Int, Int], pc: Int, mp: Int, mem: Mem) : Mem = {
    if (pc >= pg.length()) mem
    else pg.charAt(pc) match {
      case '>' => compute4(pg,tb, pc + 1, mp + 1, mem)
      case '<' => compute4(pg,tb, pc + 1, mp - 1, mem)
      case '+' => compute4(pg,tb, pc + 1, mp, write(mem, mp, sread(mem, mp) + 1))
      case '-' => compute4(pg,tb, pc + 1, mp, write(mem, mp, sread(mem, mp) - 1))
      case '.' => print(sread(mem, mp).toChar); compute4(pg,tb, pc + 1, mp, mem)
      case ',' => compute4(pg,tb, pc + 1, mp, mem + (mp -> Console.in.read().toByte))
      case '[' => if (sread(mem, mp) == 0) compute4(pg,tb, tb(pc)/*jumpRight(pg, pc + 1, 0)*/, mp, mem) else compute4(pg,tb, pc + 1, mp, mem)
      case ']' => if (sread(mem, mp) != 0) compute4(pg,tb, tb(pc)/*jumpLeft(pg, pc - 1, 0)*/, mp, mem) else compute4(pg,tb, pc + 1, mp, mem)
      case '0' => compute4(pg,tb, pc + 1, mp, write(mem, mp, 0))
      case _ => compute4(pg,tb, pc + 1, mp, mem)
    }
  }

  // should call first optimise and then combine on the input string
  //
  def run4(pg: String, m: Mem = Map()) = {compute4(optimise(pg),jtable(optimise(pg)),0,0,m )}

  // testcases
  // combine(optimise(load_bff("benchmark.bf"))) // => """>A+B[<A+M>A-A]<A[[....."""

  // testcases (they should now run much faster)
  // time_needed(1, run4(load_bff("benchmark.bf")))
  // time_needed(1, run4(load_bff("sierpinski.bf")))
  // time_needed(1, run4(load_bff("mandelbrot.bf")))
}
