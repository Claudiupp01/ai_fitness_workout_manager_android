package com.example.ai_fitness_workout_manager.profile

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_fitness_workout_manager.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileQuestionAdapter(
    private val questions: List<ProfileQuestion>,
    private val onAnswerChanged: (String, Any) -> Unit
) : RecyclerView.Adapter<ProfileQuestionAdapter.QuestionViewHolder>() {

    private val answers = mutableMapOf<String, Any>()

    fun getAnswer(fieldName: String): Any? = answers[fieldName]

    fun setAnswer(fieldName: String, value: Any) {
        answers[fieldName] = value
    }

    fun getAllAnswers(): Map<String, Any> = answers.toMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int = questions.size

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvQuestionTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvQuestionSubtitle)
        private val tilTextInput: TextInputLayout = itemView.findViewById(R.id.tilTextInput)
        private val etTextInput: TextInputEditText = itemView.findViewById(R.id.etTextInput)
        private val rgSingleChoice: RadioGroup = itemView.findViewById(R.id.rgSingleChoice)
        private val cgMultiChoice: ChipGroup = itemView.findViewById(R.id.cgMultiChoice)
        private val sliderContainer: LinearLayout = itemView.findViewById(R.id.sliderContainer)
        private val tvSliderValue: TextView = itemView.findViewById(R.id.tvSliderValue)
        private val tvSliderUnit: TextView = itemView.findViewById(R.id.tvSliderUnit)
        private val slider: Slider = itemView.findViewById(R.id.slider)
        private val tvSliderMin: TextView = itemView.findViewById(R.id.tvSliderMin)
        private val tvSliderMax: TextView = itemView.findViewById(R.id.tvSliderMax)
        private val datePickerContainer: LinearLayout = itemView.findViewById(R.id.datePickerContainer)
        private val tvSelectedDate: TextView = itemView.findViewById(R.id.tvSelectedDate)
        private val btnSelectDate: Button = itemView.findViewById(R.id.btnSelectDate)
        private val tvOptional: TextView = itemView.findViewById(R.id.tvOptional)

        fun bind(question: ProfileQuestion) {
            tvTitle.text = question.title
            tvSubtitle.text = question.subtitle

            // Hide all input containers first
            tilTextInput.visibility = View.GONE
            rgSingleChoice.visibility = View.GONE
            cgMultiChoice.visibility = View.GONE
            sliderContainer.visibility = View.GONE
            datePickerContainer.visibility = View.GONE

            // Show optional indicator if not required
            tvOptional.visibility = if (!question.isRequired) View.VISIBLE else View.GONE

            when (question.type) {
                QuestionType.TEXT_INPUT -> setupTextInput(question)
                QuestionType.SINGLE_CHOICE -> setupSingleChoice(question)
                QuestionType.MULTI_CHOICE -> setupMultiChoice(question)
                QuestionType.SLIDER, QuestionType.NUMBER_PICKER -> setupSlider(question)
                QuestionType.DATE_PICKER -> setupDatePicker(question)
            }
        }

        private fun setupTextInput(question: ProfileQuestion) {
            tilTextInput.visibility = View.VISIBLE
            tilTextInput.hint = question.hint

            // Restore previous answer if exists
            val previousAnswer = answers[question.fieldName] as? String
            etTextInput.setText(previousAnswer ?: "")

            etTextInput.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val value = etTextInput.text.toString().trim()
                    if (value.isNotEmpty()) {
                        answers[question.fieldName] = value
                        onAnswerChanged(question.fieldName, value)
                    }
                }
            }
        }

        private fun setupSingleChoice(question: ProfileQuestion) {
            rgSingleChoice.visibility = View.VISIBLE
            rgSingleChoice.removeAllViews()

            val previousAnswer = answers[question.fieldName] as? String

            question.options.forEachIndexed { index, option ->
                val radioButton = RadioButton(itemView.context).apply {
                    id = View.generateViewId()
                    text = if (option.description.isNotEmpty()) {
                        "${option.label}\n${option.description}"
                    } else {
                        option.label
                    }
                    textSize = 16f
                    setPadding(16, 24, 16, 24)
                    setTextColor(itemView.context.getColor(R.color.textPrimary))
                    isChecked = previousAnswer == option.id

                    // Special handling for workout duration which stores as Int
                    if (question.fieldName == "workoutDurationMinutes") {
                        val prevDuration = answers[question.fieldName] as? Int
                        isChecked = prevDuration?.toString() == option.id
                    }
                }

                rgSingleChoice.addView(radioButton)

                // Add margin between options
                (radioButton.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    bottomMargin = 8
                }
            }

            rgSingleChoice.setOnCheckedChangeListener { group, checkedId ->
                val selectedIndex = (0 until group.childCount).indexOfFirst {
                    group.getChildAt(it).id == checkedId
                }
                if (selectedIndex >= 0) {
                    val selectedOption = question.options[selectedIndex]

                    // Handle workout duration specially (needs to be Int)
                    val value: Any = if (question.fieldName == "workoutDurationMinutes") {
                        selectedOption.id.toIntOrNull() ?: 45
                    } else {
                        selectedOption.id
                    }

                    answers[question.fieldName] = value
                    onAnswerChanged(question.fieldName, value)
                }
            }
        }

        private fun setupMultiChoice(question: ProfileQuestion) {
            cgMultiChoice.visibility = View.VISIBLE
            cgMultiChoice.removeAllViews()

            @Suppress("UNCHECKED_CAST")
            val previousAnswers = answers[question.fieldName] as? List<String> ?: emptyList()

            val context = itemView.context

            question.options.forEach { option ->
                val chip = Chip(context).apply {
                    text = option.label
                    isCheckable = true
                    isChecked = previousAnswers.contains(option.id)
                    textSize = 14f

                    // Use color state lists for proper checked/unchecked states
                    chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_background_color)
                    chipStrokeColor = ContextCompat.getColorStateList(context, R.color.chip_stroke_color)
                    setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_color))
                    chipStrokeWidth = 2f

                    // Hide the checkmark icon for cleaner look
                    isCheckedIconVisible = false

                    setOnCheckedChangeListener { _, isChecked ->
                        updateMultiChoiceAnswer(question.fieldName, option.id, isChecked)
                    }
                }

                cgMultiChoice.addView(chip)
            }
        }

        private fun updateMultiChoiceAnswer(fieldName: String, optionId: String, isSelected: Boolean) {
            @Suppress("UNCHECKED_CAST")
            val currentList = (answers[fieldName] as? List<String>)?.toMutableList() ?: mutableListOf()

            if (isSelected && !currentList.contains(optionId)) {
                currentList.add(optionId)
            } else if (!isSelected) {
                currentList.remove(optionId)
            }

            answers[fieldName] = currentList
            onAnswerChanged(fieldName, currentList)
        }

        private fun setupSlider(question: ProfileQuestion) {
            sliderContainer.visibility = View.VISIBLE

            slider.valueFrom = question.minValue.toFloat()
            slider.valueTo = question.maxValue.toFloat()

            // Restore previous answer or use default
            val previousAnswer = when (val ans = answers[question.fieldName]) {
                is Int -> ans.toFloat()
                is Float -> ans
                is Number -> ans.toFloat()
                else -> question.defaultValue.toFloat()
            }

            slider.value = previousAnswer.coerceIn(slider.valueFrom, slider.valueTo)

            tvSliderUnit.text = question.unit
            tvSliderMin.text = "${question.minValue} ${question.unit}"
            tvSliderMax.text = "${question.maxValue} ${question.unit}"

            updateSliderDisplay(slider.value.toInt(), question.unit)

            slider.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    val intValue = value.toInt()
                    updateSliderDisplay(intValue, question.unit)

                    // Store as appropriate type based on field
                    val finalValue: Any = if (question.fieldName == "currentWeightKg" ||
                                               question.fieldName == "targetWeightKg") {
                        value // Keep as float for weight
                    } else {
                        intValue // Int for others
                    }

                    answers[question.fieldName] = finalValue
                    onAnswerChanged(question.fieldName, finalValue)
                }
            }

            // Initialize answer if not set
            if (!answers.containsKey(question.fieldName)) {
                val initialValue: Any = if (question.fieldName == "currentWeightKg" ||
                                            question.fieldName == "targetWeightKg") {
                    slider.value
                } else {
                    slider.value.toInt()
                }
                answers[question.fieldName] = initialValue
            }
        }

        private fun updateSliderDisplay(value: Int, unit: String) {
            tvSliderValue.text = value.toString()
        }

        private fun setupDatePicker(question: ProfileQuestion) {
            datePickerContainer.visibility = View.VISIBLE

            val previousAnswer = answers[question.fieldName] as? String
            if (previousAnswer != null && previousAnswer.isNotEmpty()) {
                tvSelectedDate.text = formatDateForDisplay(previousAnswer)
            } else {
                tvSelectedDate.text = itemView.context.getString(R.string.select_your_birthday)
            }

            btnSelectDate.setOnClickListener {
                showDatePickerDialog(question)
            }

            tvSelectedDate.setOnClickListener {
                showDatePickerDialog(question)
            }
        }

        private fun showDatePickerDialog(question: ProfileQuestion) {
            val calendar = Calendar.getInstance()

            // If we have a previous answer, parse it
            val previousAnswer = answers[question.fieldName] as? String
            if (previousAnswer != null && previousAnswer.isNotEmpty()) {
                try {
                    val parts = previousAnswer.split("-")
                    calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                } catch (e: Exception) {
                    // Use default (25 years ago)
                    calendar.add(Calendar.YEAR, -25)
                }
            } else {
                // Default to 25 years ago
                calendar.add(Calendar.YEAR, -25)
            }

            DatePickerDialog(
                itemView.context,
                { _, year, month, dayOfMonth ->
                    val dateString = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    answers[question.fieldName] = dateString
                    tvSelectedDate.text = formatDateForDisplay(dateString)
                    onAnswerChanged(question.fieldName, dateString)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                // Set max date to today (can't be born in the future)
                datePicker.maxDate = System.currentTimeMillis()
                // Set min date to 100 years ago
                val minCalendar = Calendar.getInstance()
                minCalendar.add(Calendar.YEAR, -100)
                datePicker.minDate = minCalendar.timeInMillis
            }.show()
        }

        private fun formatDateForDisplay(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }
    }

    // Save text inputs when leaving the page
    fun saveCurrentTextInput(position: Int) {
        // This would be called from the activity before page change
        // to ensure text input is saved
    }
}
