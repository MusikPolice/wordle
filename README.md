# wordle
[Wordle](https://www.powerlanguage.co.uk/wordle/) is an interesting game. Let's solve it!

## Algorithm
The algorithm is simple:
1. On the first attempt, guess the highest ranked word in the dictionary
2. Use the result to assemble a regex that will only match potential solutions
3. On subsequent attempts, guess the highest ranked word in the dictionary that matches the regex _and_ contains all potential characters
4. Repeat until finding the solution or running out of guesses

This algorithm is implemented in [Wordle.kt](src/main/kotlin/Wordle.kt)

## Data
This implementation relies on a dictionary of 5 letter long English language words that has been ranked in order of frequency of use. I grabbed my dataset from [kaggle.com](https://www.kaggle.com/wheelercode/dictionary-word-frequency) and filtered it to contain only five letter words using the code in [FilterWordList.kt](src/main/kotlin/FilterWordList.kt). 

## Next Steps
Right now, this code can only solve a wordle that you already know the answer to. In that sense, it's really just a proof of concept for the algorithm. In the future, I'd like to expand on this code to include a visualization of the game, or maybe even to play against the actual website.