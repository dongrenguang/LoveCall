package loveCall.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	private static DBHelper dbHelper;

	private DBHelper(Context context, String name,
			int version) {
		super(context, name, null, version);
	}
	
	public static DBHelper getInstance(Context context, int version){
		if(dbHelper==null){
			dbHelper=new DBHelper(context, "loveCallDB", version);
		}
		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 String[] createSqls = {
				 "create table if not exists specialGroup (groupname varchar(30))",
				 "create table if not exists specialPerson (groupname varchar(30), contactname varchar(30), number varchar(20))",
				 "create table if not exists callRecord (number varchar(30), type int, start varchar(30), end varchar(30))",
				 "create table if not exists reminder (flag int,contactname varchar(30), contactphone varchar(30), note varchar(200), details varchar(200) )"
				 };
		 for(int i = 0; i < createSqls.length; i++){
			 db.execSQL(createSqls[i]);
		 }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
	
}
