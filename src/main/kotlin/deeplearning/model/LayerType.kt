package deeplearning.model

enum class LayerType {
    /**
     * Passes the inputs over to the next layer without performing any operations.
     */
    INPUT,
    HIDDEN,
    OUTPUT
}