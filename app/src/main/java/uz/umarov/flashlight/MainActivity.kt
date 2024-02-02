package uz.umarov.flashlight

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import uz.umarov.flashlight.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var flashlightStatus = false
    private lateinit var rootView: View
    private lateinit var cameraManager: CameraManager
    private var camera: Camera? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        rootView = binding.root
        setContentView(rootView)

        val isEnabled = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!isEnabled) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                50
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d("FlashlightApp", "camera2 selected");
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        } else {
            Log.d("FlashlightApp", "camera1 selected");
            camera = Camera.open()
        }

        binding.button.setOnClickListener {
            if (checkCameraHardware(this)) {
                if (flashlightStatus) {
                    flashlightOff()
                } else {
                    flashlightOn()
                }
            } else {
                Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Check if this device has a camera */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun flashlightOn() {
        flashlightStatus = true
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, flashlightStatus)
        } else {
            camera?.let {
                val p = it.parameters
                p.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                it.parameters = p
                it.startPreview()
            }
        }
    }

    private fun flashlightOff() {
        flashlightStatus = false
        rootView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, flashlightStatus)
        } else {
            camera?.let {
                val p = it.parameters
                p.flashMode = Camera.Parameters.FLASH_MODE_OFF
                it.parameters = p
                it.startPreview()
            }
        }
    }
}
