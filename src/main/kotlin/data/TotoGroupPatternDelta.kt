package data

/**
 * Alternative way of handling group patterns for drawings.
 *
 * Given a drawing, perform the following algorithm:
 * 1. sort the drawing in ascending order
 * 2. take the first / lowest number as the first result in the output
 * 3. divide subsequent numbers by the previous one in the array
 * 4. use the result in the output
 *
 * To reverse the output, perform the following algorithm:
 * 1. take the first number as the first result in the output
 * 2. take the second number and add it to the result from step 1
 * 3. use the product from step 2 as the next result in the output
 * 4. perform addition of each subsequent number with the previous result
 * 5. use each product in the ouput
 *
 * Here is an example:
 *
 * Original drawing:
 * 4,6,9,21,36,46
 *
 * Delta algorithm operation forward:
 * 4
 * 6-4 = 2
 * 9-6 = 3
 * 21-9 = 12
 * 36-21 = 15
 * 46-36 = 10
 *
 * Output:
 * 4,2,3,12,15,10
 *
 * Delta algorithm reversed (backwards) operation:
 * 4
 * 4+2 = 6
 * 6+3 = 9
 * 9+12 = 21
 * 21+15 = 36
 * 36+10 = 46
 *
 * Output / Original drawing:
 * 4,6,9,21,36,46
 *
 * This algorithm serves the purpose of lowering the total possible numbers
 * that can occur in a drawing. In the case of 6/49 instead of having a total of 49 different number
 * we end it with around 15.
 */
class TotoGroupPatternDelta