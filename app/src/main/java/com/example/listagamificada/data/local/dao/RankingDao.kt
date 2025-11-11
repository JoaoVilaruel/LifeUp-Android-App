// Define o pacote para os DAOs (Data Access Objects) locais.
package com.example.listagamificada.data.local.dao

// Importa as anotações e classes do Room, a entidade RankingEntity e o Flow do Coroutines.
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.listagamificada.model.entity.RankingEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para a tabela de ranking.
 */
@Dao
interface RankingDao {
    /**
     * Busca o ranking completo, ordenado por pontos em ordem decrescente.
     * @return Um Flow com a lista de entradas do ranking.
     */
    @Query("SELECT * FROM ranking ORDER BY points DESC")
    fun getRankingFlow(): Flow<List<RankingEntity>>

    /**
     * Insere ou atualiza uma lista de entradas no ranking.
     * Útil para sincronizar dados de uma fonte remota como o Firebase.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(ranking: List<RankingEntity>)

    /**
     * Deleta todas as entradas da tabela de ranking.
     */
    @Query("DELETE FROM ranking")
    suspend fun clearAll()
}
