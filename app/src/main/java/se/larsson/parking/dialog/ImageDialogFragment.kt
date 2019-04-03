package se.larsson.parking.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import se.larsson.parking.R


class ImageDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context)

        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val layoutInflater = activity!!
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.image_dialog, null)
        dialog.setContentView(view)



//
//
      val imageView = view.findViewById(R.id.imageView) as ImageView
        imageView.visibility = View.VISIBLE


        dialog.setTitle("Item Details")
        dialog.show()

        return dialog
    }
}