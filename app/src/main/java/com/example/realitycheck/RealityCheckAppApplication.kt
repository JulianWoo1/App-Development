package com.example.realitycheck

import android.app.Application
import com.example.realitycheck.data.model.ContentItem
import com.example.realitycheck.data.repository.ContentRepository
import com.example.realitycheck.data.repository.SupabaseContentRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

class RealityCheckApplication : Application() {

    val supabaseClient: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
        }
    }

    val contentRepository: ContentRepository by lazy {
        val fetchBatch: suspend () -> List<ContentItem> = {
            supabaseClient
                .postgrest["content_items"]
                .select()
                .decodeList<ContentItem>()
        }
        SupabaseContentRepository(fetchBatch = fetchBatch)
    }
}