package com.example.realitycheck.data.di

import com.example.realitycheck.BuildConfig
import com.example.realitycheck.data.model.ContentItem
import com.example.realitycheck.data.repository.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

private const val STORAGE_URL =
    "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
private const val MAX_IMAGE_ID = 400
private const val BATCH_SIZE = 10

object SupabaseModule {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth) {
            scheme = "com.example.realitycheck"
            host = "reset-password"
        }
    }

    val authRepository: AuthRepository by lazy {
        SupabaseAuthRepository(client)
    }

    val profileRepository: ProfileRepository by lazy {
        SupabaseProfileRepository(client, authRepository)
    }

    private val allItems by lazy {
        (1..MAX_IMAGE_ID).flatMap { id ->
            listOf(
                ContentItem(
                    id = "Real-$id",
                    type = "image",
                    contentUrl = "$STORAGE_URL/Real/$id.jpg",
                    isAi = false
                ),
                ContentItem(
                    id = "AI-$id",
                    type = "image",
                    contentUrl = "$STORAGE_URL/AI/$id.jpg",
                    isAi = true
                )
            )
        }
    }

    val contentRepository: ContentRepository by lazy {
        SupabaseContentRepository(
            fetchBatch = {
                allItems.shuffled().take(BATCH_SIZE)
            }
        )
    }
}
