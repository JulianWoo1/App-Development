package com.example.realitycheck.data.di

import com.example.realitycheck.BuildConfig
import com.example.realitycheck.data.model.ContentItem
import com.example.realitycheck.data.repository.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

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

    val contentRepository: ContentRepository by lazy {
        SupabaseContentRepository(
            fetchBatch = {
                client.postgrest["content_items"]
                    .select()
                    .decodeList<ContentItem>()
            }
        )
    }
}
