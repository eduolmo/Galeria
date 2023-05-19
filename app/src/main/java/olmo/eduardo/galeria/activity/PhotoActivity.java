package olmo.eduardo.galeria.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import olmo.eduardo.galeria.R;
import olmo.eduardo.galeria.util.Util;

public class PhotoActivity extends AppCompatActivity {

    String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        //pegando a toolbar pelo id
        Toolbar toolbar = findViewById(R.id.tbPhoto);
        //configurando a toolbar como a barra de ferramentas padrao
        setSupportActionBar(toolbar);

        //pegando actionbar
        ActionBar actionBar = getSupportActionBar();
        //inserindo o botao de voltar na actionbar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //pegando intent
        Intent i = getIntent();
        //pegando o camimnho da foto selecionada, que estava armazenada na intent
        photoPath = i.getStringExtra("photo_path");

        //salvando a foto em um bitmap
        Bitmap bitmap = Util.getBitmap(photoPath);
        //pegando o imPhoto
        ImageView imPhoto = findViewById(R.id.imPhoto);
        //colocando a imagem salva no bitmap dentro do imPhoto
        imPhoto.setImageBitmap(bitmap);
    }

    //metodo que cria as opcoes de menu, com base no arquivo de menu passado como parametro
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photo_activity_tb, menu);
        return true;
    }

    //metodo que gerencia quais acoes sao realizadas quando um clicam em um botao
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void sharePhoto(){
        //criando URI para compartilhar a foto
        Uri photoUri = FileProvider.getUriForFile(PhotoActivity.this,"olmo.eduardo.galeria.fileprovider",new File(photoPath));
        //criando nova Intent para realizar o compartilhamento
        Intent i = new Intent(Intent.ACTION_SEND);
        //colocando a URI na Intent
        i.putExtra(Intent.EXTRA_STREAM, photoUri);
        //informando que a URI é para um arquivo de imagem
        i.setType("image/jpeg");
        //executando o Intent e iniciando o compartilhamento
        startActivity(i);
    }

}