package com.example.lostandfound.ui.reporter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.lostandfound.controller.AuthController
import com.example.lostandfound.controller.CaseController
import com.example.lostandfound.data.repositories.CaseRepository
import com.example.lostandfound.data.repositories.UserRepository
import com.example.lostandfound.databinding.ActivityCreateCaseBinding
import com.example.lostandfound.models.Case
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch


class CreateCaseActivity : AppCompatActivity() {
    private val TAG:String = "CreateCaseActivity"
    private lateinit var binding: ActivityCreateCaseBinding
    private  lateinit var firebaseAuth : FirebaseAuth
    private lateinit var authController: AuthController
    private lateinit var userRepository: UserRepository
    private lateinit var caseRepository: CaseRepository

    val CAMERA_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateCaseBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.firebaseAuth = FirebaseAuth.getInstance()
        this.userRepository = UserRepository(applicationContext)
        this.caseRepository = CaseRepository(applicationContext)
        authController = AuthController(this, this.userRepository)

        val categoryList:List<String> = listOf("Bag","Gadget","Clothes")

        val categoriesAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, categoryList
        )

        this.binding.sType.adapter = categoriesAdapter

        binding.btnCapture.setOnClickListener {
            val intent = Intent(this@CreateCaseActivity, CameraActivity::class.java)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }

        binding.btnCreateCase.setOnClickListener {
            CaseController().createCase(binding, categoryList, caseRepository)
            finish()
        }
    }

    // Override onActivityResult to handle the result from the launched activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        CaseController().loadImage(requestCode, resultCode, data, binding)

    }

}