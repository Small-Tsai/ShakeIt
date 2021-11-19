package com.tsai.shakeit.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.tsai.shakeit.R
import com.tsai.shakeit.databinding.LoginFragmentBinding
import com.tsai.shakeit.ext.getVmFactory
import com.tsai.shakeit.service.MyFirebaseService
import com.tsai.shakeit.ui.orderdetail.TOPIC
import com.tsai.shakeit.util.Logger
import com.tsai.shakeit.util.UserInfo
import kotlinx.coroutines.launch

private const val RC_SIGN_IN = 9001

class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel> {
        getVmFactory()
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var orderId: String

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUser(currentUser);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyFirebaseService.sharedPref =
            requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Logger.w("Fetching FCM registration token failed ${task.exception}")
                return@OnCompleteListener
            }
            MyFirebaseService.token = task.result
        })

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        if (requireActivity().intent.action == "android.intent.action.VIEW") {
            requireActivity().intent.data?.pathSegments?.get(0)?.let {
                Logger.d(it)
                orderId = it
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC + it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize Firebase Auth
        auth = Firebase.auth
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.signIn.setOnClickListener {
            signIn()
        }

        viewModel.navToOrder.observe(viewLifecycleOwner, {
            it?.let { findNavController().navigate(LoginFragmentDirections.navToOrder()) }
        })

        return binding.root
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {

                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)

            } catch (e: ApiException) {
                Logger.w("Google sign in failed $e")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->

                if (task.isSuccessful) {

                    // Sign in success, update UserInfo with the signed-in user's information
                    Logger.d("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUser(user)
                } else {

                    // If sign in fails, display a message to the user.
                    Logger.d("signInWithCredential:failure ${task.exception}")
                    updateUser(null)
                }
            }
    }

    private fun updateUser(user: FirebaseUser?) {
        user?.let {

            UserInfo.userId = user.uid
            UserInfo.userName = user.displayName.toString()
            UserInfo.userImage = user.photoUrl.toString()

            lifecycleScope.launch {

                viewModel.uploadUser()

                if (::orderId.isInitialized) {
                    viewModel.joinToOrder(orderId)
                } else {
                    findNavController().navigate(LoginFragmentDirections.navToHome())
                }

            }
        }
    }
}