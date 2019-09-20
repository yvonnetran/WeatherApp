package hu.ait.w

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import android.widget.Spinner
import hu.ait.w.data.City
import kotlinx.android.synthetic.main.new_city_dialog.view.*
import kotlinx.android.synthetic.main.city_row.view.*
import java.lang.RuntimeException
import java.util.*

class CityDialog : DialogFragment() {

    interface CityHandler {
        fun cityCreated(item: City)
        fun cityUpdated(item: City)
    }

    private lateinit var cityHandler: CityHandler

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is CityHandler) {
            cityHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the CityHandlerInterface")
        }
    }
    private lateinit var etCityName: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New city")

        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_city_dialog, null
        )
        etCityName = rootView.etCityName

        builder.setView(rootView)

        builder.setPositiveButton("OK") {
                dialog, witch -> // empty
        }

        return builder.create()
    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etCityName.text.isNotEmpty()) {
                handleCityCreate()
                dialog.dismiss()
            } else {
                    etCityName.error = "This field cannot be empty"
            }
        }
    }

    private fun handleCityCreate() {
        cityHandler.cityCreated(
            City(
                null,
                etCityName.text.toString()
            )
        )
    }
}