// Define o pacote para os DAOs (Data Access Objects) locais.
package com.example.listagamificada.data.local.dao

// Importa as anotações e classes do Room, além da entidade TaskEntity e do Flow do Coroutines.
import androidx.room.*
import com.example.listagamificada.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

// Anotação que marca a interface como um DAO do Room.
@Dao
interface TaskDao {
    // Query para selecionar todas as tarefas de um usuário específico, retornando um Flow para observação contínua.
    @Query("SELECT * FROM tasks WHERE ownerId = :uid")
    fun getTasksFlow(uid: String): Flow<List<TaskEntity>>

    // Insere uma nova tarefa no banco de dados. Se houver conflito (mesmo ID), a tarefa existente será substituída.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    // Atualiza uma tarefa existente no banco de dados.
    @Update
    suspend fun update(task: TaskEntity)

    // Deleta uma tarefa do banco de dados.
    @Delete
    suspend fun delete(task: TaskEntity)

    // Query para buscar uma tarefa específica pelo seu ID.
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): TaskEntity?
}
