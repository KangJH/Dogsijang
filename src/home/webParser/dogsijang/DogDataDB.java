package home.webParser.dogsijang;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DogDataDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "dogsijang.db";
	private static final String TABLE_NAME = "dogs";
	private static final String COL_NO = "no";
	private static final String COL_SPECIES = "species";
	private static final String COL_CHARACTER = "character";
	private static final String COL_PRICE = "price";
	private static final String COL_URI = "uri";
	private static final String COL_CONTACT_NUM = "contact";
	private static final String COL_READMARK = "readmark";
    private static final int DATABASE_VERSION = 2;
    private static final int DB_MAX_ROW =100;
	public DogDataDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_NO + " INTEGER, "
				+ COL_SPECIES + " TEXT, "
				+ COL_CHARACTER + " TEXT, "
				+ COL_PRICE + " TEXT, "
				+ COL_URI + " TEXT, "
				+ COL_CONTACT_NUM + " TEXT, "
				+ COL_READMARK + " INTEGER"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS '" + TABLE_NAME + "'");
        onCreate(db); 
	}
	
	public int getLatestDogNo() {
		int ret = -1;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "
				+ "MAX(" + COL_NO + ") "
				+ "FROM " + TABLE_NAME + ";";
		Cursor cursor = db.rawQuery(sql , null);
		
		if(cursor.getCount() > 0) { 
			if(cursor.moveToNext()){
				ret = cursor.getInt(0);
			}
		}
		cursor.close();
		db.close();
		return ret;
	}
	
	private int geDataCount() {
		int ret = -1;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "
				+ COL_NO + " "
				+ "FROM " + TABLE_NAME + ";";
		Cursor cursor = db.rawQuery(sql , null);
		ret = cursor.getCount();
		cursor.close();
		db.close();
		return ret;
	}
	
	private int getOldestDogNo(int from) {
		int ret = -1;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "
				+ COL_NO
				+ " FROM " + TABLE_NAME 
				+ " ORDER BY " + COL_NO +" DESC;";
		Cursor cursor = db.rawQuery(sql , null);
		
		if(cursor.getCount() > from) { 
			do {
				cursor.moveToNext();
			} while(from > 0);
			//while(cursor.moveToNext()){
			//}
			ret = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return ret;
	}
	
	public void update(DogData dog) {
		SQLiteDatabase db = getWritableDatabase();
		int iReadMark = dog.blReadMark == false ? 0 : 1; 
		String sql = "UPDATE " + TABLE_NAME + " SET "
				/*+ COL_SPECIES + "='" + dog.strSpecies + "', "
				+ COL_CHARACTER + "='" + dog.strCharacter + "', "
				+ COL_PRICE + "='" + dog.strPrice + "', "
				+ COL_URI + "='" + dog.strUri + "', "
				+ COL_CONTACT_NUM + "='" + dog.strContactNum + "', "*/
				+ COL_READMARK + "=" +iReadMark + " "
				+ "WHERE " + COL_NO + "=" + dog.iNo + ";";
        db.execSQL(sql);
		
		db.close();
	}
	
	public void add(DogData dog) {
		SQLiteDatabase db = null;

		//If data count is over MAX_ROW, remove oldest Data
		int iDataCount = geDataCount();
		if(iDataCount >= DB_MAX_ROW) {
			//remove oldest data
			int iOldestNo = getOldestDogNo(0);
			if(iOldestNo > 0) {
				String delSql = "DELETE FROM " + TABLE_NAME + " WHERE "
						+ COL_NO + "=" + iOldestNo + ";";
				db = getWritableDatabase();
				db.execSQL(delSql);
				db.close();
				Log.d("Test", "DB-del:" + iOldestNo);
			}
		}
		//insert new data
		db = getWritableDatabase();
		int iReadMark = dog.blReadMark == false ? 0 : 1;
		String insertSql = "INSERT INTO " + TABLE_NAME
				+ "(" + COL_NO + ", "
				+ COL_SPECIES + ", "
				+ COL_CHARACTER + ", "
				+ COL_PRICE + ", "
				+ COL_URI + ", "
				+ COL_CONTACT_NUM + ", "
				+ COL_READMARK + ")" + " VALUES("
				+ dog.iNo + ", "
				+ "'" + dog.strSpecies + "', "
				+ "'" + dog.strCharacter + "', "
				+ "'" + dog.strPrice + "', "
				+ "'" + dog.strUri + "', "
				+ "'" + dog.strContactNum + "', "
				+ iReadMark
				+ ");";
		db.execSQL(insertSql);
		db.close();
	}
	public void addAll(ArrayList<DogData> inputs) {
		SQLiteDatabase db = null;
		if(inputs != null && !inputs.isEmpty()) {
			//If data count is over MAX_ROW, remove oldest Data
			int iDataCount = geDataCount();
			int inputSize = inputs.size();
			if(iDataCount + inputSize >= DB_MAX_ROW) {
				//remove oldest data
				int iOldestNo = getOldestDogNo(inputSize - 1);
				if(iOldestNo > 0) {
					String delSql = "DELETE FROM " + TABLE_NAME + " WHERE "
							+ COL_NO + "<=" + iOldestNo + ";";
					db = getWritableDatabase();
					db.execSQL(delSql);
					db.close();
					Log.d("Test", "DB-del:" + iOldestNo);
				}
			}
			//insert new data
			db = getWritableDatabase();
			for(DogData dog : inputs) {
				int iReadMark = dog.blReadMark == false ? 0 : 1;
				String insertSql = "INSERT INTO " + TABLE_NAME
						+ "(" + COL_NO + ", "
						+ COL_SPECIES + ", "
						+ COL_CHARACTER + ", "
						+ COL_PRICE + ", "
						+ COL_URI + ", "
						+ COL_CONTACT_NUM + ", "
						+ COL_READMARK + ")" + " VALUES("
						+ dog.iNo + ", "
						+ "'" + dog.strSpecies + "', "
						+ "'" + dog.strCharacter + "', "
						+ "'" + dog.strPrice + "', "
						+ "'" + dog.strUri + "', "
						+ "'" + dog.strContactNum + "', "
						+ iReadMark
						+ ");";
				db.execSQL(insertSql);
			}
			db.close();
		}
	}
	public ArrayList<DogData> loadDB() {
		//Log.d("Test", getLatestDogNo() +", " + getOldestDogNo());
		ArrayList<DogData> datas = null;
		SQLiteDatabase db = getReadableDatabase();
		String sql = "SELECT "
				+ COL_NO + ", "
				+ COL_SPECIES + ", "
				+ COL_CHARACTER + ", "
				+ COL_PRICE + ", "
				+ COL_URI + ", "
				+ COL_CONTACT_NUM + ", "
				+ COL_READMARK + " "
				+ "FROM " + TABLE_NAME + ";";
		Cursor cursor = db.rawQuery(sql , null);
		
		if(cursor.getCount() > 0) { 
			datas = new ArrayList<DogData>();
			while(cursor.moveToNext()){
				DogData dog = new DogData();
				dog.iNo = cursor.getInt(0);
			    dog.strSpecies = cursor.getString(1);
			    dog.strCharacter = cursor.getString(2);
			    dog.strPrice = cursor.getString(3);
			    dog.strUri = cursor.getString(4);
			    dog.strContactNum = cursor.getString(5);
			    dog.blReadMark = cursor.getInt(6) == 0 ? false: true;
			    datas.add(dog);
			}
		}
		cursor.close();
		db.close();
		return datas;
	}
}
