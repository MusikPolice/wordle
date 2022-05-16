# wordle
[Wordle](https://www.nytimes.com/games/wordle/index.html) is an interesting game. Let's solve it!

## Algorithm
The algorithm is simple:
1. Guess the first word in the dictionary
2. Use the results to filter out words that violate any new knowledge that was gained
3. Repeat until finding the solution or running out of guesses

This algorithm is implemented in [Wordle.kt](src/main/kotlin/ca/jonathanfritz/wordle/Wordle.kt)

### Known Issues
This algorithm is pretty naive. Some limitations include:
* Guessing the first word in the dictionary is often sub-optimal
* The algorithm does not keep track of the letter positions that have already been tested, which means that it wastes guesses re-discovering information that it already knows
* Words that include the same letter twice are guessed, wasting an opportunity to gain more knowledge

I intend to fix these issues by ranking the remaining words in the dictionary by some heuristic that includes:
* Whether the word contains duplicate letters
* Prevalence of letters in the English language
* Prevalence of ngrams in the English language
* Some measure of how much additional information is expected to be gained by each valid guess

### Performance
The `--quantify` flag measures the performance of the algorithm by solving for every word in the dictionary and reporting the average number of guesses that were required to find a solution.
* `#b998d9b0` required 6.13 guesses
* `#12565f6` required 6.42 guesses
* `#05fdc23` required 5.15 guesses
* `#082d5e4` required 5.72 guesses

## Data
* The word list comes from [this pastebin](https://paste.ee/p/4zigF) of Wordle answers
* English language monogram frequency in [`englishmonograms.txt`](src/main/resources/englishmonograms.txt) came from [The University of Notre Dame](https://www3.nd.edu/~busiforc/handouts/cryptography/letterfrequencies.html).
* English language bigram frequency in  [`englishbigrams.txt`](src/main/resources/englishbigrams.txt) came from [Practical Cryptography](http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/).

## Next Steps
Right now, this code can only solve a wordle that you already know the answer to. In that sense, it's really just a proof of concept for the algorithm. In the future, I'd like to expand on this code to include a visualization of the game, or maybe even to play against the actual website.