object KnightsTourSolver4 {
  // Part 4 about finding a single tour on "mutilated" chessboards

  type Pos = (Int, Int)
  type Path = List[Pos]

  def print_board(dim: Int, path: Path): Unit = {
    println()
    for (i <- 0 until dim) {
      for (j <- 0 until dim) {
        print(f"${path.reverse.indexOf((i, j))}%4.0f ")
      }
      println()
    }
  }

  def one_tour_pred(dim: Int, path: Path, n: Int, pred: Pos => Boolean): Option[Path] = {
    def is_legal(dim: Int, path: Path, x: Pos) : Boolean = {
      val (a, b) = x
      if (a >= 0 && a < dim && b >= 0 && b < dim && !path.contains(x)) true else false
    }

    def legal_moves(dim: Int, path: Path, x: Pos) : List[Pos] = {
      val legal_moves = List((1,2),(2,1),(2,-1),(1,-2),(-1,-2),(-2,-1),(-2,1),(-1,2))
      legal_moves.map{ case (dx, dy) => ( x._1 + dx , x._2 + dy) }.filter(move => is_legal(dim, path, move))
    }

    if (path.size == n) return Some(path)
    val legal_moves_1: List[Pos] = legal_moves(dim, path, path.head).filter(pred)
    legal_moves_1.foreach { move =>
      val result = one_tour_pred(dim, move :: path, n, pred)
      if (result.isDefined) return result
    }
    None
  }
}
