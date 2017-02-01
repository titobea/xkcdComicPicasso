package tt.xkcdcomicpicasso.Service;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tt.xkcdcomicpicasso.Model.ComicModel;

/**
 * Created by tito_ on 01/02/2017.
 */

public class ComicService {

    private final String BASE_URL = "https://xkcd.com/";

    public interface ComicAPI {
        @GET("{numero}info.0.json")
        Call<ComicModel> loadComic(@Path("numero") String numero);
    }

    public ComicAPI getAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ComicAPI.class);
    }
}
