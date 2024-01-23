package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.Models.User
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.example.instagramclone.utils.USER_NODE
import com.example.instagramclone.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) {
                if (it!=null) {

                }else{
                    user.image=it
                    binding.profileImage2.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val text =
            "<font color=#FF000000>Already Have an Account?</font> <font color=#2196F3>Login</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()
        if (intent.hasExtra("MODE")) {
            if (intent.getIntExtra("MODE", -1) == 1) {

                binding.signUpBtn.text = "Update Profile"
                binding.login.visibility = View.GONE
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {

                        user = it.toObject<User>()!!
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage2)
                        }
                        binding.name.editText?.setText(user.name)
                        binding.email.editText?.setText(user.email)
                        binding.password.editText?.setText(user.password)

                    }
            }
        }
        binding.signUpBtn.setOnClickListener {
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {
                    Firebase.firestore.collection(USER_NODE)
                        .document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        }
                    }
                }
            else {
                    if ((binding.name.editText?.text.toString() == "") or
                        (binding.email.editText?.text.toString() == "") or
                        (binding.password.editText?.text.toString() == "")

                    ) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Please fill the all information",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                            binding.email.editText?.text.toString(),
                            binding.password.editText?.text.toString()
                        ).addOnCompleteListener { result ->

                            if (result.isSuccessful) {
                                user.name = binding.name.editText?.text.toString()
                                user.email = binding.email.editText?.text.toString()
                                user.password = binding.password.editText?.text.toString()
                                Firebase.firestore.collection(USER_NODE)
                                    .document(Firebase.auth.currentUser!!.uid).set(user)
                                    .addOnSuccessListener {
                                        startActivity(
                                            Intent(
                                                this@SignUpActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                        finish()
                                    }

                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    result.exception?.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            binding.addImage.setOnClickListener {
                launcher.launch("image/*")
            }
            binding.login.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }
        }
    }