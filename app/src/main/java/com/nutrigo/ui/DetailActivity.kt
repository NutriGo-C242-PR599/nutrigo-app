package com.nutrigo.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nutrigo.R
import com.nutrigo.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Ambil data dari intent
        val productCode = intent.getStringExtra("PRODUCT_CODE")

        // Gunakan data ini untuk menampilkan detail
        if (productCode != null) {
            binding.produkName.text = "Kode Produk: $productCode"
            // Lakukan logika tambahan (misalnya mencari detail produk dari database atau API)
        } else {
            Toast.makeText(this, "Kode produk tidak ditemukan!", Toast.LENGTH_SHORT).show()
        }
    }

}