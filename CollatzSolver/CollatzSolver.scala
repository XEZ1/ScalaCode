object CollatzSolver {

  def collatzConjectureSteps(initial: Long, n: Long, count: Long): (Long, Long) = {
    if (n == 1) (count, initial) else if (n % 2 == 0) collatzConjectureSteps(initial, n / 2, count + 1) else collatzConjectureSteps(initial, ((n * 3) + 1), count + 1)
  }

  def collatz(n: Long): Long = collatzConjectureSteps(n, n, 0)._1

  def collatz_max_helper(bnd: Long): (List[(Long, Long)]) = {
    val lst = (1 to bnd.toInt).toList
    for (n <- lst) yield collatzConjectureSteps(n, n, 0)
  }

  def collatz_max(bnd: Long): (Long, Long) = {
    collatz_max_helper(bnd).maxByOption(_._1).getOrElse((0,0))
  }

  def is_pow_of_two(n: Long) : Boolean = (n & (n - 1)) == 0


  def is_hard(n: Long) : Boolean = is_pow_of_two(3*n+1)


  def collatzConjectureStepsNew(initial: Long, n: Long, n0: Long): Long = {
    if (is_pow_of_two(n)) (n0) else if (n % 2 == 0) collatzConjectureStepsNew(initial, n / 2, n) else collatzConjectureStepsNew(initial, ((n * 3) + 1), n)
  }

  def last_odd(n: Long) : Long = {
    collatzConjectureStepsNew(n, n, -1)
  }

}
