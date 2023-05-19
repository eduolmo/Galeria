package olmo.eduardo.galeria.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import olmo.eduardo.galeria.R;
import olmo.eduardo.galeria.adapter.MainAdapter;
import olmo.eduardo.galeria.util.Util;

public class MainActivity extends AppCompatActivity {

    static int RESULT_TAKE_PICTURE = 1;
    static int RESULT_REQUEST_PERMISSION = 2;

    List<String> photos = new ArrayList<>();
    MainAdapter mainAdapter;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //obtendo a toolbar
        Toolbar toolbar = findViewById(R.id.tbMain);
        //configurando a toolbar como a barra de ferramentas padrao
        setSupportActionBar(toolbar);

        //acessando o diretorio Pictures
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //criando a lista de fotos
        File[] files = dir.listFiles();

        //adicionando as fotos salvas na lista de fotos
        for(int i = 0; i < files.length; i++){
            photos.add(files[i].getAbsolutePath());
        }

        //criando o mainAdapter
        mainAdapter = new MainAdapter(MainActivity.this, photos);

        //pegando a rvGallery
        RecyclerView rvGallery = findViewById(R.id.rvGallery);
        //colocando o mainAdapter na rvGallery
        rvGallery.setAdapter(mainAdapter);

        //pegando a largura das imagens
        float w = getResources().getDimension(R.dimen.itemWidth);
        //calculando o numero de colunas que cabe na tela
        int numberOfColums = Util.calculateNoOfColumns(MainActivity.this, w);
        //criando um gridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, numberOfColums);
        //colocando o gridLayoutManager na rvGallery
        rvGallery.setLayoutManager(gridLayoutManager);

        //lista de permissoes que a app precisa
        List<String> permissions = new ArrayList<>();
        //adicionando na lista a permissao para usar a camera
        permissions.add(Manifest.permission.CAMERA);
        //verificando se a permissao para usar a camera foi concedida
        checkForPermissions(permissions);

    }

    //metodo que cria as opcoes de menu, com base no arquivo de menu passado como parametro
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_tb, menu);
        return true;
    }

    //metodo que gerencia quais acoes sao realizadas quando um clicam em um botao
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.opCamera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startPhotoActivity(String photoPath){
        //criando nova Intent para a PhotoActivity
        Intent i = new Intent(MainActivity.this, PhotoActivity.class);
        //colocando o caminho para a imagem dentro da Intent
        i.putExtra("photo_path", photoPath);
        //iniciando a PhotoActivity
        startActivity(i);
    }


    private void dispatchTakePictureIntent()
    {
        //criando um arquivo vazio
        File f = null;
        try{
            //chamando a funcao que cria um arquivo de imagem dentro da pasta Pictures
            f = createImageFile();
        }
        catch (IOException e){
            //mostrando uma mensagem de erro caso nao consiga criar o arquivo
            Toast.makeText(MainActivity.this, "Não foi possível criar o arquivo", Toast.LENGTH_LONG).show();
            return;
        }

        //colocando dentro de currentPhotoPath o caminho da imagem
        currentPhotoPath = f.getAbsolutePath();

        if(f != null){
            //gerando uma URI para a imagem
            Uri fUri = FileProvider.getUriForFile(MainActivity.this,"olmo.eduardo.galeria.fileprovider", f);
            //criando Intent para disparar a app de camera
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //colocando a URI na Intent
            i.putExtra(MediaStore.EXTRA_OUTPUT, fUri);
            //iniciando a camera, e o app fica esperando o resultado
            startActivityForResult(i, RESULT_TAKE_PICTURE);
        }
    }

    //
    private File createImageFile() throws IOException{
        //pegando data e formatando ela
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //criando o nome do arquivo de imagem
        String imageFileName = "JPEG_" + timeStamp;
        //pegando o diretorio Pictures
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //criando o arquivo de imagem dentro do diretorio Pictures
        File f = File.createTempFile(imageFileName, ".jpg", storageDir);
        //retornando o arquivo de imagem criado
        return f;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        //conferindo se p resultado corresponde a acao de tirar uma foto
        if(requestCode == RESULT_TAKE_PICTURE){
            //conferindo se a foto foi tirada
            if(resultCode == Activity.RESULT_OK){
                //adicionando a foto na lista de fotos
                photos.add(currentPhotoPath);
                //avisando o mainAdapter que uma nova foto foi inserida na lista
                mainAdapter.notifyItemInserted(photos.size()-1);
            }
            //caso a foto nao tenha sido tirada
            else{
                //pegando o arquivo de imagem
                File f = new File(currentPhotoPath);
                //deletando o arquivo de imagem vazio
                f.delete();
            }
        }
    }

    private void checkForPermissions(List<String> permissions){
        //criando uma lista para as permissoes nao concedidas
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions){
            //caso aquela permissao nao seja concedida ela é colocada dentro da lista de permissoes nao aprovadas
            if(!hasPermission(permission)){
                permissionsNotGranted.add(permission);
            }
            //as permissoes nao concedidas sao requisitadas ao usuario
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(permissionsNotGranted.size() > 0){
                    requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
                }
            }
        }
    }

    //verifica se uma permissao ja foi concedida ou nao
    private boolean hasPermission(String permission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return ActivityCompat.checkSelfPermission(MainActivity.this,permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        //criando lista de permissoes nao concedidas
        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION){
            for(String permission : permissions){
                //caso uma permissao nao tenha sido concedida ela é colocada dentro da lista de permissoes nao concedidas
                if(!hasPermission(permission)){
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                    //mostra uma mensagem para o usuario caso um permissao nao tenha sido concedida e ela seja necessaria para a app
                    new AlertDialog.Builder(MainActivity.this).setMessage("Para usar essa app é preciso conceder essas permissões").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            //pede permissao novamente, caso seja necessario para a app
                            requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]),RESULT_REQUEST_PERMISSION);
                        }
                    }).create().show();
                }
            }
        }


    }


}