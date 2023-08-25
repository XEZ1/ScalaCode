# Collatz Conjecture Solver

## Overview

This repository contains a Scala program for solving the **Collatz conjecture**, also known as the **3n + 1 conjecture**. The Collatz conjecture is a mathematical puzzle that involves iteratively applying a set of rules to a positive integer until it eventually reaches the value 1. This program demonstrates the implementation of the following functions related to the Collatz conjecture:

1. **calculateSteps**: A recursive function that calculates the number of steps needed to reach 1 starting from a given positive integer.
2. **findMaxStepsInRange**: A function that takes an upper bound as an argument and calculates the maximum number of steps needed to reach 1 for all numbers in the range from 1 up to the given bound. It also provides the corresponding number that requires that many steps.
3. **lastOddBeforePowerOfTwo**: A function that calculates the last odd number before a power of two in the Collatz series.

## How to use

To run the program and test the Collatz conjecture solver, you can follow these steps:

1. Clone this repository to your local machine.
2. Ensure you have Scala installed. If not, you can download and install it from [here](https://www.scala-lang.org/download/).
3. Open a terminal/command prompt and navigate to the repository directory.
4. Run the program using the following command:
```
scala CollatzSolver.scala
```
5. The program will output the results for various test cases, demonstrating the correctness of the implemented functions.

## Why This Code Was Produced

This code was developed to solve the Collatz conjecture problem, a well-known mathematical puzzle. The goal was to implement efficient solutions to calculate the number of steps needed for different scenarios, including finding the maximum steps within a given range and determining the last odd number before a power of two in the Collatz series.

The code follows best practices and adheres to functional programming principles, utilising recursion and functional transformations. It avoids mutable data structures and uses only immutable values, promoting clean and maintainable code.

The inspiration for this project came from the curiosity and interest in exploring mathematical problems programmatically. 
