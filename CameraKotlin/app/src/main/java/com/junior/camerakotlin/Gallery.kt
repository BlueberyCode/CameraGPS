package com.junior.camerakotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView

class Gallery : Activity() {
    var imageView: ImageView? = null
    var button: Button? = null
    var imageUri: Uri? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        imageView = findViewById<View>(R.id.imageView) as ImageView
        button = findViewById<View>(R.id.buttonLoadPicture) as Button
        button!!.setOnClickListener { openGallery() }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.data
            imageView!!.setImageURI(imageUri)
        }

    }

    companion object {
        private const val PICK_IMAGE = 100
    }
}