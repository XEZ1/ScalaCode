# Document Similarity Analyser

## Overview

Welcome to the Document Similarity Analyzer! This repository contains a Scala program designed to determine the similarity between two documents, whether they are source code or English texts. The purpose of this program is to provide a tool for assessing the resemblance between documents and assisting in detecting potential plagiarism. The program utilises various techniques to achieve its goal.

## Key Features

1. **String Cleaning**: The program includes a function that cleans a given string to extract meaningful words. Regular expressions are used in combination with the `findAllIn` function to identify words within the string.

2. **Document Occurrences**: To compute the overlap between two documents, the program associates each document with a map. This map represents the strings within the document and the frequency of their occurrences. The function efficiently calculates these occurrences, creating memory-efficient representations of the documents.

3. **Dot Product of Vectors**: The program implements the dot product of two sparse "vectors," which are represented as maps of string occurrences. This dot product captures the similarity between the documents, highlighting shared terms while penalising discrepancies.

4. **Overlap Calculation**: The program calculates the overlap between two documents based on the terms they share. The formula used considers the unique terms in each document and their corresponding frequencies. The overlap is quantified as a value between 0 and 1.

5. **Similarity Measurement**: A function assesses the similarity of two strings by extracting substrings using the cleaning function. The similarity is then calculated based on the overlap between the resulting documents.

## Usage

To use the Document Similarity Analyzer:

1. Clone this repository to your local machine.
2. Ensure you have Scala installed. If not, you can download and install it from [here](https://www.scala-lang.org/download/).
3. Open a terminal/command prompt and navigate to the repository directory.
4. Run the program using the following command:
```
scala SimilarityAnalyser.scala
```
5. The program will output the similarity scores between various test cases, demonstrating its effectiveness in assessing document resemblance.

## Purpose

This program was developed to address the need for detecting document similarity, a critical aspect of assessing potential plagiarism and ensuring the originality of content. The techniques implemented in this program have real-world applications, such as identifying similarities between software codebases or written texts.

