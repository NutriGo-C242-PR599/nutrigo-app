package com.nutrigo.ui.contribute

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.nutrigo.databinding.FragmentContributeBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ContributeFragment : Fragment() {

    private var _binding: FragmentContributeBinding? = null
    private val binding get() = _binding!!

    private var photoUri: Uri? = null

    // Launchers for gallery and camera
    private val pickFromGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.ivNutritionTable.setImageURI(uri)
            } else {
                Toast.makeText(requireContext(), "Gambar tidak dipilih", Toast.LENGTH_SHORT).show()
            }
        }

    private val captureFromCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (photoUri != null) {
                    binding.ivNutritionTable.setImageURI(photoUri)
                } else {
                    Toast.makeText(requireContext(), "Gambar gagal disimpan", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Operasi dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContributeBinding.inflate(inflater, container, false)

        // Ambil data dari arguments
        val productCode = arguments?.getString(PRODUCT_CODE_KEY)
        binding.etProductCode.setText(productCode)

        // Handle button click for choosing photo
        binding.ibNutritionTable.setOnClickListener {
            showPhotoOptions()
        }

        return binding.root
    }

    private fun showPhotoOptions() {
        val options = arrayOf("Ambil dari Galeri", "Ambil dari Kamera")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        pickFromGalleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        captureFromCameraLauncher.launch(takePictureIntent)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_CODE_KEY = "productCode"
    }
}
