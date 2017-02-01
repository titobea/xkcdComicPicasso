package tt.xkcdcomicpicasso.Presenter;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tt.xkcdcomicpicasso.Model.ComicModel;
import tt.xkcdcomicpicasso.Service.ComicService;

/**
 * Created by tito_ on 01/02/2017.
 */

public class ComicPresenter {
    private final Context context;
    private final ComicPresenterListener mListener;
    private final ComicService comicService;

    public interface ComicPresenterListener{
        void comicReady(ComicModel comic, Boolean paraCache);
    }

    public ComicPresenter(ComicPresenterListener listener, Context context){
        this.mListener = listener;
        this.context = context;
        this.comicService = new ComicService();
    }

    public void getComic(int numero, final Boolean paraCache) {
        String numeroPlus="";
        if (numero!=0){
            numeroPlus=""+numero+"/";
        }

        comicService
                .getAPI()
                .loadComic(numeroPlus)
                .enqueue(new Callback<ComicModel>() {
                    @Override
                    public void onResponse(Call<ComicModel> call, Response<ComicModel> response) {
                        ComicModel result = response.body();
                        if(result != null) mListener.comicReady(result, paraCache);
                    }

                    @Override
                    public void onFailure(Call<ComicModel> call, Throwable t) {
                        mListener.comicReady(null, paraCache);
                    }
                });
    }
}
