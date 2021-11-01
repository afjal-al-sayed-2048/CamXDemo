package com.dotescapesoftwarelab.camxdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dotescapesoftwarelab.camxdemo.databinding.DemoDrawModelBinding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: DemoDrawModelBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DemoDrawModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}