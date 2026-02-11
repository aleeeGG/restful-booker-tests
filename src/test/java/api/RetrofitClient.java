package api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.InputStream;
import java.util.Properties;

public class RetrofitClient {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = RetrofitClient.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ReqresApi api() {
        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor();

        logging.setLevel(
                HttpLoggingInterceptor.Level.BODY
        );

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })

                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(props.getProperty("base.url"))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ReqresApi.class);
    }
}
