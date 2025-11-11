// Define o pacote para os DAOs (Data Access Objects) locais.
package com.example.listagamificada.data.local.dao

// Importa as anotações e classes do Room, a entidade StatsEntity e o Flow do Coroutines.
import androidx.room.*
import com.example.listagamificada.data.local.entity.StatsEntity
import kotlinx.coroutines.flow.Flow

// Anotação que marca a interface como um DAO do Room.
@Dao
interface StatsDao {
    // Query para buscar as estatísticas de um usuário específico, retornando um Flow para observação contínua.
    @Query("SELECT * FROM stats WHERE userId = :uid LIMIT 1")
    fun getStatsFlow(uid: String): Flow<StatsEntity?>

    // Insere ou atualiza as estatísticas de um usuário. Se já existirem, serão substituídas.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: StatsEntity)

    // Query para deletar as estatísticas de um usuário específico.
    @Query("DELETE FROM stats WHERE userId = :uid")
    suspend fun deleteByUser(uid: String)
}
