package com.example.cp_cop_0621;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;

import static java.sql.DriverManager.println;

public class Input_db extends AppCompatActivity {
    EditText editText1;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    TextView textView;
    ListView listView;
    String[] result;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_db);

        editText1 = (EditText)findViewById(R.id.edit_open);
        editText2 = (EditText)findViewById(R.id.edit_make);
        editText3 = (EditText)findViewById(R.id.edit_adda);
        editText4 = (EditText)findViewById(R.id.edit_addp);

        textView = (TextView)findViewById(R.id.textview);
        listView = (ListView)findViewById(R.id.listview);

        Button btn_open = (Button)findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databaseName = editText1.getText().toString();
                openDatabase(databaseName);
                createTable("chemical_material");
            }
        });

        Button btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText2.getText().toString().trim();
                String startstr = editText3.getText().toString().trim();
                String finishstr = editText4.getText().toString().trim();

                int start_date = -1;
                int finish_date = -1;
                try{
                    start_date = Integer.parseInt(startstr);
                    finish_date = Integer.parseInt(finishstr);
                }catch(Exception e){}
                insertData(name,start_date,finish_date);
            }
        });
        Button btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectData("chemical_material");
            }
        });

        Button btn_search = (Button)findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchData();
            }
        });

        Button btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

    }

    public void openDatabase(String databaseName){
        DatabaseHelper helper = new DatabaseHelper(this, databaseName, null, 1);
        database = helper.getWritableDatabase();
    }

    public void createTable(String tableName){
        textView.setText("");
        println("데이터베이스 오픈됨");

        if(database != null){
            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, start_date integer, finish_date integer)";
            database.execSQL(sql);
        }
        else{
            println("먼저 데이터베이스를 오픈하세요");
        }
    }
    public void println(String data){
        textView.append(data + "\n");
    }

    public void insertData(String name, int start_date, int finish_date){
        textView.setText("");

        if(database != null){
            String sql = "insert into chemical_material(name, start_date, finish_date) values(?, ?, ?)";
            Object[] params = {name, start_date, finish_date};
            database.execSQL(sql, params);

            println("데이터 추가됨");
        }
        else {
            println("먼저 데이터베이스를 오픈하세요");
        }
    }

    public void selectData(String tableName){
        textView.setText("");

        if(database != null){
            String sql = "select name, start_date, finish_date from " + tableName;
            Cursor cursor = database.rawQuery(sql, null); //두번째 파라미터 ?로 주고 변경하능
            int count = cursor.getCount();
            result = new String[count];
            println("조회된 데이터 개수 : " + cursor.getCount());

            try {
                while(cursor.moveToNext()){
                    String name = cursor.getString(0);
                    int start_date = cursor.getInt(1);
                    int finish_date = cursor.getInt(2);
                    println("-----------------------------------------------------------------------------------------------------------");
                    println("  " + name + "  |  " + start_date + "  |  " + finish_date );
                    println("-----------------------------------------------------------------------------------------------------------");
                }
                cursor.close();
            }catch (Exception e){}
        }
        else{
            println("먼저 데이터베이스를 오픈하세요");
        }
    }


    public void searchData(){
        textView.setText("");

        if(database != null){
            String names = editText2.getText().toString();
            String sql = "select name, start_date, finish_date from chemical_material where name='" + names + "';";
            Cursor cursor = database.rawQuery(sql, null);
            println("조회된 데이터 개수 : " + cursor.getCount());

            while(cursor.moveToNext()){
                String name = cursor.getString(0);
                int start_date = cursor.getInt(1);
                int finish_date = cursor.getInt(2);
                println("-----------------------------------------------------------------------------------------------------------");
                println("  " + name + "  |  " + start_date + "  |  " + finish_date );
                println("-----------------------------------------------------------------------------------------------------------");
            }
            cursor.close();
        }
        else{
            println("먼저 데이터베이스를 오픈하세요");
        }
    }
    public void deleteData(){
        textView.setText("");

        if(database != null){
            String names = editText2.getText().toString();
            String sql = "delete from chemical_material where name=" +"(select name from chemical_material where name='"+ names+"')";
            Cursor cursor = database.rawQuery(sql,null);

            println("삭제된 화학 약품 명 : " + editText2.getText().toString());

            while(cursor.moveToNext()){
                String name = cursor.getString(0);
                int start_date = cursor.getInt(1);
                int finish_date = cursor.getInt(2);
                println("-----------------------------------------------------------------------------------------------------------");
                println("  " + name + "  |  " + start_date + "  |  " + finish_date );
                println("-----------------------------------------------------------------------------------------------------------");
            }
            cursor.close();
        }
        else{
            println("먼저 데이터베이스를 오픈하세요");
        }
    }



    class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context,String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String tableName = "chemical_material";
            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, start_date integer, finish_date integer)";
            db.execSQL(sql);

            println("테이블 생성됨");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("onUpgrade 호출됨 : " + oldVersion + ", " + newVersion);
            if(newVersion > 1){
                String tableName = "chemical_material";
                db.execSQL("drop table if exists " + tableName); //altertable로 변경가능
                println("테이블 삭제함");

                String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, start_date integer, finish_date integer)";
                db.execSQL(sql);
                println("테이블 생성됨");
            }
        }
    }
}


