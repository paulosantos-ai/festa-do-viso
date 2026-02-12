package com.festadoviso.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.festadoviso.data.local.dao.AdminDao
import com.festadoviso.data.local.dao.FolhaDao
import com.festadoviso.data.local.dao.RegistoDao
import com.festadoviso.data.local.dao.VencedorDao
import com.festadoviso.data.local.entity.AdminEntity
import com.festadoviso.data.local.entity.FolhaEntity
import com.festadoviso.data.local.entity.RegistoEntity
import com.festadoviso.data.local.entity.VencedorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Base de dados Room principal da aplicação Festa do Viso.
 *
 * Contém todas as entidades e DAOs necessários para o funcionamento offline.
 * A primeira vez que a app é executada, são criados dados iniciais (seed data).
 */
@Database(
    entities = [
        FolhaEntity::class,
        RegistoEntity::class,
        VencedorEntity::class,
        AdminEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun folhaDao(): FolhaDao
    abstract fun registoDao(): RegistoDao
    abstract fun vencedorDao(): VencedorDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DATABASE_NAME = "festa_viso.db"

        /**
         * Obtém a instância singleton da base de dados.
         * Se não existir, cria uma nova com seed data.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Callback executado quando a base de dados é criada pela primeira vez.
         * Insere dados iniciais (admin padrão e primeira folha).
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }
        }

        /**
         * Popula a base de dados com dados iniciais:
         * - Admin padrão: username "admin", password "admin123"
         * - Folha padrão: "Semana 1"
         */
        private suspend fun populateDatabase(database: AppDatabase) {
            val adminDao = database.adminDao()
            val folhaDao = database.folhaDao()

            // Verificar se já existem dados (prevenir duplicação)
            if (adminDao.count() == 0) {
                // Criar admin padrão com password "admin123"
                val passwordHash = BCrypt.hashpw("admin123", BCrypt.gensalt())
                adminDao.insert(
                    AdminEntity(
                        username = "admin",
                        passwordHash = passwordHash
                    )
                )
            }

            if (folhaDao.count() == 0) {
                // Criar folha padrão
                folhaDao.insert(
                    FolhaEntity(
                        nome = "Semana 1",
                        ativa = true
                    )
                )
            }
        }
    }
}
