package com.pictureoftheday;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PicOfDayActivity extends Activity {
	private ProgressDialog pd;
	private IotdHandler iotdHandler;
	private Handler handler;
	private Bitmap imgBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_of_day);
		handler = new Handler();
		refreshFromFeed();
	}

	public void onRefresh(View view) {
		refreshFromFeed();
	}

	public void onSetWallpaper(View view) {
		Thread thread = new Thread() {
			public void run() {
				WallpaperManager wallpaperManager = WallpaperManager
						.getInstance(PicOfDayActivity.this);
				try {
					wallpaperManager.setBitmap(imgBitmap);
					handler.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(PicOfDayActivity.this,
									"Wallpaper Set", Toast.LENGTH_LONG).show();

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					handler.post(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(PicOfDayActivity.this,
									"Wallpaper Set Failed", Toast.LENGTH_LONG).show();

						}
					});
				}
			}
		};

		thread.run();
	}

	private void refreshFromFeed() {

		pd = ProgressDialog.show(this, "Loading", "Loading pic of the day");

		Thread thread = new Thread() {
			public void run() {
				if (iotdHandler == null) {
					iotdHandler = new IotdHandler();
				}
				iotdHandler.processFeed();
				imgBitmap = iotdHandler.getImage();
				handler.post(new Runnable() {

					@Override
					public void run() {
						resetDisplay(iotdHandler.getTitle(),
								iotdHandler.getDate(), iotdHandler.getImage(),
								iotdHandler.getDescription().toString());

						pd.dismiss();

					}
				});

			}
		};

		thread.start();
	}

	public void resetDisplay(String title, String date, Bitmap image,
			String descr) {
		TextView titleView = (TextView) findViewById(R.id.imageTitle);
		titleView.setText(title);
		TextView dateView = (TextView) findViewById(R.id.imageDate);
		dateView.setText(date);
		ImageView imageView = (ImageView) findViewById(R.id.imageDisplay);
		imageView.setImageBitmap(image);

		TextView descrView = (TextView) findViewById(R.id.imageDescription);
		descrView.setText(descr);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_pic_of_day, menu);
		return true;
	}
}
