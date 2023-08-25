# Knight's Tour Problem Solver in Scala

*"The problem with object-oriented languages is they've got all this implicit,
environment that they carry around with them. You wanted a banana but
what you got was a gorilla holding the banana and the entire jungle."*
— Joe Armstrong (creator of the Erlang programming language)

## Introduction

This repository contains Scala programs that address various versions of the Knight's Tour Problem on a chessboard. The Knight's Tour Problem is about finding a tour such that a knight visits every field on an n x n chessboard exactly once. The challenge becomes more complex as the dimensions of the chessboard increase.

## Background

The Knight's Tour Problem involves finding a path where a knight moves on a chessboard in such a way that each square is visited exactly once. The tour is considered closed if the knight can return to its starting position. Warnsdorf's Rule, a heuristic, can help speed up the search for tours.

## Tasks

The repository contains Scala implementations for solving the Knight's Tour Problem:

### File 1 (File: `KnightsTourSolver1.scala`)
- Implement `is_legal` function to check if a position is within the board and unvisited.
- Implement `legal_moves` function to calculate legal onward moves.
- Implement `count_tours` and `enum_tours` recursive functions to search exhaustively for tours.

### File 2 (File: `KnightsTourSolver2.scala`)
- Implement `ordered_moves` to calculate onward moves based on Warnsdorf’s Rule.
- Implement `first_closed_tour_heuristics` to find a single closed tour on a 6x6 board using ordered moves.
- Implement `first_tour_heuristics` to search for tours on boards up to 30x30 using ordered moves.

### File 3 (File: `KnightsTourSolver3.scala`)
- Implement `tour_on_mega_board` to search for tours on boards up to 70x70 within 30 seconds using tail recursion.

### File 4 (File: `KnightsTourSolver4.scala`)
- Implement `one_tour_pred` to find a single tour (if it exists) on "mutilated" chessboards, using specified path length and a predicate function.

## How to Use

1. Clone or download this repository to your local machine.
2. Ensure you have Scala installed.
3. Open a terminal and navigate to the repository directory.
4. Compile and run the Scala code using the following commands
```
scala KnightsTourSolver1.scala
scala KnightsTourSolver2.scala
scala KnightsTourSolver3.scala
scala KnightsTourSolver4.scala
```