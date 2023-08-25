object KnightsTourSolver2 {
  // Finding a single tour for a board using the Warnsdorf Rule

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

  def first(xs: List[Pos], f: Pos => Option[Path]) : Option[Path] = {
    if (xs.isEmpty) None
    else {
      val res = f(xs.head)
      if (res.isDefined) res
      else first(xs.tail, f)
    }
  }

  def count_tours(dim: Int, path: Path) : Int = {
    if(path.length == dim*dim && path.distinct.length == dim*dim) 1
    else legal_moves(dim,path,path.head).map(move => count_tours(dim, move :: path)).sum
  }

  def enum_tours(dim: Int, path: Path) : List[Path] = {
    if(path.length == dim*dim && path.distinct.length == dim*dim) List(path)
    else legal_moves(dim,path,path.head).flatMap(move => enum_tours(dim, move :: path))
  }

  def ordered_moves(dim: Int, path: Path, x: Pos) : List[Pos] = {
    legal_moves(dim, path, x).sortBy(p => legal_moves(dim, p :: path, p).length)
  }

  def first_closed_tour_heuristics(dim: Int, path: Path) : Option[Path] = {
    if (dim < 5) None
    if(dim*dim == path.length) {
      if(ordered_moves(dim,List(),path.head).contains(path.last) == true) {
        Some(path)
      }
      else {
        None
      }
    }
    else first(ordered_moves(dim,path,path.head), x => first_closed_tour_heuristics(dim,x::path))
  }

  //  searches for *non-closed* tours. This
  //  version of the function will be called with dimensions of
  //  up to 30 * 30.

  def first_tour_heuristics(dim: Int, path: Path) : Option[Path] = {
    if(dim < 5) None
    else {
      if(dim*dim == path.length) Some(path)
      else first(ordered_moves(dim,path,path.head), x => first_tour_heuristics(dim,x::path))
    }
  }
}
