package com.example.dreamhigh.exercise_20;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    WordsDBHelper mDbHelper;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

     FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
       fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
         InsertDialog();

            }

        });
    ListView list = (ListView) findViewById(R.id.lstWords);
      registerForContextMenu(list);
        mDbHelper = new WordsDBHelper(this);
        ArrayList<Map<String, String>> items = getAll();
        setWordsListView(items);

    }

    protected void onDestroy() {
        super.onDestroy();
         mDbHelper.close();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
       return true;

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         switch (id) {
            case R.id.action_search:
               SearchDialog();
                return true;
             case R.id.action_insert:
                InsertDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
     getMenuInflater().inflate(R.menu.context_menu, menu);

    }

    public boolean onContextItemSelected(MenuItem item) {
       TextView textId = null;
         TextView textWord = null;
        TextView textMeaning = null;
        TextView textSample = null;
        AdapterView.AdapterContextMenuInfo info = null;
      View itemView = null;
        switch (item.getItemId()) {
           case R.id.action_delete:

                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                 itemView = info.targetView;
               textId = (TextView) itemView.findViewById(R.id.textId);
                if (textId != null) {
                  String strId = textId.getText().toString();
                  DeleteDialog(strId);

                }
                break;
             case R.id.action_update:
                 info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                 itemView = info.targetView;
               textId = (TextView) itemView.findViewById(R.id.textId);
               textWord = (TextView) itemView.findViewById(R.id.textViewWord);
                textMeaning = (TextView) itemView.findViewById(R.id.textViewMeaning);
               textSample = (TextView) itemView.findViewById(R.id.textViewSample);
                if (textId != null && textWord != null && textMeaning != null && textSample != null) {
                  String strId = textId.getText().toString();
                    String strWord = textWord.getText().toString();
                    String strMeaning = textMeaning.getText().toString();
                   String strSample = textSample.getText().toString();
                   UpdateDialog(strId, strWord, strMeaning, strSample);
                }
              break;
        }
      return true;
    }
    private void setWordsListView(ArrayList<Map<String, String>> items) {
      SimpleAdapter adapter = new SimpleAdapter(this, items, R.layout.item,
        new String[]{Words.Word._ID, Words.Word.COLUMN_NAME_WORD, Words.Word.COLUMN_NAME_MEANING, Words.Word.COLUMN_NAME_SAMPLE},
        new int[]{R.id.textId, R.id.textViewWord, R.id.textViewMeaning, R.id.textViewSample});
        ListView list = (ListView) findViewById(R.id.lstWords);
       list.setAdapter(adapter);
    }
    private ArrayList<Map<String, String>> getAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                Words.Word._ID,
                Words.Word.COLUMN_NAME_WORD,
              Words.Word.COLUMN_NAME_MEANING,
                Words.Word.COLUMN_NAME_SAMPLE
               };
        //排序
        String sortOrder =Words.Word.COLUMN_NAME_WORD + " DESC";
         Cursor c = db.query(
             Words.Word.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                 null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
             sortOrder                                 // The sort order
       );

        return ConvertCursor2List(c);

    }
    private ArrayList<Map<String, String>> ConvertCursor2List(Cursor cursor) {
      ArrayList<Map<String, String>> result = new ArrayList<>();
      while (cursor.moveToNext()) {
             Map<String, String> map = new HashMap<>();
            map.put(Words.Word._ID, String.valueOf(cursor.getInt(0)));
            map.put(Words.Word.COLUMN_NAME_WORD, cursor.getString(1));
            map.put(Words.Word.COLUMN_NAME_MEANING, cursor.getString(2));
            map.put(Words.Word.COLUMN_NAME_SAMPLE, cursor.getString(3));
          result.add(map);
        }
    return result;
    }
    private void InsertUserSql(String strWord, String strMeaning, String strSample) {
       String sql = "insert into  words(word,meaning,sample) values(?,?,?)";
       SQLiteDatabase db = mDbHelper.getWritableDatabase();
     db.execSQL(sql, new String[]{strWord, strMeaning, strSample});

    }
    private void Insert(String strWord, String strMeaning, String strSample) {
      SQLiteDatabase db = mDbHelper.getWritableDatabase();
         ContentValues values = new ContentValues();
        values.put(Words.Word.COLUMN_NAME_WORD, strWord);
      values.put(Words.Word.COLUMN_NAME_MEANING, strMeaning);
       values.put(Words.Word.COLUMN_NAME_SAMPLE, strSample);
         long newRowId;
        newRowId = db.insert(Words.Word.TABLE_NAME, null, values);
    }

    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
       new AlertDialog.Builder(this)
       .setTitle("新增单词")
       .setView(tableLayout)

     .setPositiveButton("确定", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
                String strWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                String strMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                String strSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();
              Insert(strWord, strMeaning, strSample);
                ArrayList<Map<String, String>> items = getAll();
                setWordsListView(items);
            }

        })
       .setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create()//创建对话框
      .show();//显示对话框

    }
    private void DeleteUseSql(String strId) {
        String sql = "delete from words where _id='" + strId + "'";
       SQLiteDatabase db = mDbHelper.getReadableDatabase();
       db.execSQL(sql);

    }

    private void Delete(String strId) {
      SQLiteDatabase db = mDbHelper.getReadableDatabase();
       String selection = Words.Word._ID + " = ?";
       String[] selectionArgs = {strId};
       db.delete(Words.Word.TABLE_NAME, selection, selectionArgs);
    }
    private void DeleteDialog(final String strId) {
        new AlertDialog.Builder(this).setTitle("删除单词").setMessage("确定删除?").setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteUseSql(strId);
             setWordsListView(getAll());

            }

        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {



            public void onClick(DialogInterface dialogInterface, int i) {



            }


        }).create().show();
    }

    private void UpdateUseSql(String strId, String strWord, String strMeaning, String strSample) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update words set word=?,meaning=?,sample=? where _id=?";
        db.execSQL(sql, new String[]{strWord, strMeaning, strSample, strId});
    }

    private void Update(String strId, String strWord, String strMeaning, String strSample) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Log.v("My", strId);
        Log.v("My", strWord);
        Log.v("My", strMeaning);
        Log.v("My", strSample);
        ContentValues values = new ContentValues();
        values.put(Words.Word.COLUMN_NAME_WORD, strWord);
        values.put(Words.Word.COLUMN_NAME_MEANING, strMeaning);
        values.put(Words.Word.COLUMN_NAME_SAMPLE, strSample);
        String selection = Words.Word._ID + " = ?";
        String[] selectionArgs = {strId};
        int count = db.update(
                Words.Word.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    private void UpdateDialog(final String strId, final String strWord, final String strMeaning, final String strSample) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText) tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText) tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText) tableLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();

                        //既可以使用Sql语句更新，也可以使用使用update方法更新
                        UpdateUseSql(strId, strNewWord, strNewMeaning, strNewSample);
                        //  Update(strId, strNewWord, strNewMeaning, strNewSample);
                        setWordsListView(getAll());
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }

    private ArrayList<Map<String, String>> SearchUseSql(String strWordSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "select * from words where word like ? order by word desc";
        Cursor c = db.rawQuery(sql, new String[]{"%" + strWordSearch + "%"});
        return ConvertCursor2List(c);
    }

    private ArrayList<Map<String, String>> Search(String strWordSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] projection = {
                Words.Word._ID,
                Words.Word.COLUMN_NAME_WORD,
                Words.Word.COLUMN_NAME_MEANING,
                Words.Word.COLUMN_NAME_SAMPLE
        };
        String sortOrder = Words.Word.COLUMN_NAME_WORD + " DESC";
        String selection = Words.Word.COLUMN_NAME_WORD + " LIKE ?";
        String[] selectionArgs = {"%" + strWordSearch + "%"};
        Cursor c = db.query(
                Words.Word.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,
                sortOrder
        );

        return ConvertCursor2List(c);
    }

    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();
                        ArrayList<Map<String, String>> items = null;
                        items = SearchUseSql(txtSearchWord);
                        if (items.size() > 0) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("result", items);
                            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else
                            Toast.makeText(MainActivity.this, "没有找到", Toast.LENGTH_LONG).show();


                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();

    }
}
