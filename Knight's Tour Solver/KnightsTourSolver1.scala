import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Numeric.Implicits.infixNumericOps

object KnightsTourSolver1 {
  // Finding Knight's tours

  type Pos = (Int, Int)    // a position on a chessboard
  type Path = List[Pos]    // a path...a list of positions

  def is_legal(dim: Int, path: Path, x: Pos) : Boolean = {
    val (a, b) = x
    if (a >= 0 && a < dim && b >= 0 && b < dim && !path.contains(x)) true else false
  }

  def legal_moves(dim: Int, path: Path, x: Pos) : List[Pos] = {
    val legal_moves = List((1,2),(2,1),(2,-1),(1,-2),(-1,-2),(-2,-1),(-2,1),(-1,2))
    legal_moves.map{ case (dx, dy) => ( x._1 + dx , x._2 + dy) }.filter(move => is_legal(dim, path, move))
  }

  //some testcases
  //
  //assert(legal_moves(8, Nil, (2,2)) == List((3,4), (4,3), (4,1), (3,0), (1,0), (0,1), (0,3), (1,4)))
  //assert(legal_moves(8, Nil, (7,7)) == List((6,5), (5,6)))
  //assert(legal_moves(8, List((4,1), (1,0)), (2,2)) ==
  //  List((3,4), (4,3), (3,0), (0,1), (0,3), (1,4)))
  //assert(legal_moves(8, List((6,6)), (7,7)) == List((6,5), (5,6)))

  def count_tours(dim: Int, path: Path) : Int = {
    if(path.length == dim*dim && path.distinct.length == dim*dim) 1
    else legal_moves(dim,path,path.head).map(move => count_tours(dim, move :: path)).sum
  }

  def enum_tours(dim: Int, path: Path) : List[Path] = {
    if(path.length == dim*dim && path.distinct.length == dim*dim) List(path)
    else legal_moves(dim,path,path.head).flatMap(move => enum_tours(dim, move :: path))
  }

  def first(xs: List[Pos], f: Pos => Option[Path]) : Option[Path] = {
    if (xs.isEmpty) None
    else {
      val res = f(xs.head)
      if (res.isDefined) res
      else first(xs.tail, f)
    }
  }

  // testcases
  //
  //def foo(x: (Int, Int)) = if (x._1 > 3) Some(List(x)) else None
  //
  //first(List((1, 0),(2, 0),(3, 0),(4, 0)), foo)   // Some(List((4,0)))
  //first(List((1, 0),(2, 0),(3, 0)), foo)          // None

  def first_tour(dim: Int, path: Path) : Option[Path] = {
    if (dim*dim == path.length) Some(path)
    else first(legal_moves(dim,path,path.head),x => first_tour(dim,x::path))
  }

  // time measuring
  def time_needed[T](code: => T) : T = {
    val start = System.nanoTime()
    val result = code
    val end = System.nanoTime()
    println(f"Time needed: ${(end - start) / 1.0e9}%3.3f secs.")
    result
  }

  // board printing
  def print_board(dim: Int, path: Path): Unit = {
    println()
    for (i <- 0 until dim) {
      for (j <- 0 until dim) {
        print(f"${path.reverse.indexOf((j, dim - i - 1))}%3.0f ")
      }
      println()
    }
  }
}
