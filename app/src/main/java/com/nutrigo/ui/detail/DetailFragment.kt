package com.nutrigo.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.nutrigo.R
import com.nutrigo.data.remote.response.ProductResponse
import com.nutrigo.data.remote.retrofit.ApiConfig
import com.nutrigo.databinding.FragmentDetailBinding
import com.nutrigo.ui.contribute.ContributeFragment
import com.nutrigo.ui.home.HomeFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data dari arguments
        val productCode = arguments?.getString(PRODUCT_CODE_KEY)

        if (productCode != null) {

            binding.produkCode.text = "Kode Produk: $productCode"
            findProductDetail(productCode)

        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Kesalahan")
                .setMessage("Data yang dikirim Null. Apakah Anda ingin mencoba lagi?")
                .setNegativeButton("Coba Lagi") { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun findProductDetail(productCode: String) {
        showProgressBar(true)
        val client = ApiConfig.getApiService().getProduct(productCode)
        client.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    showProgressBar(false)
                    val responseBody = response.body()
                    val nutriments = responseBody?.product?.nutriments

                    if (nutriments != null) {
                        val energyValue = nutriments.energy ?: 0.0
                        val fatValue = nutriments.fat ?: 0.0
                        val saturatedFatValue = nutriments.saturatedFat ?: 0.0
                        val carbohydratesValue = nutriments.carbohydrates ?: 0.0
                        val sugarsValue = nutriments.sugars ?: 0.0
                        val saltsValue = nutriments.salt ?: 0.0
                        val proteinsValue = nutriments.proteins ?: 0.0

                        binding.energyValue.text = String.format("%.2f %s", energyValue, "kcal")
                        binding.fatValue.text = String.format("%.2f %s", fatValue, "g")
                        binding.saturatedFatValue.text = String.format("%.2f %s", saturatedFatValue, "g")
                        binding.carbohydratesValue.text = String.format("%.2f %s", carbohydratesValue, "g")
                        binding.sugarValue.text = String.format("%.2f %s", sugarsValue, "g")
                        binding.saltValue.text = String.format("%.2f %s", saltsValue, "g")
                        binding.proteinValue.text = String.format("%.2f %s", proteinsValue, "g")
                    } else {
                        Toast.makeText(requireContext(), "Data nutriments tidak tersedia", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    showProgressBar(false)
                    AlertDialog.Builder(requireContext())
                        .setTitle("Gagal memuat data produk")
                        .setMessage("Kode produk $productCode tidak ditemukan. Apakah Anda ingin berkontribusi untuk menambahkan produk ini?")
                        .setPositiveButton("Ikut Kontribusi") { _, _ ->
                            navigateToContributeFragment(productCode)
                        }
                        .setNegativeButton("Tidak") { _, _ ->
                            parentFragmentManager.popBackStack()
                        }
                        .setCancelable(false)
                        .show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showProgressBar(false)
                AlertDialog.Builder(requireContext())
                    .setTitle("Kesalahan")
                    .setMessage("Gagal terhubung ke server: ${t.message}. Apakah Anda ingin mencoba lagi?")
                    .setPositiveButton("Coba Lagi") { _, _ ->
                        findProductDetail(productCode)
                    }
                    .setNegativeButton("Batal") { _, _ ->
                        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        findNavController().navigate(R.id.navigation_home)
                    }
                    .setCancelable(false)
                    .show()
            }

        })
    }

    private fun showProgressBar(status: Boolean) {
        if (status) {
            binding.progressBar.visibility = View.VISIBLE
            binding.root.children.forEach { view ->
                if (view.id != binding.progressBar.id) {
                    view.visibility = View.GONE
                }
            }
        } else {
            binding.progressBar.visibility = View.GONE
            binding.root.children.forEach { view ->
                if (view.id != binding.progressBar.id) {
                    view.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToContributeFragment(productCode: String?) {
        if (!productCode.isNullOrEmpty() && productCode.length > 11) {
            if (findNavController().currentDestination?.id == R.id.navigation_detail) {
                val action = DetailFragmentDirections.actionDetailToContribute(productCode)
                findNavController().navigate(action)
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

    companion object {
        const val PRODUCT_CODE_KEY = "productCode"
    }
}
