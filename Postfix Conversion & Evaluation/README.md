# Infix to Postfix Expression Converter and Evaluator

## Overview

Welcome to the Infix to Postfix Expression Converter and Evaluator repository! Here, we present a Scala program that showcases a robust solution for converting and evaluating mathematical expressions from infix notation to postfix notation. This technique is inspired by the Shunting Yard Algorithm, a versatile method developed by Edsger Dijkstra.

## Key Features

1. **Conversion to Postfix Notation**: The program employs the Shunting Yard Algorithm to convert well-formed infix arithmetic expressions into postfix notation. The algorithm meticulously processes input tokens, taking into account operators, numbers, and parentheses. The resulting postfix expressions offer a more convenient representation for certain computations.

2. **Postfix Expression Evaluation**: The program includes an evaluation component that calculates the value of a postfix expression. Utilising a stack-based approach, the evaluation considers common arithmetic operations such as addition, subtraction, multiplication, and division. Additionally, the program handles the right-associative power operator to enrich the expression capabilities.

## Usage

To utilise the Infix to Postfix Expression Converter and Evaluator:

1. Clone this repository to your local machine.
2. Ensure you have Scala installed. If not, you can download and install it from [here](https://www.scala-lang.org/download/).
3. Open a terminal/command prompt and navigate to the repository directory.
4. Run the program using the following command:
```
scala PostfixConversion.scala
```
for the first part, and
```
scala PostfixEvaluation.scala
```
for the second one.
5. Follow the program's instructions to input infix expressions and observe their conversion to postfix notation and subsequent evaluation.

## Purpose

The primary motivation behind this project was to design a versatile tool for processing mathematical expressions in both infix and postfix notations. 

This solution could be applied in practical scenarios where efficient expression manipulation is required, such as calculator applications, numerical analysis, and code optimisation.
