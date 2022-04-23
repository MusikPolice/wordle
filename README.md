# wordle
[Wordle](https://www.powerlanguage.co.uk/wordle/) is an interesting game. Let's solve it!

## Algorithm
The algorithm is simple:
1. On the first attempt, guess the highest ranked word in the dictionary
2. Use the result to assemble a regex that will only match potential solutions
3. On subsequent attempts, guess the highest ranked word in the dictionary that matches the regex _and_ contains all potential characters
4. Repeat until finding the solution or running out of guesses

This algorithm is implemented in [Wordle.kt](src/main/kotlin/Wordle.kt)

### Known Issues
This algorithm is pretty naive. It does not keep track of the letter positions that have already been tested, which means that it wastes guesses re-discovering information that it already knows.

Here's an example to drive the point home:
```
Guess 1 is which
Correct letters: [null, null, null, null, null]
Known present letters: [i]
Known absent letters: [c, w, h]

Guess 2 is first
Correct letters: [null, null, null, null, null]
Known present letters: [f, i]
Known absent letters: [r, c, s, t, w, h]

Guess 3 is field
Correct letters: [null, null, null, null, null]
Known present letters: [e, f, i]
Known absent letters: [r, c, s, t, d, w, h, l]

Guess 4 is knife
Correct letters: [null, null, null, null, e]
Known present letters: [e, f, i]
Known absent letters: [r, c, s, t, d, w, h, k, l, n]

Guess 5 is effie
The solution is effie
```
In this case, the solution to the puzzle was found, but guessing "field" right after "first" was wasteful, because we already know that "f" is not in the first position. The next guess, "knife", is much smarter, because it tries a new position for "f", which helps to expand what we know about the solution.

### Performance
The `--quantify` flag measures the performance of the algorithm by solving for every word in the dictionary, reporting the average number of guesses that were required to find a solution.
* `#b998d9b0` required 6.13 guesses

## Data
* This implementation relies on a dictionary of 5 letter long English language words that has been ranked in order of frequency of use. I grabbed my dataset from [kaggle.com](https://www.kaggle.com/wheelercode/dictionary-word-frequency) and filtered it to contain only five letter words using the code in [FilterWordList.kt](src/main/kotlin/FilterWordList.kt).
* English language monogram frequency in [`englishmonograms.txt`](src/main/resources/englishmonograms.txt) came from [The University of Notre Dame](https://www3.nd.edu/~busiforc/handouts/cryptography/letterfrequencies.html).
* English language bigram frequency in  [`englishbigrams.txt`](src/main/resources/englishbigrams.txt) came from [Practical Cryptography](http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/).

## Next Steps
Right now, this code can only solve a wordle that you already know the answer to. In that sense, it's really just a proof of concept for the algorithm. In the future, I'd like to expand on this code to include a visualization of the game, or maybe even to play against the actual website.