package com.junior.camerakotlin

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Engine
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG="CameraView"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        recordButton.isClickable=false
        Dexter.withContext(this).withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    //some code
                    recordButton.isClickable = true
                } else if (report.isAnyPermissionPermanentlyDenied) {
                    recordButton.isClickable = false
                    showAlertDialog()
                }
            }


            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                recordButton.isClickable = false
                token!!.continuePermissionRequest()
            }
        }).onSameThread().check()

        cameraView.setLifecycleOwner(this)
        cameraView.mode=Mode.PICTURE
        cameraView.engine=Engine.CAMERA2
        cameraView.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bm ->
                    val imageName = "image_" + DateFormat.format(
                            "yyyy_MM_dd_HH_mm_ss",
                            Calendar.getInstance()
                    )
                            .toString()
                    val folder = File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                    .path + File.separator + "CameraView"
                    )
                    val file = File(folder.path + File.separator + imageName + ".jpg")
                    if (!folder.exists()) {
                        val folderCreated = folder.mkdir()
                        if (folderCreated) {
                            Log.d(TAG, "folder created")
                        }
                    } else {
                        Log.d(TAG, "folder is exists")
                    }
                    val bitmap = Bitmap.createBitmap(bm!!)
                    val out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                }
            }

        })
        flipCamera.setOnClickListener(this)
        recordButton.setOnClickListener(this)
        flash.setOnClickListener(this)
        flashOn.setOnClickListener(this)
        flashOff.setOnClickListener(this)
        flashAuto.setOnClickListener(this)
        flashTorch.setOnClickListener(this)
        gallery.setOnClickListener(this)
    }

    private fun showAlertDialog() {
        val alertDialog=AlertDialog.Builder(this)
        alertDialog.setTitle("App needs permission")
        alertDialog.setPositiveButton("Go to settings"){

            dialog: DialogInterface, _->
            dialog.cancel()
            openSettings()
        }
        alertDialog.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivityForResult(intent, 101)
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.recordButton -> {
                flashLayout.visibility = View.GONE
                if (cameraView.mode == Mode.PICTURE) {
                    cameraView.takePicture()
                }

                Toast.makeText(applicationContext, "BUTTON", Toast.LENGTH_SHORT).show()

            }
            R.id.flipCamera -> {
                flashLayout.visibility = View.GONE
                if (!cameraView.isTakingVideo) {
                    if (cameraView.facing == Facing.BACK) {
                        cameraView.facing = Facing.FRONT
                    } else if (cameraView.facing == Facing.FRONT) {
                        cameraView.facing = Facing.BACK
                    }
                    Toast.makeText(applicationContext, "MODE", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.flash -> {
                if (cameraView.mode == Mode.PICTURE) {
                    if (flashLayout.visibility == View.GONE) {
                        flashLayout.visibility = View.VISIBLE
                    } else if (flashLayout.visibility == View.VISIBLE) {
                        flashLayout.visibility = View.GONE
                    }
                }

            }
            R.id.flashTorch -> {
                cameraView.flash = Flash.TORCH
                flashLayout.visibility = View.GONE
                flash.setImageResource(R.drawable.ic_torch)
            }
            R.id.flashAuto -> {
                cameraView.flash = Flash.AUTO
                flashLayout.visibility = View.GONE
                flash.setImageResource(R.drawable.ic_flash_auto)
            }
            R.id.flashOn -> {
                cameraView.flash = Flash.ON
                flashLayout.visibility = View.GONE
                flash.setImageResource(R.drawable.ic_flash_on_red)
            }
            R.id.flashOff -> {
                cameraView.flash = Flash.OFF
                flashLayout.visibility = View.GONE
                flash.setImageResource(R.drawable.ic_flash_off_red)
            }
            R.id.gallery -> {
                val intent = Intent(this, Gallery::class.java) // (1) (2)
                startActivity(intent)
            }
        }
    }


}