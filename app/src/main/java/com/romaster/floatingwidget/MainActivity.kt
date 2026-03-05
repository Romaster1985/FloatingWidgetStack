package com.romaster.floatingwidget

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    
    private val OVERLAY_PERMISSION_REQUEST = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val btnStart = findViewById<Button>(R.id.btn_start_overlay)
        val btnConfigure = findViewById<Button>(R.id.btn_configure)
        val btnRequestPermission = findViewById<Button>(R.id.btn_request_permission)
        
        btnRequestPermission.setOnClickListener {
            requestOverlayPermission()
        }
        
        btnStart.setOnClickListener {
            if (checkOverlayPermission()) {
                startFloatingService()
            } else {
                Toast.makeText(this, "Primero concede el permiso", Toast.LENGTH_SHORT).show()
            }
        }
        
        btnConfigure.setOnClickListener {
            startActivity(Intent(this, ConfigurationActivity::class.java))
        }
    }
    
    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST)
        }
    }
    
    private fun startFloatingService() {
        val intent = Intent(this, FloatingWidgetService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        finish() // Cerrar la actividad
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST) {
            if (checkOverlayPermission()) {
                Toast.makeText(this, "¡Permiso concedido!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permiso necesario para usar el overlay", Toast.LENGTH_LONG).show()
            }
        }
    }
}