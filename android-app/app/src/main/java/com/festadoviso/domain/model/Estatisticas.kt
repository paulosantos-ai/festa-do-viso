package com.festadoviso.domain.model

/**
 * Domain model para Estatísticas gerais do sistema.
 * Usado no painel de administração.
 */
data class Estatisticas(
    val totalFolhas: Int,
    val folhasAtivas: Int,
    val numerosVendidos: Int,
    val numerosDisponiveis: Int,
    val totalVencedores: Int
)
