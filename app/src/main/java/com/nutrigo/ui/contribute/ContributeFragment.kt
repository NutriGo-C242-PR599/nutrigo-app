package com.nutrigo.ui.contribute


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nutrigo.databinding.FragmentContributeBinding

class ContributeFragment : Fragment() {

    private var _binding: FragmentContributeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContributeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val productCode = arguments?.getString(PRODUCT_CODE)

        binding.etProductCode.setText(productCode)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PRODUCT_CODE = "PRODUCT_CODE"
    }
}
