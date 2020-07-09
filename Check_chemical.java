package com.example.cp_cop_0621;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Check_chemical extends AppCompatActivity {

    String dbname = "chemical.db";
    String tablename = "chemical_material";
    String sql;
    SQLiteDatabase db;   // db를 다루기 위한 SQLiteDatabase 객체 생성
    Cursor resultset;   // select 문 출력위해 사용하는 Cursor 형태 객체 생성
    ListView listView;   // ListView 객체 생성
    String[] result;   // ArrayAdapter에 넣을 배열 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_chemical);
        db = openOrCreateDatabase(dbname, MODE_PRIVATE, null);   // 있으면 열고 없으면 DB를 생성
        listView = findViewById(R.id.listview);
        try {
            sql = "select * from "+ tablename;
            resultset = db.rawQuery(sql, null);   // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)
            int count = resultset.getCount();   // db에 저장된 행 개수를 읽어온다
            int j = 0;
            result = new String[count];   // 저장된 행 개수만큼의 배열을 생성

            for (int i = 0; i < count; i++) {

                resultset.moveToNext();   // 첫번째에서 다음 레코드가 없을때까지 읽음
                String str_name = resultset.getString(1);   // 첫번째 속성
                String start_date = resultset.getString(2);   // 두번째 속성
                String end_date = resultset.getString(3);   // 세번째 속성
                int date_s = Integer.valueOf(start_date);
                int date_e = Integer.valueOf(end_date);
                if(date_e - date_s >= 30000) {
                    result[i] = str_name + "               " +start_date + "               " + end_date;   // 각각의 속성값들을 해당 배열의 j번째에 저장
                    j++;
                }
                else{
                    result[i] = "" + "" + "" + "" +"";   // 각각의 속성값들을 해당 배열의 j번째에 저장

                }

            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, result);   // ArrayAdapter(this, 출력모양, 배열)
            listView.setAdapter(adapter);   // listView 객체에 Adapter를 붙인다

        } catch (Exception e) { }
    }
}

