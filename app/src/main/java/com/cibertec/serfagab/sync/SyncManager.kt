package com.cibertec.serfagab.sync

import android.content.Context
import com.cibertec.serfagab.data.store.MaterialStore
import com.cibertec.serfagab.data.store.ProveedorStore
import com.cibertec.serfagab.data.store.TipoMaterialStore
import com.cibertec.serfagab.remote.ApiClient

object SyncManager {

    fun sincronizarCatalogos(context: Context): Boolean {
        val tipos = ApiClient.listarTiposMaterial()
        val proveedores = ApiClient.listarProveedores()
        val materiales = ApiClient.listarMateriales()

        tipos.datos?.let { TipoMaterialStore(context).reemplazarTodos(it) }
        proveedores.datos?.let { ProveedorStore(context).reemplazarTodos(it) }
        materiales.datos?.let { MaterialStore(context).reemplazarTodos(it) }

        return tipos.exito && proveedores.exito && materiales.exito
    }

    fun sincronizarMateriales(context: Context): Boolean {
        val materiales = ApiClient.listarMateriales()
        materiales.datos?.let { MaterialStore(context).reemplazarTodos(it) }
        return materiales.exito
    }
}