package com.example.instagramclone.post

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.HomeActivity
import com.example.instagramclone.Models.Reel
import com.example.instagramclone.Models.User
import com.example.instagramclone.R
import com.example.instagramclone.databinding.ActivityReelsBinding
import com.example.instagramclone.utils.REEL
import com.example.instagramclone.utils.REEL_FOLDER
import com.example.instagramclone.utils.USER_NODE
import com.example.instagramclone.utils.uploadVideo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class ReelsActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityReelsBinding.inflate(layoutInflater)
    }

    private lateinit var videoUrl: String
    lateinit var progressDialog: ProgressDialog
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadVideo(uri, REEL_FOLDER, progressDialog) { url ->
                if (url != null) {

                    videoUrl = url

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        progressDialog = ProgressDialog(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.selectReel.setOnClickListener {
            launcher.launch("video/*")
        }
        binding.shareButton.setOnClickListener {
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                .addOnSuccessListener {
                    var user: User = it.toObject<User>()!!
                    val reel: Reel =
                        Reel(videoUrl!!, binding.caption.editText?.text.toString(), user.image!!)

                    Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + REEL)
                            .document()
                            .set(reel)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@ReelsActivity,
                                    "Reel uploaded successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                startActivity(Intent(this@ReelsActivity, HomeActivity::class.java))
                                finish()

                            }
                    }

                }
        }
    }
}
