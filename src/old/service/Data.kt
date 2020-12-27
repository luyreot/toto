package old.service

// Markov
val numberSameDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
val numberPreviousDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
val colorChains = mutableMapOf<String, MutableMap<String, Int>>()
val lowHighChains = mutableMapOf<String, MutableMap<String, Int>>()
val oddEvenChains = mutableMapOf<String, MutableMap<String, Int>>()

// All possible & missing color patterns, no need to do this for low/high and odd/even because they are all there
val allPossibleColorPatterns = mutableSetOf<String>()
val missingColorPatterns = mutableMapOf<String, Double>()