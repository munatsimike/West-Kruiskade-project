package nl.project.westkruiskade.data.api
import nl.project_.west_kruiskade.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class RemoteDataSource @Inject constructor() {
    // build service to fetch data from api using retrofit
    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(api)
    }

    private fun okhttpClient() : OkHttpClient{
        return  OkHttpClient.Builder().also { client ->
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                client.addInterceptor(logging)
            }
        }.build()
    }

    companion object {
        const val BASE_URL = "https://westkruiskadeapi.azurewebsites.net/"
    }
}