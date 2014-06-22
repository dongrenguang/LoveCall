package loveCall.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import loveCall.view.BarChartView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class ScreenShot {
	Activity activity;
	public ScreenShot(Activity activity) {
		this.activity = activity;
	}

	public  Bitmap takeScreeShot(BarChartView chartView){
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		//获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		//获取屏幕长和高 
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		//去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	//保存到sdcard
	public  void savePic(Bitmap b){
		String path ="history.png";
		try{
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
			}

			FileOutputStream fos = new FileOutputStream(file);


			b.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
			fos.close();

			Log.d("save picture", "save success");
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}
