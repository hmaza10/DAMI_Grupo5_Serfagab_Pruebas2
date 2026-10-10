package com.cibertec.serfagab

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.cibertec.serfagab.databinding.ActivityLoginBinding
import android.content.Intent
import android.widget.Toast
import com.cibertec.serfagab.data.DatosPrueba
import com.cibertec.serfagab.data.store.UsuarioStore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioStore: UsuarioStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DatosPrueba.cargarSiVacio(this)
        usuarioStore = UsuarioStore(this)

        binding.buttonIngresar.setOnClickListener {
            inciarSesion()
        }

    }

    private fun inciarSesion() {
        val login = binding.editTextUsuario.text.toString().trim()
        val clave = binding.editTextPassword.text.toString().trim()
        var formulario = true

        binding.inputLayoutUsuario.error = null
        binding.inputLayoutPassword.error = null

        if (login.isBlank()) {
            binding.inputLayoutUsuario.error = getString(R.string.error_usuario_vacio)
            formulario = false
        }
        if (clave.isBlank()) {
            binding.inputLayoutPassword.error = getString(R.string.error_password_vacio)
            formulario = false
        }
        if(!formulario) {
            return
        }

        Thread {
            val usuario = usuarioStore.validarLogin(login, clave)

            runOnUiThread {
                binding.buttonIngresar.isEnabled = true

                if (usuario == null) {
                    Toast.makeText(this, R.string.error_credenciales, Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra(MainActivity.EXTRA_ID_USUARIO, usuario.idUsuario)
                        putExtra(MainActivity.EXTRA_ID_TIPO, usuario.tipo.idTipo)
                        putExtra(MainActivity.EXTRA_NOMBRES, usuario.nombres)
                        putExtra(MainActivity.EXTRA_APELLIDOS, usuario.apellidos)
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }
}