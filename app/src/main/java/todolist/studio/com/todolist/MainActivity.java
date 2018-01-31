package todolist.studio.com.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private EditText textAdicionar;
    private Button botaoAdicionar;
    private ListView listNomes;
    private SQLiteDatabase bancoDados;
    private ArrayList<String> tarefas;
    private ArrayAdapter<String> listaTarefas;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {


            textAdicionar = findViewById(R.id.editTextAdicionar);
            botaoAdicionar = findViewById(R.id.botaoAdicionar);
            listNomes = findViewById(R.id.listNomes);

            //DB
            bancoDados = openOrCreateDatabase("appTarefas", MODE_PRIVATE, null);

            //tabela tarefas
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            botaoAdicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textoDigitado = textAdicionar.getText().toString();

                    inserirTarefa(textoDigitado);
                }
            });

            listNomes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    removerTarefa(ids.get(position));
                    return true;
                }
            });

            listNomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Toast.makeText(MainActivity.this, "Id da Tarefa: " + ids.get(position).toString(), Toast.LENGTH_SHORT).show();
                }
            });

            listarTarefas();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inserirTarefa(String texto){
        try{
            if(!texto.isEmpty()){
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + texto + "')");
                Toast.makeText(MainActivity.this, "Tarefa [ " + texto + " ] inserida com sucesso!", Toast.LENGTH_SHORT).show();
                textAdicionar.setText("");
                listarTarefas();
            } else {
                Toast.makeText(MainActivity.this, "Tarefa vazia! Favor digitar Tarefa.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listarTarefas(){
        try{

            //cursor recupera tarefas
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");



            tarefas = new ArrayList<String>();
            ids = new ArrayList<Integer>();

            listaTarefas = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    tarefas);

            listNomes.setAdapter(listaTarefas);

            //volta para o inicio, pq o cursor parou no pultimo registro
            cursor.moveToFirst();
            while (cursor != null){

                Log.i("RESULTADO - ", "Tarefa: " + cursor.getString(indiceColunaTarefa));
                tarefas.add(cursor.getString(indiceColunaTarefa)); //cria indice automaticamente na lista
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId))); //cria indice automaticamente na lista

                cursor.moveToNext();
            }



        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id = " + id);
            Toast.makeText(MainActivity.this, "Tarefa removida!", Toast.LENGTH_SHORT).show();
            listarTarefas();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
