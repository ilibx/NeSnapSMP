package org.openparallel.imagethresh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.openparallel.imagethresh.R;
import org.openparallel.imagethresh.R.id;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Camera;

@SuppressLint("ParserError")
public class ImageThreshActivity extends Activity {
	/** Called when the activity is first created. */

	static {
		System.loadLibrary("opencv");
	}

	//OpenCV JNI functions
	//public native byte[] findContours(int[] data, int w, int h);
	public native boolean applyNeonising();
	public native boolean applyFunhouseEffect();
	public native boolean applySketchbookEffect();
	public native byte[] getSourceImage();
	public native boolean setSourceImage(int[] data, int w, int h);
	public native void doGrayscaleTransform();
	public native boolean doChainOfImageProcessingOperations();
	public native void setWorkingDir(String string);
	public native boolean imageProcessingHasFinished();
	public native String stringFromJNI();
	
	public native float STWithOpenCV();
	public native float STWithManualManips();
	public native float STWithManualManipsAndSMP();
	public native float STWithNeTen();
	public native float STWithNeTenAndSMP();
	public native float STWithNeon();
	public native float STWithNeonAndSMP();
	
	//Image capture constants
	final int PICTURE_ACTIVITY = 1000; // This is only really needed if you are catching the results of more than one activity.  It'll make sense later.
	public static final String TEMP_PREFIX = "tmp_";

	//private variables needed for image capture
	private ImageView imageView;
	private TextView runtimeView;
	
	private Uri imageUri;

	//these variables are used to determine the front or back camera
	//a bit of a hack is used here by examining the higher resolution
	//of the back camera
	private int backCameraPhotoWidth = -10000;
	private int backCameraPhotoHeight = -10000;
	private int frontCameraPhotoWidth = 10000;
	private int frontCameraPhotoHeight = 10000;

	private int effectNo = 0;

