package com.cibertec.serfagab.sync

import android.content.Context
import com.cibertec.serfagab.data.store.MaterialStore
import com.cibertec.serfagab.data.store.ProveedorStore
import com.cibertec.serfagab.data.store.TipoMaterialStore
import com.cibertec.serfagab.model.Material
import com.cibertec.serfagab.remote.ApiClient
import com.cibertec.serfagab.remote.Respuesta

object SyncManager {

    fun sincronizarCatalogos(context: Context): Boolean {
        val subidaCorrecta = subirMaterialesPendientes(context)

        val tipos = ApiClient.listarTiposMaterial()
        val proveedores = ApiClient.listarProveedores()
        val materiales = ApiClient.listarTodosLosMateriales()

        tipos.datos?.let { TipoMaterialStore(context).reemplazarTodos(it) }
        proveedores.datos?.let { ProveedorStore(context).reemplazarTodos(it) }
        materiales.datos?.let { MaterialStore(context).reemplazarTodos(it) }

        return subidaCorrecta && tipos.exito && proveedores.exito && materiales.exito
    }

    fun sincronizarMateriales(context: Context): Boolean {
        val subidaCorrecta = subirMaterialesPendientes(context)

        val materiales = ApiClient.listarMateriales()
        materiales.datos?.let { MaterialStore(context).reemplazarTodos(it) }

        return subidaCorrecta && materiales.exito
    }

    private fun subirMaterialesPendientes(context: Context): Boolean {
        val store = MaterialStore(context)
        var todoCorrecto = true

        store.obtenerPendientes().forEach { material ->
            val respuesta = if (material.idMaterial < 0) {
                ApiClient.crearMaterial(material)
            } else {
                subirCambiosMaterial(material)
            }

            val materialServidor = respuesta.datos
            if (respuesta.exito && materialServidor != null) {
                store.confirmarSincronizado(material.idMaterial, materialServidor)
            } else {
                todoCorrecto = false
            }
        }
        return todoCorrecto
    }

    private fun subirCambiosMaterial(material: Material): Respuesta<Material> {
        val actualizado = ApiClient.actualizarMaterial(material.idMaterial, material)
        val materialServidor = actualizado.datos
        if (!actualizado.exito || materialServidor == null) return actualizado
        if (materialServidor.activo == material.activo) return actualizado

        return if (material.activo) {
            ApiClient.activarMaterial(material.idMaterial)
        } else {
            ApiClient.desactivarMaterial(material.idMaterial)
        }
    }
}
