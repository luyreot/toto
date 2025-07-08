package visualizer

import model.TotoType
import java.awt.*
import java.awt.datatransfer.StringSelection
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder

class NumberViewer(
    totoType: TotoType,
    private val allNumbers: List<Int>,
    private val combinationSize: Int,
    private val totalCombinations: Int
) : JFrame("Combinations Viewer ${totoType.name}") {

    private var currentIndex = 0
    private val combinations: MutableList<MutableList<Int>> =
        MutableList(totalCombinations) { mutableListOf() }

    private val selectedNumbers = mutableSetOf<Int>()

    private val numberLabel = JLabel("", SwingConstants.CENTER)
    private val selectButton = JButton("Select")
    private val combinationsPanel = JPanel()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(600, 500)
        isResizable = false
        layout = BorderLayout()

        isFocusable = true
        focusTraversalKeysEnabled = false

        // Top-center: combinations panel
        combinationsPanel.layout = BoxLayout(combinationsPanel, BoxLayout.Y_AXIS)
        combinationsPanel.border = EmptyBorder(10, 10, 10, 10)
        updateCombinationsDisplay()
        add(combinationsPanel, BorderLayout.NORTH)

        // Center: number display
        numberLabel.font = Font("Arial", Font.BOLD, 48)
        val centerPanel = JPanel(GridBagLayout())
        centerPanel.add(numberLabel)
        centerPanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mouseClicked(e: java.awt.event.MouseEvent) {
                val clickedComponent = centerPanel.getComponentAt(e.point)

                // Check if the click is NOT on a button
                if (clickedComponent !is JButton) {
                    toggleNumber()
                    requestFocusInWindow()
                }
            }
        })
        add(centerPanel, BorderLayout.CENTER)

        // Bottom: styled buttons
        val buttonPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        buttonPanel.border = EmptyBorder(10, 20, 20, 20)

        styleSelectButton(selectButton)

        val resetButton = JButton("Reset")
        styleSelectButton(resetButton)

        val copyButton = JButton("Copy")
        styleSelectButton(copyButton)

        buttonPanel.add(selectButton)
        buttonPanel.add(Box.createHorizontalStrut(10))
        buttonPanel.add(resetButton)
        buttonPanel.add(Box.createHorizontalStrut(10))
        buttonPanel.add(copyButton)

        add(buttonPanel, BorderLayout.SOUTH)

        selectButton.addActionListener {
            selectCurrentNumber()
            toggleNumber()
        }

        resetButton.addActionListener {
            resetCombinations()
        }

        copyButton.addActionListener {
            val text = combinations
                .filter { it.size == combinationSize }
                .joinToString("\n") {
                    it.sorted().joinToString(", ")
                }

            val selection = StringSelection(text)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, null)
        }

        setLocationRelativeTo(null)
        isVisible = true
        requestFocusInWindow()
        updateDisplay()
    }

    private fun toggleNumber() {
        currentIndex = (currentIndex + 1) % allNumbers.size
        updateDisplay()
    }

    private fun styleSelectButton(button: JButton) {
        button.preferredSize = Dimension(100, 35)
        button.font = Font("Arial", Font.BOLD, 14)
        button.border = LineBorder(Color.BLACK, 2, true)
        button.background = Color.WHITE
        button.isFocusable = false
    }

    private fun updateDisplay() {
        numberLabel.text = allNumbers[currentIndex].toString()
    }

    private fun selectCurrentNumber() {
        val num = allNumbers[currentIndex]
        if (selectedNumbers.contains(num)) return
        if (selectedNumbers.size >= combinationSize * totalCombinations) return

        selectedNumbers.add(num)

        for (combo in combinations) {
            if (combo.size < combinationSize) {
                combo.add(num)
                break
            }
        }

        updateCombinationsDisplay()

        if (selectedNumbers.size >= combinationSize * totalCombinations) {
            selectButton.isEnabled = false
        }
    }

    private fun updateCombinationsDisplay() {
        combinationsPanel.removeAll()

        for (combo in combinations) {
            val comboText = combo.joinToString("  ")
            val label = JLabel(comboText)
            label.font = Font(Font.MONOSPACED, Font.PLAIN, 16)

            val rowPanel = JPanel()
            rowPanel.layout = FlowLayout(FlowLayout.CENTER, 0, 0) // Centered, no extra spacing
            rowPanel.isOpaque = false
            rowPanel.add(label)

            combinationsPanel.add(rowPanel)
            combinationsPanel.add(Box.createVerticalStrut(4)) // Space between rows
        }

        combinationsPanel.revalidate()
        combinationsPanel.repaint()
    }

    private fun resetCombinations() {
        selectedNumbers.clear()
        for (combo in combinations) {
            combo.clear()
        }
        selectButton.isEnabled = true
        updateCombinationsDisplay()
    }
}