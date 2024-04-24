package com.example.shoppingcart.fragments.loreg

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shoppingcart.R
import com.example.shoppingcart.activities.ShoppingActivity
import com.example.shoppingcart.databinding.FragmentLoginBinding
import com.example.shoppingcart.dialog.setupBottomSheetDialog
import com.example.shoppingcart.util.Resource
import com.example.shoppingcart.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragments : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDonthaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragments_to_registerFragment)
        }

        binding.apply {
            buttonLoginnAccountOptions.setOnClickListener {
                val email = edemaillogin.text.toString().trim()
                val password = edpasswordlogin.text.toString()
                viewModel.login(email, password)
            }
        }
        binding.forgetpasswrd.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {
                    }

                    is Resource.success -> {
                        Snackbar.make(
                            requireView(),
                            "Reset link was sent to your email",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error:${it.message}", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    else -> Unit

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginnAccountOptions.startAnimation()
                    }

                    is Resource.success -> {
                        binding.buttonLoginnAccountOptions.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.buttonLoginnAccountOptions.revertAnimation()
                    }

                    else -> Unit

                }
            }
        }
    }
}
