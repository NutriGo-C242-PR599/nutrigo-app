package com.nutrigo.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.nutrigo.R
import com.nutrigo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var barcodeScanner: BarcodeScanner
    private var firstCall = true

    // Variabel untuk mendeteksi dua kali klik back
    private var lastBackPressedTime: Long = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Izin kamera diperlukan untuk menggunakan fitur ini",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchCamera.setOnClickListener {
            cameraSelector =
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            checkCameraPermissionAndStart()
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.inputType = InputType.TYPE_CLASS_NUMBER

            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val inputText = searchView.editText.text.toString()

                if (inputText.isEmpty()) {
                    Toast.makeText(requireContext(), "Input tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                } else {
                    searchBar.setText(inputText)
                    searchView.hide()

                    navigateToDetailFragment(inputText)

                    searchView.postDelayed({
                        searchView.editText.setText("")
                        searchBar.setText("")
                    }, 200)
                }
                false
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val currentTime = System.currentTimeMillis()
            val searchView = binding.searchView
            if (searchView.isShowing){
                binding.searchView.hide()
            } else {
                if (currentTime - lastBackPressedTime < 2000) {
                    showExitConfirmationDialog()
                } else {
                    lastBackPressedTime = currentTime
                    Toast.makeText(requireContext(), "Tekan kembali lagi untuk keluar.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        checkCameraPermissionAndStart()
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun checkCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Izin Kamera Diperlukan")
                    .setMessage("Fitur ini memerlukan izin kamera untuk dapat digunakan. Berikan izin untuk melanjutkan.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        val analyzer = MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(requireContext())
        ) { result: MlKitAnalyzer.Result? ->
            showResult(result)
        }

        val cameraController = LifecycleCameraController(requireContext())
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            analyzer
        )
        cameraController.bindToLifecycle(viewLifecycleOwner)
        binding.viewFinder.controller = cameraController
    }

    private fun showResult(result: MlKitAnalyzer.Result?) {
        if (firstCall) {
            val barcodeResults = result?.getValue(barcodeScanner)

            if (!barcodeResults.isNullOrEmpty() && barcodeResults[0] != null) {
                val barcode = barcodeResults[0]
                if (barcode.valueType == Barcode.TYPE_PRODUCT) {
                    firstCall = false
                    val productCode = barcode.rawValue
                    if (productCode != null) {
                        AlertDialog.Builder(requireContext())
                            .setMessage("Kode Produk: $productCode")
                            .setPositiveButton("Detail Nutrisi") { _, _ ->
                                navigateToDetailFragment(productCode)
                            }
                            .setNegativeButton("Scan Lagi") { _, _ ->
                                firstCall = true
                            }
                            .setCancelable(false)
                            .show()
                    } else {
                        Toast.makeText(requireContext(), "Tidak ada kode produk yang terbaca", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun navigateToDetailFragment(productCode: String?) {
        if (!productCode.isNullOrEmpty() && productCode.length > 11) {
            if (findNavController().currentDestination?.id == R.id.navigation_home) {
                val action = HomeFragmentDirections.actionHomeToDetail(productCode)
                findNavController().navigate(action)
                firstCall = true
            } else {
                Toast.makeText(requireContext(), "Navigasi tidak valid dari halaman ini!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Kode produk tidak valid atau terlalu pendek!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
