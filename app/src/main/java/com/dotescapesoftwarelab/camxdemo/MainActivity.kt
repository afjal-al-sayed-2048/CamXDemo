package com.dotescapesoftwarelab.camxdemo

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.AspectRatio.RATIO_4_3
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.dotescapesoftwarelab.camxdemo.databinding.ActivityMainBinding
import com.dotescapesoftwarelab.camxdemo.databinding.SampleLayoutBinding
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: SampleLayoutBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var objectDetector: ObjectDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SampleLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObjectDetector()
        startCamera()

        //binding.captureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun setupObjectDetector(){
        val localModel = LocalModel.Builder()
            .setAssetFilePath("object_detection_model.tflite")
            .build()
        val objectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.8F)
            .setMaxPerObjectLabelCount(3)
            .build()
        objectDetector = ObjectDetection.getClient(objectDetectorOptions)
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(720, 1280))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this)){imageProxy ->
                    val image = imageProxy.image
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    image?.let { img ->
                        Log.d("test", "image came")
                        val inputImage = InputImage.fromMediaImage(img, rotationDegrees)
                        objectDetector.process(inputImage)
                            .addOnSuccessListener { detectedObjects ->
                                for(detectedObject in detectedObjects){
                                    var labels = ""
                                    for(label in detectedObject.labels){
                                        Log.d("Detected: ", label.text)
                                        if(labels == "") {
                                            labels = label.text
                                        }else{
                                            labels = "$labels, ${label.text}"
                                        }
                                    }
                                    if (binding.parentlayout.childCount > 1) {
                                        binding.parentlayout.removeViewAt(1)
                                    }
                                    val rect: Rect = detectedObject.getBoundingBox()
                                    var text = labels
                                    /*if (detectedObject.getLabels().size != 0) {
                                        text = detectedObject.labels[0].text
                                    }*/

                                    val drawGraphic = DrawOverlay(this, rect = rect, text = text)
                                    binding.parentlayout.addView(drawGraphic)
                                }
                                //binding.labelViewer.text = labels
                                if(detectedObjects.size < 1){
                                    if (binding.parentlayout.childCount > 1) {
                                        binding.parentlayout.removeViewAt(1)
                                    }
                                }
                                imageProxy.close()
                            }
                            .addOnFailureListener{
                                it.printStackTrace()
                                imageProxy.close()
                            }
                    }
                }

                imageCapture = ImageCapture.Builder()
                    .build()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        imageAnalysis,
                        preview
                    )
                }catch (e: Exception){
                    e.printStackTrace()
                }
            },
            ContextCompat.getMainExecutor(this)
        )
    }
    private fun takePhoto(){

    }
}