object BrainFInterpreter {
  // Interpreter for the Brainf*** language

  // representation of BF memory

  type Mem = Map[Int, Int]

  import io.Source
  import scala.util._

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


  // some sample bf-programs collected from the Internet
  //=====================================================

  //run("+++.+++.+++.", Map(0 -> 100))

  // some contrived (small) programs
  //---------------------------------

  // clears the 0-cell
  //run("[-]", Map(0 -> 100))    // Map will be 0 -> 0

  // moves content of the 0-cell to 1-cell
  //run("[->+<]", Map(0 -> 10))  // Map will be 0 -> 0, 1 -> 10


  // copies content of the 0-cell to 2-cell and 4-cell
  //run("[>>+>>+<<<<-]", Map(0 -> 42))    // Map(0 -> 0, 2 -> 42, 4 -> 42)

  // prints out numbers 0 to 9
  //run("""+++++[->++++++++++<]>--<+++[->>++++++++++<<]>>++<<----------[+>.>.<+<]""")

  // some more "useful" programs
  //-----------------------------

  // hello world program 1
  //run("""++++++++[>++++[>++>+++>+++>+<<<<-]>+>+>->>+[<]<-]>>.>---.+++++++
  //       ..+++.>>.<-.<.+++.------.--------.>>+.>++.""")

  // hello world program 2
  //run("""++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>+
  //       +.<<+++++++++++++++.>.+++.------.--------.>+.>.""")

  // hello world program 3
  //run("""+++++++++[>++++++++>+++++++++++>+++++<<<-]>.>++.+++++++..
  //       +++.>-.------------.<++++++++.--------.+++.------.--------.>+.""")

  // draws the Sierpinski triangle
  //run(load_bff("sierpinski.bf"))

  //fibonacci numbers below 100
  //run("""+++++++++++
  //      >+>>>>++++++++++++++++++++++++++++++++++++++++++++
  //      >++++++++++++++++++++++++++++++++<<<<<<[>[>>>>>>+>
  //      +<<<<<<<-]>>>>>>>[<<<<<<<+>>>>>>>-]<[>++++++++++[-
  //      <-[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]>[<<[>>>+<<<
  //      -]>>[-]]<<]>>>[>>+>+<<<-]>>>[<<<+>>>-]+<[>[-]<[-]]
  //      >[<<+>>[-]]<<<<<<<]>>>>>[+++++++++++++++++++++++++
  //      +++++++++++++++++++++++.[-]]++++++++++<[->-<]>++++
  //      ++++++++++++++++++++++++++++++++++++++++++++.[-]<<
  //      <<<<<<<<<<[>>>+>+<<<<-]>>>>[<<<<+>>>>-]<-[>>.>.<<<
  //      [-]]<<[>>+>+<<<-]>>>[<<<+>>>-]<<[<+>-]>[<+>-]<<<-]""")

  //outputs the square numbers up to 10000
  //run("""++++[>+++++<-]>[<+++++>-]+<+[>[>+>+<<-]++>>[<<+>>-]>>>[-]++>[-]+
  //       >>>+[[-]++++++>>>]<<<[[<++++++++<++>>-]+<.<[>----<-]<]
  //       <<[>>>>>[>>>[-]+++++++++<[>-<-]+++++++++>[-[<->-]+[<<<]]<[>+<-]>]<<-]<<-]""")

  // calculates 2 to the power of 6
  //(example from a C-to-BF compiler at https://github.com/elikaski/BF-it)
  //run(""">>[-]>[-]++>[-]++++++><<<>>>>[-]+><>[-]<<[-]>[>+<<+>-]>[<+>-]
  //       <><[-]>[-]<<<[>>+>+<<<-]>>>[<<<+>>>-][-]><<>>[-]>[-]<<<[>>[-]
  //       <[>+>+<<-]>[<+>-]+>[[-]<-<->>]<<<-]>>[<<+>>-]<<[[-]>[-]<<[>+>
  //       +<<-]>>[<<+>>-][-]>[-]<<<<<[>>>>+>+<<<<<-]>>>>>[<<<<<+>>>>>-]
  //       <<>>[-]>[-]<<<[>>>+<<<-]>>>[<<[<+>>+<-]>[<+>-]>-]<<<>[-]<<[-]
  //       >[>+<<+>-]>[<+>-]<><[-]>[-]<<<[>>+>+<<<-]>>>-[<<<+>>>-]<[-]>[-]
  //       <<<[>>+>+<<<-]>>>[<<<+>>>-][-]><<>>[-]>[-]<<<[>>[-]<[>+>+<<-]>
  //       [<+>-]+>[[-]<-<->>]<<<-]>>[<<+>>-]<<][-]>[-]<<[>+>+<<-]>>[<<+>
  //       >-]<<<<<[-]>>>>[<<<<+>>>>-]<<<<><>[-]<<[-]>[>+<<+>-]>[<+>-]<>
  //       <[-]>[-]>[-]<<<[>>+>+<<<-]>>>[<<<+>>>-]<<>>[-]>[-]>[-]>[-]>[-]>
  //       [-]>[-]>[-]>[-]>[-]<<<<<<<<<<>>++++++++++<<[->+>-[>+>>]>[+[-<+
  //       >]>+>>]<<<<<<]>>[-]>>>++++++++++<[->-[>+>>]>[+[-<+>]>+>>]<<<<<
  //       ]>[-]>>[>++++++[-<++++++++>]<.<<+>+>[-]]<[<[->-<]++++++[->++++
  //       ++++<]>.[-]]<<++++++[-<++++++++>]<.[-]<<[-<+>]<<><<<""")

  // a Mandelbrot set generator in brainf*** written by Erik Bosman
  // (http://esoteric.sange.fi/brainfuck/utils/mandelbrot/)
  //
  //run(load_bff("mandelbrot.bf"))

  // a benchmark program (counts down from 'Z' to 'A')
  //
  //run(load_bff("benchmark.bf"))

  // calculates the Collatz series for numbers from 1 to 30
  //
  //run(load_bff("collatz.bf"))
}
