# Brainf*** Interpreter and Compiler

![Project Logo](link_to_your_logo.png)

## Overview

This repository contains an interpreter and compiler for the brainf*** programming language, implemented in Scala. Brainf*** is an esoteric programming language known for its minimalistic set of instructions and Turing completeness.

## Features

- **Interpreter:** Interpret brainf*** programs directly and execute them step by step.
- **Compiler:** Compile brainf*** programs into optimised executable code for improved performance.
- **Optimisations:** Apply various optimisations to improve the efficiency of brainf*** programs.
- **Jump Table:** Utilise a jump table for efficient loop handling in compiled programs.

## How to Use

1. Clone or download this repository to your local machine.
2. Ensure you have Scala installed.
3. Open a terminal and navigate to the repository directory.
4. Compile and run the Scala code using the command
```
scala BrainFInterpreter.scala
scala BrainFCompiler.scala
```

## Examples


### Running the Interpreter

1. Uncomment the following code in the file:
```
    run(""">>[-]>[-]++>[-]++++++><<<>>>>[-]+><>[-]<<[-]>[>+<<+>-]>[<+>-]
           <><[-]>[-]<<<[>>+>+<<<-]>>>[<<<+>>>-][-]><<>>[-]>[-]<<<[>>[-]
           <[>+>+<<-]>[<+>-]+>[[-]<-<->>]<<<-]>>[<<+>>-]<<[[-]>[-]<<[>+>
           +<<-]>>[<<+>>-][-]>[-]<<<<<[>>>>+>+<<<<<-]>>>>>[<<<<<+>>>>>-]
           <<>>[-]>[-]<<<[>>>+<<<-]>>>[<<[<+>>+<-]>[<+>-]>-]<<<>[-]<<[-]
           >[>+<<+>-]>[<+>-]<><[-]>[-]<<<[>>+>+<<<-]>>>-[<<<+>>>-]<[-]>[-]
           <<<[>>+>+<<<-]>>>[<<<+>>>-][-]><<>>[-]>[-]<<<[>>[-]<[>+>+<<-]>
           [<+>-]+>[[-]<-<->>]<<<-]>>[<<+>>-]<<][-]>[-]<<[>+>+<<-]>>[<<+>
           >-]<<<<<[-]>>>>[<<<<+>>>>-]<<<<><>[-]<<[-]>[>+<<+>-]>[<+>-]<>
           <[-]>[-]>[-]<<<[>>+>+<<<-]>>>[<<<+>>>-]<<>>[-]>[-]>[-]>[-]>[-]>
           [-]>[-]>[-]>[-]>[-]<<<<<<<<<<>>++++++++++<<[->+>-[>+>>]>[+[-<+
           >]>+>>]<<<<<<]>>[-]>>>++++++++++<[->-[>+>>]>[+[-<+>]>+>>]<<<<<
           ]>[-]>>[>++++++[-<++++++++>]<.<<+>+>[-]]<[<[->-<]++++++[->++++
           ++++<]>.[-]]<<++++++[-<++++++++>]<.[-]<<[-<+>]<<><<<""")
```
2. Execute the following command:
```sh
$ scala BrainFInterpreter.scala
64
```

### Compiling and Running

1. Uncomment the following code in the file:
```
    time_needed(1, run4(load_bff("benchmark.bf")))
    time_needed(1, run4(load_bff("sierpinski.bf")))
    time_needed(1, run4(load_bff("mandelbrot.bf")))
```
2. Execute the following command:
```sh
$ scala BrainFInterpreter.scala
Time
```

# Acknowledgments

This project was inspired by the brainf*** programming language and the challenge of optimising its execution. Special thanks to the Scala programming language for providing a powerful environment for this endeavor.