	/* Override the onCreate method */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); // Blah blah blah call the super.
		setContentView(R.layout.main); // It is VERY important that you do this FIRST.  If you don't, the next line will throw a null pointer exception.  And God will kill a kitten.

		//determine camera rotations on startup (this is dependent on what camera on device is used)
		this.WorkoutCameraResolutions();
		this.LogCameraResolutions();

		//rig up a camera button to collect images and to run the bulk of the app when a good photo is taken
		final Button sepiaToneOpenCVButton = (Button)findViewById(R.id.sepia_tone_with_opencv);
		final Button sepiaToneWithManualManipsButton = (Button)findViewById(R.id.sepia_tone_with_manual_manips);
		final Button sepiaToneWithManualManipsAndSMPButton = (Button)findViewById(R.id.sepia_tone_with_manual_manips_and_smp);
		final Button sepiaToneWithNe10Button = (Button)findViewById(R.id.sepia_tone_with_ne10);
		final Button sepiaToneWithNe10AndSMPButton = (Button)findViewById(R.id.sepia_tone_with_ne10_and_smp);
		final Button sepiaToneWithNeonButton = (Button)findViewById(R.id.sepia_tone_with_neon);
		final Button sepiaToneWithNeonAndSMPButton = (Button)findViewById(R.id.sepia_tone_with_neon_and_smp);
		
		runtimeView = (TextView)findViewById(id.runtimeResultTextField);
		imageView = (ImageView)findViewById(id.imageView1);
		
		runtimeView.setText("Runtime (Sec) = 0");
		

		final Button saveButton = (Button)findViewById(id.save_button);

		sepiaToneOpenCVButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 1;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});

		sepiaToneWithManualManipsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 2;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});
		
		sepiaToneWithManualManipsAndSMPButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 3;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});
		
		sepiaToneWithNe10Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 4;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});

		sepiaToneWithNe10AndSMPButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 5;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});
		
		sepiaToneWithNeonButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 6;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});
		
		sepiaToneWithNeonAndSMPButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v){
				effectNo = 7;

				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Normally you would populate this with your custom intent.

				ContentValues values = new ContentValues();
				values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
				imageUri = getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

				startActivityForResult(cameraIntent, PICTURE_ACTIVITY); // This will cause the onActivityResult event to fire once it's done

			}
		});
		
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog msgDialog;
				
				final ImageView imageView = (ImageView)findViewById(id.imageView1);
				final BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
	            Bitmap x = bitmapDrawable.getBitmap();
				
				ContentValues values = new ContentValues();
				//values.put(Images.Media.TITLE, "title");
				//values.put(Images.Media.BUCKET_ID, "NeSnap");
				//values.put(Images.Media.DESCRIPTION, "NeSnap pictures saved");
				values.put(Images.Media.MIME_TYPE, "image/png");
				Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				Log.i("Captain's Log", "Uri to save ->" + uri);
				OutputStream outstream;
				try {
					outstream = getContentResolver().openOutputStream(uri);

					x.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
					outstream.close();
					msgDialog = createAlertDialog(":)", "The image was saved to " + uri, "OK!");
				} catch (Exception e) {
					msgDialog = createAlertDialog(":(", "The image couldn't be saved...", "OK!");
					Log.i("Captain's Log", "Problem saving image");
					//
				}
				
				msgDialog.show();
			}
		});


		String s = stringFromJNI();
		Log.d("Captain's Log", s);
	}


	@Override
	protected void onDestroy(){
		super.onDestroy();
		//cleanup after yourself by removing all those resources placed on the SD
		/*
		File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"hatResources");
		DeleteRecursive(directory);

		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"moeResources");
		DeleteRecursive(directory);

		directory = new File(Environment.getExternalStorageDirectory()+File.separator+"haarCascadeClassifiers");
		DeleteRecursive(directory);
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		AlertDialog msgDialog;

		/*
			This is where you would trap the requestCode (in this case PICTURE_ACTIVITY).  Seeing as how this is the ONLY 
			Activity that we are calling from THIS activity, it's kind of a moot point.  If you had more than one activity that
			you were calling for results, you would need to throw a switch statement in here or a bunch of if-then-else
			constructs.  Whatever floats your boat.
		 */

		if (requestCode == PICTURE_ACTIVITY) { 
			if(resultCode == RESULT_OK){

				//initialise the imageview
				this.imageView = (ImageView)this.findViewById(R.id.imageView1);

				//load the image from camera and set it as the imageview
				try{
					/*
					 //this is the old way but often the intent returns a NPE... bummer right?

					 if(intent != null){
						//Bitmap photo = Media.getBitmap(this.getContentResolver(), intent.getData());	
						Bitmap photo =  (Bitmap) getIntent().getExtras().get("data");
						imageView.setImageBitmap(photo);
						photo.recycle();
					}
					else{
						Log.e("Captains Log", "The intent is nul");
					}

					else{
					 */						

					//try to collect the image the sophisticated way
					Bitmap photo;	
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inTempStorage = new byte[16*1024];
					options.inSampleSize = 2;

					Cursor cursor = MediaStore.Images.Media.query(this.getContentResolver(), imageUri, null);

					if( cursor != null && cursor.getCount() > 0 ) {
						cursor.moveToFirst();
						String path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );


						photo = BitmapFactory.decodeFile(path, options);


						//now we have the image rotate accordingly
						//figure out which camera was used depending on the image dimensions

						Log.i("Captain's Log", "Camera uses  width -> "+ photo.getWidth() +" height -> " + photo.getHeight());


						int width = photo.getWidth();
						int height = photo.getHeight();

						boolean frontCamera;

						//this is the image case when 
						//	options.inSampleSize = 6;
						//							backCameraPhotoHeight = 612;
						//							backCameraPhotoWidth = 816;
						//							frontCameraPhotoHeight = 240;
						//							frontCameraPhotoWidth = 320;
						//							
						//back crappy from camera for the Samsung Galaxy S III
						if(width == frontCameraPhotoWidth && height == frontCameraPhotoHeight){ //its the front faceing camera
							frontCamera = true;	
						}
						//							else if(width == backCameraPhotoWidth && height == backCameraPhotoHeight){
						//								frontCamera = false;
						//							}
						else{
							frontCamera = false;
							// quit early as this clearly isn't on the Samsung Galaxy S III
							//Log.e("Captain's Log", "The test device isn't a Samsung Galaxy S III");
							//return;
						}


						int rotationInDegrees = 0;

						//set the rotation aspect according to which camera is used :)
						if (frontCamera){
							rotationInDegrees = 270;
							Log.i("Captain's Log", "Front Camera Was Used -> rotation "+ rotationInDegrees +" degrees ");

						}
						else {
							rotationInDegrees = 90;
							Log.i("Captain's Log", "Back Camera Was Used -> rotation "+ rotationInDegrees +" degrees ");

						}

						// createa matrix for the manipulation
						Matrix matrix = new Matrix();
						// resize the bit map
						//matrix.postScale(scaleWidth, scaleHeight);
						// rotate the Bitmap
						matrix.postRotate(rotationInDegrees);

						// recreate the new Bitmap
						Bitmap resizedBitmap = Bitmap.createBitmap(photo, 0, 0,
								photo.getWidth(), photo.getHeight(), matrix, true);

						// make a Drawable from Bitmap to allow to set the BitMap
						// to the ImageView, ImageButton or what ever


						//imageView.setImageBitmap(resizedBitmap);
						//return;

						//photo is passed as the image to filter, so set it
						photo = resizedBitmap;

						//and clear the temp variable resizedBitmap
						resizedBitmap = null;
						System.gc();

					}
					else{
						//if we can't save collect the image the sophisticated way the old school way; if that fails so be it
						Log.v("Captain's Log", "The old photo capture is being used");
						photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					}

					//establish the parameters of the image and allocate space for it
					int w = photo.getWidth();
					int h = photo.getHeight();
					int[] data = new int[w * h];

					//set the data with the pixels from the photo
					photo.getPixels(data, 0, w, 0, 0, w, h);

					//pass the pixels to OpenCV for later processing
					boolean didSet = this.setSourceImage(data, w, h);

					Log.i("Captain's Log", "Image Passed into the NDK");

					if(didSet){
						float runTime = 0;
						boolean benchmarking = false;
						if(benchmarking){
							//process the data
							//this.doGrayscaleTransform();
							float [] runtimes = new float[100];
							for (int i = 0; i < 100; i++){
								this.setSourceImage(data, w, h);
								float startnow = android.os.SystemClock.uptimeMillis();

								

								switch(effectNo){
								case 1:
									runTime = this.STWithOpenCV();
									break;
								case 2:
									runTime = this.STWithManualManips();
									break;
								case 3:
									runTime = this.STWithManualManipsAndSMP();
									break;
								case 4:
									runTime = this.STWithNeTen();
									break;
									
								case 5:
									runTime = this.STWithNeTenAndSMP();
									break;
								case 6:
									runTime = this.STWithNeon();
									break;
								case 7:
									runTime = this.STWithNeonAndSMP();
									break;
								}

								float endnow = android.os.SystemClock.uptimeMillis();
								Log.d("MYTAG", "Excution time: "+(endnow-startnow)+" ms");
								runtimes[i] = endnow-startnow;

								
							}

							String file = "/runtimes.txt";

							//write it to file
							try {

								File root = Environment.getExternalStorageDirectory();

								String localFilePath = root.getPath() + file;



								FileOutputStream fos = new FileOutputStream(localFilePath, false);

								fos.write("Run\tMethod\tMilliseconds\n".getBytes());

								for(int i = 0; i < 100; i++){
									fos.write(((i+1)+"\t"+ 1 +"\t"+ runtimes[i] +"\n").getBytes());
								}

								fos.close();

							} catch (Exception e) {

								e.printStackTrace();

							}

						}else{
							//we don't need 100 iterations of the same thing, so we test it once
							float startnow = android.os.SystemClock.uptimeMillis();

							switch(effectNo){
							case 1:
								runTime = this.STWithOpenCV();
								break;
							case 2:
								runTime = this.STWithManualManips();
								break;
							case 3:
								runTime = this.STWithManualManipsAndSMP();
								break;
							case 4:
								runTime = this.STWithNeTen();
								break;
								
							case 5:
								runTime = this.STWithNeTenAndSMP();
								break;
							case 6:
								runTime = this.STWithNeon();
								break;
							case 7:
								runTime = this.STWithNeonAndSMP();
								break;
							}

							float endnow = android.os.SystemClock.uptimeMillis();
							Log.d("MYTAG", "Execution time: "+(endnow-startnow)+" ms");

						}	




						//collect the data back from openCV
						Log.i("Captain's Log", "setting image was successful");
						byte[] resultData = this.getSourceImage();

						//process the OpenCV returned data back into a usable bitmap and display it
						Bitmap resultPhoto = BitmapFactory.decodeByteArray(resultData, 0, resultData.length);

						runtimeView.setText("Runtime (Sec) =" + Float.toString(runTime));
						imageView.setImageBitmap(resultPhoto);
						
					}else{
						//could notify the user that opencv has a problem
						Log.i("Captain's Log", "setting image was not very successful... thanks OpenCV");
						msgDialog = createAlertDialog(":(", "You should contact the developer... OpenCV Failed epically!", "OK!");
					}



					//					}
				}catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}

			}

			if (resultCode == RESULT_CANCELED) { // The user didn't like the photo.  ;_;
				msgDialog = createAlertDialog(":)", "You hit the cancel button... why not try again later!", "OK!");
			}

		}



		msgDialog = createAlertDialog(":)", "The Photo you have taken has been filtered", "Ok!");


		msgDialog.show();
	}


	private void writeInputStreamToFile(InputStream is, String file){
		try {

			//InputStream is = null; // Your stream here (myInputStream)



			File root = Environment.getExternalStorageDirectory();

			String localFilePath = root.getPath() + file;



			FileOutputStream fos = new FileOutputStream(localFilePath, false);




			byte[] buffer = new byte[1024];

			int byteRead = 0;



			while ((byteRead = is.read(buffer)) != -1) {

				fos.write(buffer, 0, byteRead);

			}

			fos.close();

		} catch (Exception e) {

			e.printStackTrace();

		}


	}

	@SuppressWarnings("deprecation")
	private AlertDialog createAlertDialog(String title, String msg, String buttonText){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		AlertDialog msgDialog = dialogBuilder.create();
		msgDialog.setTitle(title);
		msgDialog.setMessage(msg);
		msgDialog.setButton(buttonText, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int idx){
				return; // Nothing to see here...
			}
		});

		return msgDialog;
	}

	private void WorkoutCameraResolutions(){

		Log.i("Captain's Log", "Determining the resolutions of both cameras");

		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();

		Log.i("Captain's Log", "There are " + cameraCount + " cameras");

		for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
			Camera.getCameraInfo( camIdx, cameraInfo );
			//if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
			try {
				cam = Camera.open( camIdx );
				Log.i("Captain's Log", "camera opened");
				if(cam!=null){
					try{

						//	                    	List<Camera.Size> listSupportedPictureSizes = cam.getParameters().getSupportedPictureSizes();
						//
						//	                    	for (int i=0; i < listSupportedPictureSizes.size(); i++){
						//
						//	                            String str = String.valueOf(i) + " : " 
						//	                               + String.valueOf(listSupportedPictureSizes.get(i).height)
						//	                               + " x "
						//	                               + String.valueOf(listSupportedPictureSizes.get(i).width);
						//	                            Log.i("Captain's Log", str);
						//	                    	}

						int thisWidth = cam.getParameters().getPictureSize().width;
						int thisHeight = cam.getParameters().getPictureSize().height;

						if(thisWidth < frontCameraPhotoWidth){
							frontCameraPhotoWidth = thisWidth;
						}
						if(thisWidth > backCameraPhotoWidth){
							backCameraPhotoWidth = thisWidth;
						}

						if(thisHeight < frontCameraPhotoHeight){
							frontCameraPhotoHeight = thisHeight;
						}
						if(thisHeight > backCameraPhotoHeight){
							backCameraPhotoHeight = thisHeight;
						}


						Log.i("Captain's Log", "photo taken!");
					}catch (Exception e) {
						// TODO: handle exception
					}   
					finally{
						cam.release();
					}   

				}else{
					Log.e("Captain's Log", "Workout Camera Resolutions Failed :(");
					//booo, failed!
				}



			} catch (RuntimeException e) {
				Log.e("Captain's Log", "Camera failed to open: " + e.getLocalizedMessage());
			}
		}
		//}

	}


	private void LogCameraResolutions(){
		Log.i("Captain's Log", "The Back Camera has photo width -> " + backCameraPhotoWidth + " height -> " + backCameraPhotoHeight);
		Log.i("Captain's Log", "The Front Camera has photo width -> " + frontCameraPhotoWidth + " height -> " + frontCameraPhotoHeight);
	}


	void DeleteRecursive(File dir)
	{
		Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) 
			{
				File temp =  new File(dir, children[i]);
				if(temp.isDirectory())
				{
					Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
					DeleteRecursive(temp);
				}
				else
				{
					Log.d("DeleteRecursive", "Delete File" + temp.getPath());
					boolean b = temp.delete();
					if(b == false)
					{
						Log.d("DeleteRecursive", "DELETE FAIL");
					}
				}
			}

			dir.delete();
		}
	}

}