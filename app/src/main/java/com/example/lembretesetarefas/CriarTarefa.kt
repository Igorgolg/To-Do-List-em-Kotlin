package com.example.lembretesetarefas

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.lembretesetarefas.Model.Task
import com.example.lembretesetarefas.databinding.ActivityCriarTarefaBinding
import com.example.lembretesetarefas.datasource.TaskDataSource
import com.example.lembretesetarefas.extensions.format
import com.example.lembretesetarefas.extensions.text
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class CriarTarefa : AppCompatActivity() {
    private lateinit var binding: ActivityCriarTarefaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarTarefaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID,0)
            TaskDataSource.findById(taskId)?.let{
                binding.textTitle.text = it.title
                binding.textHour.text = it.hour
                binding.textDate.text = it.date
            }
        }
        initListeners()

    }

    private fun initListeners() {
        //Calendário
        binding.textDate.editText?.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione a Data")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.addOnPositiveButtonClickListener {
                //Respondsavel por pegar a data atual corretamente
                val timeZone = TimeZone.getDefault()
                val offSet = timeZone.getOffset(Date().time) * -1
                binding.textDate.text = (Date(it + offSet).format())
            }

            datePicker.show(supportFragmentManager,"DATE_PICKER")
        }

        binding.textHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .setTitleText("Selecione a Hora")
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val hour = if(timePicker.hour in 0..9)"0${timePicker.hour}" else timePicker.hour
                val minute = if(timePicker.minute in 0..9)"0${timePicker.minute}" else timePicker.minute
                binding.textHour.text = ("${hour}:${minute}")
            }
            timePicker.show(supportFragmentManager,"TIME_PICKER")
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnMakeTask.setOnClickListener {
           if(binding.textTitle.text != "" && binding.textHour.text != "" && binding.textDate.text != "" ){val task = Task(
               title = binding.textTitle.text,
               date = binding.textDate.text,
               hour = binding.textHour.text,
               id = intent.getIntExtra(TASK_ID,0)

           )
               TaskDataSource.insertTask(task)
               setResult(Activity.RESULT_OK)
               finish()} else {
               val text = "Preencha os Campos em Branco"
               val duration = Toast.LENGTH_SHORT

               val toast = Toast.makeText(applicationContext, text, 2000)
               toast.show()

           }
        }

    }
companion object{
    const val TASK_ID = "task_id"
}

}

