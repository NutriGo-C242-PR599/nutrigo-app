package com.nutrigo.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.nutrigo.databinding.FragmentHomeBinding
import com.nutrigo.ui.DetailActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var barcodeScanner: BarcodeScanner

    private var firstCall = true

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
            startCamera()
        }

        with(binding) {
            // Mengatur SearchView dengan SearchBar
            searchView.setupWithSearchBar(searchBar)

            // Mengatur keyboard menjadi angka saja di dalam SearchView
            searchView.editText.inputType = InputType.TYPE_CLASS_NUMBER

            // Listener untuk menangani aksi pencarian
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val inputText = searchView.text.toString()

                // Validasi input (jika hanya ingin angka)
                if (inputText.isEmpty()) {
                    Toast.makeText(requireContext(), "Input tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                } else {
                    // Memasukkan teks pencarian ke SearchBar dan menyembunyikan SearchView
                    searchBar.setText(inputText)
                    searchView.hide()

                    // Mengirim data ke DetailActivity
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra("PRODUCT_CODE", inputText)
                    }
                    startActivity(intent)

                    // Menunda pengosongan teks untuk memastikan perubahan UI selesai
                    searchView.postDelayed({
                        searchView.editText.setText("") // Kosongkan teks
                        searchBar.setText("") // Kosongkan teks pada SearchBar (opsional)
                    }, 200)
                }
                false
            }
        }


        startCamera()
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
        ) { result : MlKitAnalyzer.Result? ->

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
                if (barcode.valueType == Barcode.TYPE_PRODUCT) { // Hanya tipe produk (UPC)
                    firstCall = false
                    val productCode = barcode.rawValue // Ambil nilai kode produk

                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setMessage("Kode Produk: $productCode")
                        .setPositiveButton("Detail Nutrisi") { _, _ ->
                            firstCall = true
                            // Kirim data ke DetailActivity
                            val intent = Intent(requireContext(), DetailActivity::class.java)
                            intent.putExtra("PRODUCT_CODE", productCode) // Kirim kode produk
                            startActivity(intent)
                        }
                        .setNegativeButton("Scan Lagi") { _, _ ->
                            firstCall = true
                        }
                        .setCancelable(false)
                        .create()
                    alertDialog.show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "CameraFragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}