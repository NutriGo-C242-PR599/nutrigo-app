package com.nutrigo.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.nutrigo.R
import com.nutrigo.data.remote.response.ProductResponse
import com.nutrigo.data.remote.retrofit.ApiConfig
import com.nutrigo.databinding.ActivityDetailBinding
import com.nutrigo.ui.contribute.ContributeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Ambil data dari intent
        val productCode = intent.getStringExtra(PRODUCT_CODE)

        // Gunakan data ini untuk menampilkan detail
        if (productCode != null) {
            binding.produkCode.text = "Kode Produk: $productCode"
            // Lakukan logika tambahan (misalnya mencari detail produk dari database atau API)
            findProductDetail(productCode)
        } else {
            Toast.makeText(this, "Kode produk tidak ditemukan!", Toast.LENGTH_SHORT).show()
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

                    // Memeriksa apakah nutriments tidak null
                    if (nutriments != null) {
                        // Menggunakan nilai default untuk menghindari NullPointerException
                        val energyValue = nutriments.energy ?: 0.0
                        val fatValue = nutriments.fat ?: 0.0
                        val saturatedFatValue = nutriments.saturatedFat ?: 0.0
                        val carbohydratesValue = nutriments.carbohydrates ?: 0.0
                        val sugarsValue = nutriments.sugars ?: 0.0
                        val saltsValue = nutriments.salt ?: 0.0
                        val proteinsValue = nutriments.proteins ?: 0.0

                        // Memperbarui tampilan binding dengan format string
                        binding.energyValue.text = String.format("%.2f %s", energyValue, "kcal")
                        binding.fatValue.text = String.format("%.2f %s", fatValue, "g")
                        binding.saturatedFatValue.text = String.format("%.2f %s", saturatedFatValue, "g")
                        binding.carbohydratesValue.text = String.format("%.2f %s", carbohydratesValue, "g")
                        binding.sugarValue.text = String.format("%.2f %s", sugarsValue, "g")
                        binding.saltValue.text = String.format("%.2f %s", saltsValue, "g")
                        binding.proteinValue.text = String.format("%.2f %s", proteinsValue, "g")
                    } else {
                        // Menampilkan pesan jika nutriments tidak tersedia
                        Toast.makeText(
                            binding.root.context,
                            "Data nutriments tidak tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    showProgressBar(false)
                    val alertDialog = AlertDialog.Builder(this@DetailActivity)
                        .setTitle("Gagal memuat data produk")
                        .setMessage("Kode produk $productCode tidak ditemukan. Apakah Anda ingin berkontribusi untuk menambahkan produk ini?")
                        .setPositiveButton("Ikut Kontribusi") { _, _ ->

                            // Jika pengguna memilih untuk berkontribusi, arahkan ke halaman berkontribusi
                            val intent = Intent(this@DetailActivity, MainActivity::class.java)
                            intent.putExtra(ContributeFragment.PRODUCT_CODE, productCode)
                            startActivity(intent)
                            finish()
                        }
                        .setNegativeButton("Tidak") { _, _ ->
                            Toast.makeText(this@DetailActivity, "Tidak Ikut, Lagi Sibuk", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@DetailActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .setCancelable(false)
                        .create()
                    alertDialog.show()

                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                showProgressBar(false)

                // Menangani kegagalan permintaan
                Toast.makeText(binding.root.context, "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_SHORT).show()

                // Kembali ke MainActivity jika gagal terhubung
                val intent = Intent(this@DetailActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional jika ingin menutup DetailActivity setelah kembali ke HomeFragment
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



    companion object {
        const val PRODUCT_CODE = "PRODUCT_CODE"
    }

}