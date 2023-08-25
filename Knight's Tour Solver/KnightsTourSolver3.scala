object KnightsTourSolver3 {
  // Finding a single tour on a "mega" board

  type Pos = (Int, Int) // a position on a chessboard
  type Path = List[Pos] // a path...a list of positions

  def is_legal(dim: Int, path: Path, x: Pos): Boolean = {
    (x._1 >= 0 && x._2 >= 0 && x._1 < dim && x._2 < dim && path.contains(x) == false)
  }

  def legal_moves(dim: Int, path: Path, x: Pos): List[Pos] = {
    List((x._1 + 1, x._2 + 2),
      (x._1 + 2, x._2 + 1),
      (x._1 + 2, x._2 - 1),
      (x._1 + 1, x._2 - 2),
      (x._1 - 1, x._2 - 2),
      (x._1 - 2, x._2 - 1),
      (x._1 - 2, x._2 + 1),
      (x._1 - 1, x._2 + 2)).filter(move => is_legal(dim, path, move))
    //(a :: b :: c :: d :: e :: f :: g :: h :: List()).filter(move => is_legal(dim,path,move) == true)
  }

  def ordered_moves(dim: Int, path: Path, x: Pos): List[Pos] = {
    (legal_moves(dim, path, x).map(move => (move, legal_moves(dim, path, move).length)).toList.sortBy(_._2)).map(pair => pair._1)
  }

  def tour_on_mega_board(dim: Int, path: Path): Option[Path] = {
    if (dim < 5) None
    else {
      if (dim * dim == path.length) Some(path)
      else tour_on_mega_board(dim, ordered_moves(dim, path, path.head).head :: path)
    }
  }
}

