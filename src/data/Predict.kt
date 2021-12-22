package data

object Predict {

    // 1. Get the last drawing
    // 2. Get the patterns (color, low/high, odd/even) for next drawing from the chains - see NEXT below...
    // 3. Generate all possible number drawings for the color patterns
    // 4. Remove any patterns that do not conform to the low/high patterns BUT store the removed ones - see STORE below...
    // 5. Do the same with the odd/even patterns
    // 6. Now we should have all possible drawings with the removed ones for LATER
    // 7. For every possible drawing + the removed ones, calculate its score based on the [numberSameDrawingChains], see SCORE below...
    // 8. Next do the same but with the [numberPreviousDrawingChains].
    // 9.

    // NEXT: Get patterns from chains (for color, low/high, odd/even)
    // - decide whether next pattern will be one from the current chain or a new one based on the probability
    // (Probability is calculated on the times each pattern in the chain has occurred more than once)
    // - if from chain: take every pattern from the chain
    // - if new: take the most probable patterns (decide on size) that is not in the chain
    // NOTE: for low/high and odd/even there are 7 different patterns. For those two cases take the top n number of patterns form the chains.

    // STORE
    //

    // SCORE
    //

}