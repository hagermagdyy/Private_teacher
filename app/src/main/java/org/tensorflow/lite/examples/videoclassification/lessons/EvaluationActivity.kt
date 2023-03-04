package org.tensorflow.lite.examples.videoclassification.lessons

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.hardware.camera2.CaptureRequest
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.Range
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.tensorflow.lite.examples.videoclassification.CalculateUtils
import org.tensorflow.lite.examples.videoclassification.R
import org.tensorflow.lite.examples.videoclassification.databinding.EvaluationActivityBinding
import org.tensorflow.lite.examples.videoclassification.ml.VideoClassifier
import org.tensorflow.lite.support.label.Category
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@androidx.camera.core.ExperimentalGetImage
@androidx.camera.camera2.interop.ExperimentalCamera2Interop
class EvaluationActivity : AppCompatActivity() {

    private var videoCapture: VideoCapture<Recorder>? = null


    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        private val videoCapture: VideoCapture<Recorder> = VideoCapture.withOutput(recorder)
        private var recording: Recording? = null
        private var videoRecordEvent: VideoRecordEvent? = null

        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val TAG = "TFLite-VidClassify"
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private const val MAX_RESULT = 3
        private var recorded = false
        private lateinit var sharedPreference: SharedPreferences

        //        private const val MODEL_MOVINET_A0_FILE = "movinet_a0_stream_int8.tflite"
//        private const val MODEL_MOVINET_A1_FILE = "movinet_a1_stream_int8.tflite"
        private const val MODEL_MOVINET_A2_FILE = "movinet_a2_stream_int8.tflite"
        private const val MODEL_LABEL_FILE = "kinetics600_label_map.txt"
        private const val MODEL_FPS = 5 // Ensure the input images are fed to the model at this fps.
        private const val MODEL_FPS_ERROR_RANGE = 0.1 // Acceptable error range in fps.
        private const val MAX_CAPTURE_FPS = 20
    }

    private val lock = Any()
    private lateinit var binding: EvaluationActivityBinding
    private lateinit var executor: ExecutorService
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    lateinit var mp: MediaPlayer

    private var videoClassifier: VideoClassifier? = null
    private var numThread = 1
    private var modelPosition = 0
    private var lastInferenceStartTime: Long = 0
    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            // do nothing
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            modelPosition = position
            createClassifier()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Initialize the view layout.
        binding = EvaluationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreference =
            this@EvaluationActivity.getSharedPreferences("Parcelable", Context.MODE_PRIVATE)


        sharedPreference.edit().putBoolean("correct_sport", false).apply()

        mp = MediaPlayer.create(this@EvaluationActivity, R.raw.before_recording)
        mp.start()
        AlertDialog.Builder(this)
            .setTitle("تنبيه!")
            .setMessage(
                "قبل البدء فى الاداء بنفسك\n" +
                        "قم بتثبيت الهاتف على حامل ثلاثى أولاً وتأكد من تشغيل الكاميرا الامامية وابتعد عن الضوضاء أو أى مثير خارجى"
            ) // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(
                R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    mp.stop()
                    captureVideo()
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .show()

        // Initialize the bottom sheet.
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetLayout)
        binding.bottomSheet.gestureLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.bottomSheet.gestureLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = binding.bottomSheet.gestureLayout.measuredHeight
                sheetBehavior.peekHeight = height
            }
        })
        sheetBehavior.isHideable = false
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.bottomSheet.bottomSheetArrow.setImageResource(R.drawable.icn_chevron_down)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_SETTLING -> {
                        binding.bottomSheet.bottomSheetArrow.setImageResource(R.drawable.icn_chevron_up)
                    }
                    else -> {
                        // do nothing.
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // no func
            }

        })
        binding.bottomSheet.threads.text = numThread.toString()
        binding.bottomSheet.minus.setOnClickListener {
            if (numThread <= 1) return@setOnClickListener
            numThread--
            binding.bottomSheet.threads.text = numThread.toString()
            createClassifier()
        }
        binding.bottomSheet.plus.setOnClickListener {
            if (numThread >= 4) return@setOnClickListener
            numThread++
            binding.bottomSheet.threads.text = numThread.toString()
            createClassifier()
        }
        binding.bottomSheet.btnClearModelState.setOnClickListener {
            videoClassifier?.reset()
        }
        initSpinner()

        // Start the camera.
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return


        val curRecording = recording
        if (curRecording != null) {
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(this@EvaluationActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {

                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                            sendEmail(recordEvent.outputResults.outputUri)

                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                            Toast.makeText(this@EvaluationActivity, recordEvent.error.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }


    private fun sendEmail(outPutUri: Uri) {

        val phone = "01025054388"


        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_STREAM, outPutUri)
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedPreference.getString("score", ""))
        sendIntent.putExtra("jid", "$phone@s.whatsapp.net") //phone number without "+" prefix

        sendIntent.setPackage("com.whatsapp")
        if (intent.resolveActivity(this.packageManager) == null) {
//            Toast.makeText(this, "Error/n" + e.toString(), Toast.LENGTH_SHORT).show()
            return
        }
        startActivity(sendIntent)

//        val sendIntent = Intent(Intent.ACTION_SENDTO)
//        sendIntent.action = Intent.ACTION_SENDTO
//        sendIntent.type = "video/*"
//        sendIntent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video")
//        startActivity(Intent.createChooser(sendIntent, "01025054388"))
//
//         sendIntent.setPackage("com.whatsapp");
//
//        startActivity(sendIntent)


    }

    /**
     * Initialize the spinner to let users change the TFLite model.
     */
    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.tfe_pe_models_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.bottomSheet.spnSelectModel.adapter = adapter
            binding.bottomSheet.spnSelectModel.setSelection(modelPosition)
        }
        binding.bottomSheet.spnSelectModel.onItemSelectedListener = changeModelListener
    }

    /**
     * Start the image capturing pipeline.
     */
    private fun startCamera() {
        executor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Create a Preview to show the image captured by the camera on screen.
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.preview.surfaceProvider)
                }

            try {
                // Unbind use cases before rebinding.
                cameraProvider.unbindAll()

                // Create an ImageAnalysis to continuously capture still images using the camera,
                // and feed them to the TFLite model. We set the capturing frame rate to a multiply
                // of the TFLite model's desired FPS to keep the preview smooth, then drop
                // unnecessary frames during image analysis.
                val targetFpsMultiplier = MAX_CAPTURE_FPS.div(MODEL_FPS)
                val targetCaptureFps = MODEL_FPS * targetFpsMultiplier
                val builder = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                val extender: Camera2Interop.Extender<*> = Camera2Interop.Extender(builder)
                extender.setCaptureRequestOption(
                    CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                    Range(targetCaptureFps, targetCaptureFps)
                )
                val imageAnalysis = builder.build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    processImage(imageProxy)
                }

                // Combine the ImageAnalysis and Preview into a use case group.
                val useCaseGroup = UseCaseGroup.Builder()
                    .addUseCase(preview)
                    .addUseCase(imageAnalysis)
                    .addUseCase(videoCapture!!)
                    .setViewPort(binding.preview.viewPort!!)
                    .build()

                // Bind use cases to camera.
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, useCaseGroup
                )

            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed.", e)
            }

        }, ContextCompat.getMainExecutor(this))

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
    }

    /**
     * Run a frames received from the camera through the TFLite video classification pipeline.
     */
    private fun processImage(imageProxy: ImageProxy) {
        // Ensure that only one frame is processed at any given moment.
        synchronized(lock) {
            val currentTime = SystemClock.uptimeMillis()
            val diff = currentTime - lastInferenceStartTime

            // Check to ensure that we only run inference at a frequency required by the
            // model, within an acceptable error range (e.g. 10%). Discard the frames
            // that comes too early.
            if (diff * MODEL_FPS >= 1000 /* milliseconds */ * (1 - MODEL_FPS_ERROR_RANGE)) {
                lastInferenceStartTime = currentTime

                val image = imageProxy.image
                image?.let {
                    videoClassifier?.let { classifier ->
                        // Convert the captured frame to Bitmap.
                        val imageBitmap = Bitmap.createBitmap(
                            it.width,
                            it.height,
                            Bitmap.Config.ARGB_8888
                        )
                        CalculateUtils.yuvToRgb(image, imageBitmap)

                        // Rotate the image to the correct orientation.
                        val rotateMatrix = Matrix()
                        rotateMatrix.postRotate(
                            imageProxy.imageInfo.rotationDegrees.toFloat()
                        )
                        val rotatedBitmap = Bitmap.createBitmap(
                            imageBitmap, 0, 0, it.width, it.height,
                            rotateMatrix, false
                        )

                        // Run inference using the TFLite model.
                        val startTimeForReference = SystemClock.uptimeMillis()
                        val results = classifier.classify(rotatedBitmap)
                        val endTimeForReference =
                            SystemClock.uptimeMillis() - startTimeForReference
                        val inputFps = 1000f / diff
                        showResults(results, endTimeForReference, inputFps)

                        if (inputFps < MODEL_FPS * (1 - MODEL_FPS_ERROR_RANGE)) {
                            Log.w(
                                TAG, "Current input FPS ($inputFps) is " +
                                        "significantly lower than the TFLite model's " +
                                        "expected FPS ($MODEL_FPS). It's likely because " +
                                        "model inference takes too long on this device."
                            )
                        }
                    }
                }
            }
            imageProxy.close()
        }
    }

    /**
     * Check whether camera permission is already granted.
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Initialize the TFLite video classifier.
     */
    private fun createClassifier() {
        synchronized(lock) {
            if (videoClassifier != null) {
                videoClassifier?.close()
                videoClassifier = null
            }
            val options =
                VideoClassifier.VideoClassifierOptions.builder()
                    .setMaxResult(MAX_RESULT)
                    .setNumThreads(numThread)
                    .build()
            val modelFile = MODEL_MOVINET_A2_FILE

            videoClassifier = VideoClassifier.createFromFileAndLabelsAndOptions(
                this,
                modelFile,
                MODEL_LABEL_FILE,
                options
            )

            // show input size of video classification
            videoClassifier?.getInputSize()?.let {
                binding.bottomSheet.inputSizeInfo.text =
                    getString(R.string.frame_size, it.width, it.height)
            }
            Log.d(TAG, "Classifier created.")
        }
    }

    /**
     * Show the video classification results on the screen.
     */

    private fun showResults(labels: List<Category>, inferenceTime: Long, inputFps: Float) {
        runOnUiThread {

            if (sharedPreference.getInt("lesson", 0) == 1) {
                if (labels[0].label.equals("air drumming") || labels[0].label.equals("running on treadmill") || labels[0].label.equals(
                        "jogging"
                    )
                    || labels[1].label.equals("air drumming") || labels[1].label.equals("running on treadmill") || labels[1].label.equals(
                        "jogging"
                    ) ||
                    labels[2].label.equals("air drumming") || labels[2].label.equals("running on treadmill") || labels[2].label.equals(
                        "jogging"
                    ) ||
                    labels[3].label.equals("air drumming") || labels[3].label.equals("running on treadmill") || labels[3].label.equals(
                        "jogging"
                    ) ||
                    labels[4].label.equals("air drumming") || labels[4].label.equals("running on treadmill") || labels[4].label.equals(
                        "jogging"
                    )
                ) {
                    binding.btnTakePhoto.setBackgroundColor(Color.parseColor("#1B8D23"))
                    sharedPreference.edit().putBoolean("correct_sport", true).apply()

                    binding.btnTakePhoto.setOnClickListener {
                        doSomethingOnce(labels)
                    }

                } else {
                    binding.btnTakePhoto.setOnClickListener {
                        if (sharedPreference.getBoolean("correct_sport", true)) {
                            doSomethingOnce(labels)
                        } else {
                            // Toast.makeText(this, labels[0].label.toString().trim(), Toast.LENGTH_SHORT).show()

                            mp = MediaPlayer.create(this@EvaluationActivity, R.raw.wrong_sport)
                            mp.start()
                            AlertDialog.Builder(this)
                                .setTitle("خطأ!")
                                .setMessage(
                                    "خطأ في اداء التمرين "
                                ) // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(
                                    R.string.yes,
                                    DialogInterface.OnClickListener { dialog, which ->
                                        mp.stop()
                                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                                .show()
                        }
                    }
                }
            }

            if (sharedPreference.getInt("lesson", 0) == 2) {
                if (labels[0].label.equals("catching or throwing frisbee") || labels[0].label.equals(
                        "playing basketball"
                    ) || labels[0].label.equals("shooting basketball") || labels[0].label.equals("catching or throwing baseball")
                    || labels[1].label.equals("catching or throwing frisbee") || labels[1].label.equals(
                        "playing basketball"
                    ) || labels[1].label.equals("shooting basketball") || labels[1].label.equals("catching or throwing baseball")
                    || labels[2].label.equals("catching or throwing frisbee") || labels[2].label.equals(
                        "playing basketball"
                    ) || labels[2].label.equals("shooting basketball") || labels[2].label.equals("catching or throwing baseball")
                    || labels[3].label.equals("catching or throwing frisbee") || labels[3].label.equals(
                        "playing basketball"
                    ) || labels[3].label.equals("shooting basketball") || labels[3].label.equals("catching or throwing baseball")
                    || labels[4].label.equals("catching or throwing frisbee") || labels[4].label.equals(
                        "playing basketball"
                    ) || labels[4].label.equals("shooting basketball") || labels[4].label.equals("catching or throwing baseball")
                ) {
                    binding.btnTakePhoto.setBackgroundColor(Color.parseColor("#1B8D23"))
                    sharedPreference.edit().putBoolean("correct_sport", true).apply()

                    binding.btnTakePhoto.setOnClickListener {
                        doSomethingOnce(labels)
                    }

                } else {

                    binding.btnTakePhoto.setOnClickListener {

                        if (sharedPreference.getBoolean("correct_sport", true)) {
                            doSomethingOnce(labels)
                        } else {
                            // Toast.makeText(this, labels[0].label.toString().trim(), Toast.LENGTH_SHORT).show()

                            mp = MediaPlayer.create(this@EvaluationActivity, R.raw.wrong_lesson1)
                            mp.start()
                            AlertDialog.Builder(this)
                                .setTitle("خطأ!")
                                .setMessage(
                                    "خطأ في اداء التمرين "
                                ) // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(
                                    R.string.yes,
                                    DialogInterface.OnClickListener { dialog, which ->
                                        mp.stop()
                                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                                .show()
                        }
                    }
                }
            }


//            binding.btnTakePhoto.setOnClickListener {
//                stopCamera(labels)
//            }
//
//            if (labels[0].label.equals("air drumming") || labels[0].label.equals("stretching arm")
//                || labels[1].label.equals("air drumming") || labels[1].label.equals("stretching arm") ||
//                labels[2].label.equals("air drumming") || labels[2].label.equals("stretching arm")
//            ) {
//                binding.bottomSheet.tvDetectedItem0Value.text = "حركة صحيحة"
//
//            }else{
//                binding.bottomSheet.tvDetectedItem0Value.text = "حركة خاطئة"
//
//            }
            //         binding.bottomSheet.tvDetectedItem0.text = labels[0].label
//            binding.bottomSheet.tvDetectedItem1.text = labels[1].label
//            binding.bottomSheet.tvDetectedItem2.text = labels[2].label
//            binding.bottomSheet.tvDetectedItem0Value.text = labels[0].score.toString()
//            binding.bottomSheet.tvDetectedItem1Value.text = labels[1].score.toString()
//            binding.bottomSheet.tvDetectedItem2Value.text = labels[2].score.toString()
//            binding.bottomSheet.inferenceInfo.text =
//                getString(R.string.inference_time, inferenceTime)
//            binding.bottomSheet.inputFpsInfo.text = String.format("%.1f", inputFps)
        }
    }

    private var cache: MutableMap<Boolean, Boolean> = ConcurrentHashMap()

    private fun doSomethingOnce(labels: List<Category>) {
        captureVideo()
        // Toast.makeText(this, labels[0].label.toString().trim() + labels[1].label.toString().trim()  + labels[2].label.toString().trim()  + labels[3].label.toString().trim(), Toast.LENGTH_SHORT).show()
        cache.computeIfAbsent(true) { x: Boolean? ->
            mp = MediaPlayer.create(this@EvaluationActivity, R.raw.sport_done)
            mp.start()
            if (labels[0].score > 0.5 || labels[1].score > 0.5 || labels[2].score > 0.5) {
                binding.bottomSheet.tvDetectedItem0Value.text = "10/10"
                sharedPreference.edit().putString("score", "10/10").apply()
            } else {
                binding.bottomSheet.tvDetectedItem0Value.text = "8/10"
                sharedPreference.edit().putString("score", "8/10").apply()
            }
            true
        }
    }

    private fun stopCamera(labels: List<Category>) {

    }

    override fun onDestroy() {
        super.onDestroy()
        videoClassifier?.close()
        executor.shutdown()
    }
}
