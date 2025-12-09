package ma.projet.restclient.config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitClient {
    private static Retrofit mInstance = null;
    private static String activeFormat = null;
    private static final String API_URL = "http://10.0.2.2:8082/";

    public static Retrofit getClient(String desiredFormat) {
        if (mInstance == null || !desiredFormat.equals(activeFormat)) {
            ma.projet.restclient.utils.CustomUtils.logInfo("NetworkConfig", "Building client for: " + desiredFormat);
            activeFormat = desiredFormat;

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(API_URL);

            if ("JSON".equals(desiredFormat)) {
                builder.addConverterFactory(GsonConverterFactory.create());
            } else {
                builder.addConverterFactory(SimpleXmlConverterFactory.createNonStrict());
            }

            mInstance = builder.build();
        }
        return mInstance;
    }
}
