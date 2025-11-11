// Define o pacote para a configuração do banco de dados local.
package com.example.listagamificada.data.local.db

// Importa as classes necessárias do Room e as entidades e DAOs da aplicação.
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.listagamificada.data.local.dao.RankingDao
import com.example.listagamificada.data.local.dao.StatsDao
import com.example.listagamificada.data.local.dao.TaskDao
import com.example.listagamificada.data.local.entity.StatsEntity
import com.example.listagamificada.data.local.entity.TaskEntity
import com.example.listagamificada.model.entity.RankingEntity

/**
 * O banco de dados principal da aplicação.
 * Inclui tabelas para tarefas, estatísticas do usuário e o ranking.
 */
// Anotação que define as entidades do banco de dados, a versão e desativa a exportação do schema.
@Database(entities = [TaskEntity::class, StatsEntity::class, RankingEntity::class], version = 9, exportSchema = false)
// Classe abstrata que representa o banco de dados da aplicação.
abstract class AppDatabase : RoomDatabase() {
    // Função abstrata que retorna o DAO para operações com tarefas.
    abstract fun taskDao(): TaskDao
    // Função abstrata que retorna o DAO para operações com estatísticas.
    abstract fun statsDao(): StatsDao
    // Função abstrata que retorna o DAO para operações com o ranking.
    abstract fun rankingDao(): RankingDao
}
