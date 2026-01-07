package com.example.ai_fitness_workout_manager.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ai_fitness_workout_manager.R
import com.example.ai_fitness_workout_manager.firebase.FirebaseAuthManager
import com.example.ai_fitness_workout_manager.firebase.FirebaseDbManager
import com.example.ai_fitness_workout_manager.model.MealEntry
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddMealBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var chipGroupMealType: ChipGroup
    private lateinit var chipBreakfast: Chip
    private lateinit var chipLunch: Chip
    private lateinit var chipDinner: Chip
    private lateinit var chipSnack: Chip

    private lateinit var tilMealName: TextInputLayout
    private lateinit var etMealName: TextInputEditText
    private lateinit var etMealDescription: TextInputEditText
    private lateinit var btnSelectTime: MaterialButton

    private lateinit var etCalories: TextInputEditText
    private lateinit var etProtein: TextInputEditText
    private lateinit var etCarbs: TextInputEditText
    private lateinit var etFat: TextInputEditText
    private lateinit var etFiber: TextInputEditText

    private lateinit var btnSaveMeal: MaterialButton

    private var selectedHour: Int = 12
    private var selectedMinute: Int = 0
    private var selectedDate: Date = Date()

    private var onMealAddedListener: (() -> Unit)? = null

    companion object {
        private const val ARG_DATE = "arg_date"

        fun newInstance(date: Date): AddMealBottomSheetFragment {
            return AddMealBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_DATE, date.time)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedDate = Date(it.getLong(ARG_DATE, System.currentTimeMillis()))
        }

        // Set initial time based on current time
        val calendar = Calendar.getInstance()
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
        selectedMinute = calendar.get(Calendar.MINUTE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupMealTypeSelection()
        setupTimePicker()
        setupSaveButton()
        setDefaultMealType()
        updateTimeButtonText()
    }

    override fun onStart() {
        super.onStart()
        // Expand the bottom sheet fully
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                // Set max height to 90% of screen
                sheet.layoutParams.height = (resources.displayMetrics.heightPixels * 0.9).toInt()
            }
        }
    }

    private fun initViews(view: View) {
        chipGroupMealType = view.findViewById(R.id.chipGroupMealType)
        chipBreakfast = view.findViewById(R.id.chipBreakfast)
        chipLunch = view.findViewById(R.id.chipLunch)
        chipDinner = view.findViewById(R.id.chipDinner)
        chipSnack = view.findViewById(R.id.chipSnack)

        tilMealName = view.findViewById(R.id.tilMealName)
        etMealName = view.findViewById(R.id.etMealName)
        etMealDescription = view.findViewById(R.id.etMealDescription)
        btnSelectTime = view.findViewById(R.id.btnSelectTime)

        etCalories = view.findViewById(R.id.etCalories)
        etProtein = view.findViewById(R.id.etProtein)
        etCarbs = view.findViewById(R.id.etCarbs)
        etFat = view.findViewById(R.id.etFat)
        etFiber = view.findViewById(R.id.etFiber)

        btnSaveMeal = view.findViewById(R.id.btnSaveMeal)
    }

    private fun setupMealTypeSelection() {
        // Clear error when a chip is selected
        chipGroupMealType.setOnCheckedStateChangeListener { _, _ ->
            // Reset any visual error state if needed
        }
    }

    private fun setDefaultMealType() {
        // Set default meal type based on current time
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..10 -> chipBreakfast.isChecked = true
            hour in 11..14 -> chipLunch.isChecked = true
            hour in 15..17 -> chipSnack.isChecked = true
            hour in 18..22 -> chipDinner.isChecked = true
            else -> chipSnack.isChecked = true
        }
    }

    private fun setupTimePicker() {
        btnSelectTime.setOnClickListener {
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                selectedHour = hourOfDay
                selectedMinute = minute
                updateTimeButtonText()
            },
            selectedHour,
            selectedMinute,
            false // Use 12-hour format
        ).show()
    }

    private fun updateTimeButtonText() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        btnSelectTime.text = timeFormat.format(calendar.time)
    }

    private fun getSelectedMealType(): String? {
        return when (chipGroupMealType.checkedChipId) {
            R.id.chipBreakfast -> MealEntry.TYPE_BREAKFAST
            R.id.chipLunch -> MealEntry.TYPE_LUNCH
            R.id.chipDinner -> MealEntry.TYPE_DINNER
            R.id.chipSnack -> MealEntry.TYPE_SNACK
            else -> null
        }
    }

    private fun setupSaveButton() {
        btnSaveMeal.setOnClickListener {
            if (validateInput()) {
                saveMeal()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        // Validate meal name
        val mealName = etMealName.text?.toString()?.trim()
        if (mealName.isNullOrEmpty()) {
            tilMealName.error = getString(R.string.error_meal_name_required)
            isValid = false
        } else {
            tilMealName.error = null
        }

        // Validate meal type
        if (getSelectedMealType() == null) {
            Toast.makeText(requireContext(), R.string.error_meal_type_required, Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun saveMeal() {
        val userId = FirebaseAuthManager.currentUserId
        if (userId == null) {
            Toast.makeText(requireContext(), R.string.error_not_logged_in, Toast.LENGTH_SHORT).show()
            return
        }

        // Disable save button to prevent double-taps
        btnSaveMeal.isEnabled = false

        val mealName = etMealName.text?.toString()?.trim() ?: ""
        val mealDescription = etMealDescription.text?.toString()?.trim() ?: ""
        val mealType = getSelectedMealType() ?: MealEntry.TYPE_SNACK

        val calories = etCalories.text?.toString()?.toIntOrNull() ?: 0
        val protein = etProtein.text?.toString()?.toFloatOrNull() ?: 0f
        val carbs = etCarbs.text?.toString()?.toFloatOrNull() ?: 0f
        val fat = etFat.text?.toString()?.toFloatOrNull() ?: 0f
        val fiber = etFiber.text?.toString()?.toFloatOrNull() ?: 0f

        // Format the time string
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
        }
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeString = timeFormat.format(calendar.time)

        // Create timestamp for ordering (combine selected date with selected time)
        val mealCalendar = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
        }

        val meal = MealEntry(
            name = mealName,
            portion = mealDescription,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fiber = fiber,
            mealType = mealType,
            time = timeString,
            timestamp = mealCalendar.timeInMillis
        )

        val dateStr = FirebaseDbManager.formatDateForDb(selectedDate)

        FirebaseDbManager.addMeal(
            userId = userId,
            meal = meal,
            date = dateStr,
            onSuccess = { mealId ->
                Toast.makeText(requireContext(), R.string.meal_saved, Toast.LENGTH_SHORT).show()
                dismiss()
                // Call listener AFTER dismissing to ensure HomeFragment is visible when it refreshes
                onMealAddedListener?.invoke()
            },
            onError = { error ->
                btnSaveMeal.isEnabled = true
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_saving_meal, error),
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    fun setOnMealAddedListener(listener: () -> Unit) {
        onMealAddedListener = listener
    }
}
