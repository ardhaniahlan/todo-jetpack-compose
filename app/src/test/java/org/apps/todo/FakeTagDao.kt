package org.apps.todo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.apps.todo.data.local.TagDao
import org.apps.todo.data.local.TagEntity

class FakeTagDao : TagDao {
    private val tags = mutableListOf<TagEntity>()
    private val flow = MutableStateFlow<List<TagEntity>>(emptyList())

    override suspend fun insertTag(tag: TagEntity): Long {
        tags.add(tag.copy(id = tags.size + 1))
        flow.value = tags
        return tags.size.toLong()
    }

    override fun getAllTags(): Flow<List<TagEntity>> = flow
    override suspend fun updateTag(tag: TagEntity) {}
    override suspend fun deleteTag(tag: TagEntity) {}
    override fun getTagById(tagId: Int): Flow<TagEntity> = flow.map { list -> list.first { it.id == tagId } }
}