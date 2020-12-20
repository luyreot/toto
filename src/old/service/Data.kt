package old.service

import impl.model.Drawing
import old.model.SubPattern

val drawingsList = mutableListOf<Drawing>()

// Patterns
var numberPatterns = mutableMapOf<String, SubPattern>()
var colorPatterns = mutableMapOf<String, SubPattern>()
var lowHighPatterns = mutableMapOf<String, SubPattern>()
var oddEvenPatterns = mutableMapOf<String, SubPattern>()

// Markov
val numberSameDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
val numberPreviousDrawingChains = mutableMapOf<String, MutableMap<String, Int>>()
val colorChains = mutableMapOf<String, MutableMap<String, Int>>()
val lowHighChains = mutableMapOf<String, MutableMap<String, Int>>()
val oddEvenChains = mutableMapOf<String, MutableMap<String, Int>>()

// All possible & missing color patterns, no need to do this for low/high and odd/even because they are all there
val allPossibleColorPatterns = mutableSetOf<String>()
val missingColorPatterns = mutableMapOf<String, Double>()