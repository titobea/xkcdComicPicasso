package tt.xkcdcomicpicasso;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Random;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import tt.xkcdcomicpicasso.Model.ComicModel;
import tt.xkcdcomicpicasso.Presenter.ComicPresenter;

public class MainActivity extends AppCompatActivity implements ComicPresenter.ComicPresenterListener {

    private Button btnCarrusel;
    private ImageView imagen;

    private ComicPresenter comicPresenter;

    private int maxComic=0;
    private String urlCache="";
    private Boolean carruselActivo=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnCarrusel = (Button) findViewById(R.id.btnCarrusel);
        imagen=(ImageView)findViewById(R.id.imageView);

        comicPresenter = new ComicPresenter(this, this);
        comicPresenter.getComic(maxComic, false);
    }

    @Override
    public void comicReady(ComicModel comic, Boolean paraCache) {
        if (paraCache){//si es para cache y tenemos algo, cargamos el cache y guardamos la url actual
            if (comic != null) {
                Picasso.with(this).load(comic.getImg()).fetch();
                urlCache=comic.getImg();
                if (carruselActivo) {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            llenarImagen(urlCache);
                        }
                    };
                    handler.postDelayed(runnable, 5000);
                }
            }
        }else {//si no es para cache, si hay error lo mostramos, sino cargamos la imagen
            if (comic == null) {//si hay un problema de conexión tambien pongo la imagen de error
                Picasso.with(this).load(R.drawable.error).into(imagen);
                Toast.makeText(this, "Error de comunicación con el servidor", Toast.LENGTH_LONG).show();
            } else {
                if (maxComic == 0) maxComic = Integer.parseInt(comic.getNum());
                llenarImagen(comic.getImg());
            }
        }
    }

    private void llenarImagen(String url){
        Transformation transformation = new Transformation() {
            @Override
            public Bitmap transform(Bitmap source) {
                Matrix matrix = new Matrix();
                if (source.getHeight() < source.getWidth())
                    matrix.postRotate(90);//si es mas ancha que alta la giro
                Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

                if (result != source) {
                    source.recycle();
                }
                return result;
            }
            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };
        Picasso.with(this)
                .load(url)
                .error(R.drawable.error)//imagen de error
                .placeholder(R.drawable.progress_animation)//imagen de carga en animación
                .transform(transformation)//esto es el ajuste con rotación
                .transform(new ColorFilterTransformation(Color.parseColor("#10BB3333")))//la aplico un color suave
                .transform(new RoundedCornersTransformation(20, 2))//redondeo los cantos pero poco //no voy a poner mas transformaciones porque al final estropean el comic
                .into(imagen);
        Picasso.with(this).invalidate(url);
        urlCache="";
        cargarImagen();//rellenamos el cache
    }

    public void carrusel(View v){
        if (btnCarrusel.getText().equals("INICIAR CARRUSEL")){
            btnCarrusel.setText("PARAR CARRUSEL");
            carruselActivo=true;
            cargarImagen();
        }else{
            btnCarrusel.setText("INICIAR CARRUSEL");
            carruselActivo=false;
        }
    }

    public void nuevoComic(View v){
        if (carruselActivo) carrusel(null);
        cargarImagen();
    }

    private void cargarImagen(){
        if (urlCache.equals("")) {//si no hay url pedimos una imagen aleatoria para el cache
            Random rnd = new Random();
            int rndNum = rnd.nextInt(maxComic) + 1;
            comicPresenter.getComic(rndNum, true);
        }else{
            llenarImagen(urlCache);//si hay cargamos la imagen del cache
        }
    }
}
