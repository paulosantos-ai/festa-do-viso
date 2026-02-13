package com.festadoviso

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gestor de dados usando SharedPreferences + Gson
 */
class DataManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("festa_viso_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Models
    data class Folha(
        val id: Long,
        val nome: String,
        val ativa: Boolean = true,
        val dataCriacao: Long = System.currentTimeMillis()
    )

    data class Registo(
        val id: Long,
        val folhaId: Long,
        val numero: Int,
        val nome: String,
        val contacto: String,
        val dataRegisto: Long = System.currentTimeMillis()
    )

    data class Vencedor(
        val id: Long,
        val folhaId: Long,
        val folhaNome: String,
        val dataSorteio: Long,
        val numeroVencedor: Int,
        val vencedorNome: String,
        val vencedorContacto: String
    )

    // --- FOLHAS ---
    fun getFolhas(): List<Folha> {
        val json = prefs.getString("folhas", "[]") ?: "[]"
        val type = object : TypeToken<List<Folha>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getFolhasAtivas(): List<Folha> = getFolhas().filter { it.ativa }

    fun getFolhaById(id: Long): Folha? = getFolhas().firstOrNull { it.id == id }

    fun adicionarFolha(nome: String): Folha {
        val folhas = getFolhas().toMutableList()
        val novaFolha = Folha(
            id = System.currentTimeMillis(),
            nome = nome,
            ativa = true
        )
        folhas.add(novaFolha)
        salvarFolhas(folhas)
        return novaFolha
    }

    fun toggleFolhaAtiva(folhaId: Long) {
        val folhas = getFolhas().toMutableList()
        val index = folhas.indexOfFirst { it.id == folhaId }
        if (index != -1) {
            folhas[index] = folhas[index].copy(ativa = !folhas[index].ativa)
            salvarFolhas(folhas)
        }
    }

    fun eliminarFolha(folhaId: Long) {
        val folhas = getFolhas().filter { it.id != folhaId }
        salvarFolhas(folhas)

        // Eliminar registos da folha
        val registos = getRegistos().filter { it.folhaId != folhaId }
        salvarRegistos(registos)
    }

    private fun salvarFolhas(folhas: List<Folha>) {
        prefs.edit().putString("folhas", gson.toJson(folhas)).apply()
    }

    // --- REGISTOS ---
    fun getRegistos(): List<Registo> {
        val json = prefs.getString("registos", "[]") ?: "[]"
        val type = object : TypeToken<List<Registo>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getRegistosByFolha(folhaId: Long): List<Registo> =
        getRegistos().filter { it.folhaId == folhaId }

    fun getNumerosOcupados(folhaId: Long): Set<Int> =
        getRegistosByFolha(folhaId).map { it.numero }.toSet()

    fun isNumeroDisponivel(folhaId: Long, numero: Int): Boolean =
        !getNumerosOcupados(folhaId).contains(numero)

    fun registarNumero(folhaId: Long, numero: Int, nome: String, contacto: String): Boolean {
        if (!isNumeroDisponivel(folhaId, numero)) return false

        val registos = getRegistos().toMutableList()
        registos.add(
            Registo(
                id = System.currentTimeMillis(),
                folhaId = folhaId,
                numero = numero,
                nome = nome,
                contacto = contacto
            )
        )
        salvarRegistos(registos)
        return true
    }

    private fun salvarRegistos(registos: List<Registo>) {
        prefs.edit().putString("registos", gson.toJson(registos)).apply()
    }

    // --- VENCEDORES ---
    fun getVencedores(): List<Vencedor> {
        val json = prefs.getString("vencedores", "[]") ?: "[]"
        val type = object : TypeToken<List<Vencedor>>() {}.type
        return gson.fromJson(json, type)
    }

    fun registarVencedor(
        folhaId: Long,
        folhaNome: String,
        numeroVencedor: Int,
        dataSorteio: Long = System.currentTimeMillis()
    ): Boolean {
        val registo = getRegistosByFolha(folhaId).firstOrNull { it.numero == numeroVencedor }
            ?: return false

        val vencedores = getVencedores().toMutableList()
        vencedores.add(
            Vencedor(
                id = System.currentTimeMillis(),
                folhaId = folhaId,
                folhaNome = folhaNome,
                dataSorteio = dataSorteio,
                numeroVencedor = numeroVencedor,
                vencedorNome = registo.nome,
                vencedorContacto = registo.contacto
            )
        )
        salvarVencedores(vencedores)
        return true
    }

    private fun salvarVencedores(vencedores: List<Vencedor>) {
        prefs.edit().putString("vencedores", gson.toJson(vencedores)).apply()
    }

    // --- ADMIN ---
    fun verificarLogin(username: String, password: String): Boolean {
        return username == "admin" && password == "admin123"
    }

    // --- INICIALIZAÇÃO ---
    fun inicializarDados() {
        if (getFolhas().isEmpty()) {
            adicionarFolha("Semana 1")
        }
    }
}
