# wordle
[Wordle](https://www.nytimes.com/games/wordle/index.html) is an interesting game. Let's solve it!

## Algorithm
The algorithm is simple:
1. Guess the first word in the dictionary
2. Use the results to filter out words that violate any new knowledge that was gained
3. Repeat until finding the solution or running out of guesses

This algorithm is implemented in [Wordle.kt](src/main/kotlin/ca/jonathanfritz/wordle/Wordle.kt)

### Known Issues
All three existing comparators are based on the idea that a better guess contains characters or ngrams that are more common in the english language than not.

In practice, it's not clear that this is the best way to choose a guess.

Currently, the score fall off for both monograms and bigrams is linear. Future improvements may attempt to improve performance by causing the score to fall off much faster for lower-ranked ngrams.

### Performance
The `--quantify` flag measures the performance of the algorithm by solving for every word in the dictionary and reporting the average number of guesses that were required to find a solution.
* `#b998d9b0` required 6.13 guesses
* `#12565f6` required 6.42 guesses
* `#05fdc23` required 5.15 guesses
* `#082d5e4` required 5.72 guesses
* `#fb6f916` required 6.06 guesses
* `#a065760` required:
  * 5.09 guesses with the Monogram Comparator
  * 5.72 guesses with the Bigram Comparator
  * 5.25 guesses with the Combined Comparator

## Data
* The word list comes from [this pastebin](https://paste.ee/p/4zigF) of Wordle answers
* English language monogram frequency in [`englishmonograms.txt`](src/main/resources/englishmonograms.txt) came from [The University of Notre Dame](https://www3.nd.edu/~busiforc/handouts/cryptography/letterfrequencies.html).
* English language bigram frequency in  [`englishbigrams.txt`](src/main/resources/englishbigrams.txt) came from [Practical Cryptography](http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/english-letter-frequencies/).

## Next Steps
Right now, this code can only solve a wordle that you already know the answer to. In that sense, it's really just a proof of concept for the algorithm. In the future, I'd like to expand on this code to include a visualization of the game, or maybe even to play against the actual website.