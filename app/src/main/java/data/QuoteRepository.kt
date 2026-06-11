package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteRepository {
    private val api: QuoteApiService = Retrofit.Builder()
        .baseUrl("https://api.quotable.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuoteApiService::class.java)

    suspend fun getMotivationalQuote(): Result<QuoteResponse> {
        return try {
            val quote = api.getRandomQuote()
            Result.success(quote)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}