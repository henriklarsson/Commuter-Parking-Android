package se.larsson.parking.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import se.larsson.parking.R


class ImageDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!)
        val token = arguments?.getString(ImageDialogFragment.TOKEN)
        val handle = arguments?.getString(ImageDialogFragment.CAMERA_NUMBER)
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val layoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.image_dialog, null)
        dialog.setContentView(view)
        val imageView = view.findViewById(R.id.imageView) as ImageView
        view.setOnClickListener {
            dialog.dismiss()
        }
        imageView.visibility = View.VISIBLE
        Glide.with(context!!)
            .load(getUrl(token!!, handle!!))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setTitle("Item Details")
        dialog.show()

        return dialog
    }

    private fun getUrl(token: String, handle: String): GlideUrl {
        return  GlideUrl(
            "https://api.vasttrafik.se/spp/v3/parkingImages/$handle", LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        )
    }

    companion object {
        val TOKEN: String = "TOKEN"
        val CAMERA_NUMBER: String = "CAMERA_NUMBER"
    }
}