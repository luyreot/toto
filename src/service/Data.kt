package service

import model.Drawing
import model.SubPattern
import java.util.*

// Using a TreeMap to sort the drawings per year.
// The drawings themselves are sorted by the date the were released on.
val drawingsMap = TreeMap<String, List<Drawing>>()
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