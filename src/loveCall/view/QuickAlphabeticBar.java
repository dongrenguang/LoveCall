package loveCall.view;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.loveCall.R;


/**
 * ��ĸ������
 * 
 * @author Administrator
 * 
 */
public class QuickAlphabeticBar extends ImageButton {
	private TextView mDialogText; // �м���ʾ��ĸ���ı���
	private Handler mHandler; // ����UI�ľ��
	private ListView mList; // �б�
	private float mHight; // �߶�
	// ��ĸ�б�����
	private String[] letters = new String[] { "#", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };
	// ��ĸ������ϣ��
	private HashMap<String, Integer> alphaIndexer;
	Paint paint = new Paint();
	boolean showBkg = false;
	int choose = -1;

	public QuickAlphabeticBar(Context context) {
		super(context);
	}

	public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public QuickAlphabeticBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// ��ʼ��
	public void init(Activity ctx) {
		mDialogText = (TextView) ctx.findViewById(R.id.fast_position);
		mDialogText.setVisibility(View.INVISIBLE);
		mHandler = new Handler();
	}

	// ������Ҫ�������б�
	public void setListView(ListView mList) {
		this.mList = mList;
	}

	// ������ĸ������ϣ��
	public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
		this.alphaIndexer = alphaIndexer;
	}

	// ������ĸ�������ĸ߶�
	public void setHight(float mHight) {
		this.mHight = mHight;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int act = event.getAction();
		float y = event.getY();
		final int oldChoose = choose;
		// ������ָλ�ã��ҵ���Ӧ�ĶΣ���mList�ƶ��ο�ͷ��λ����
		int selectIndex = (int) (y / (mHight / letters.length));

		if (selectIndex > -1 && selectIndex < letters.length) { // ��ֹԽ��
			String key = letters[selectIndex];
			if (alphaIndexer.containsKey(key)) {
				int pos = alphaIndexer.get(key);
				if (mList.getHeaderViewsCount() > 0) { // ��ֹListView�б�����,������û��
					this.mList.setSelectionFromTop(
							pos + mList.getHeaderViewsCount(), 0);
				} else {
					this.mList.setSelectionFromTop(pos, 0);
				}
				mDialogText.setText(letters[selectIndex]);
			}
		}
		switch (act) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != selectIndex) {
				if (selectIndex > 0 && selectIndex < letters.length) {
					choose = selectIndex;
					invalidate();
				}
			}
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (mDialogText != null
								&& mDialogText.getVisibility() == View.INVISIBLE) {
							mDialogText.setVisibility(VISIBLE);
						}
					}
				});
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != selectIndex) {
				if (selectIndex > 0 && selectIndex < letters.length) {
					choose = selectIndex;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			if (mHandler != null) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (mDialogText != null
								&& mDialogText.getVisibility() == View.VISIBLE) {
							mDialogText.setVisibility(INVISIBLE);
						}
					}
				});
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		int sigleHeight = height / letters.length; // ������ĸռ�ĸ߶�
		for (int i = 0; i < letters.length; i++) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(20);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			if (i == choose) { 
				paint.setColor(getResources().getColor(R.color.pink)); // ����ʱ������ĸ��ɫ
				paint.setFakeBoldText(true);
			}
			// �滭��λ��
			float xPos = width / 2 - paint.measureText(letters[i]) / 2;
			float yPos = sigleHeight * i + sigleHeight;
			canvas.drawText(letters[i], xPos, yPos, paint);
			paint.reset();
		}
	}

}
