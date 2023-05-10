package olmo.eduardo.galeria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class PhotoActivity extends AppCompatActivity {

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
    public boolean onOptionsItemsSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case R.id.opShare:
                sharePhoto();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}