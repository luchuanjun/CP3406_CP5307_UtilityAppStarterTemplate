package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import retrofit2.http.GET

interface QuoteApiService {
    @GET("random")
    suspend fun getRandomQuote(): QuoteResponse
}