package com.tg.google.map;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class FragmentProfileImageUpload extends FragmentCustom{
	private ImageView image;
	 private Button uploadButton;
	 private Bitmap bitmap;
	 private Button selectImageButton;
	
	// number of images to select
	 private static final int PICK_IMAGE = 1;
	

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
           fragmentCallback = (OnFragmentStatusListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoginStatusListener");
        }
    }
 
    
    
	 @Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
						
		}
	
	@Override
	 public void onViewCreated(View view, Bundle savedInstanceState) {
	     super.onViewCreated(view, savedInstanceState);
	     
	  // find the views
	     image = (ImageView) view.findViewById(R.id.uploadImage);
	     uploadButton = (Button) view.findViewById(R.id.uploadButton);

	     // on click select an image
	     selectImageButton = (Button) view.findViewById(R.id.selectImageButton);
	     selectImageButton.setOnClickListener(new OnClickListener() {

	      @Override
	      public void onClick(View v) {
	       selectImageFromGallery();

	      }
	     });

	     // when uploadButton is clicked
	     uploadButton.setOnClickListener(new OnClickListener() {

	      @Override
	      public void onClick(View v) {
	       new ImageUploadTask().execute();
	      }
	     });
	     
	     
	     

	 }
	
	/**
	  * Opens dialog picker, so the user can select image from the gallery. The
	  * result is returned in the method <code>onActivityResult()</code>
	  */
	 public void selectImageFromGallery() {
	  Intent intent = new Intent();
	  intent.setType("image/*");
	  intent.setAction(Intent.ACTION_GET_CONTENT);
	  startActivityForResult(Intent.createChooser(intent, "Select Picture"),
	    PICK_IMAGE);
	 }
	
	
	 
	 /**
	  * Retrives the result returned from selecting image, by invoking the method
	  * <code>selectImageFromGallery()</code>
	  */
	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 
	  super.onActivityResult(requestCode, resultCode, data);

	  if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK
	    && null != data) {
		  
		 
	   Uri selectedImage = data.getData();
	   Log.d("selectedImage", selectedImage.getPath());
	   String filepath = new File(selectedImage.getPath()).getAbsolutePath();
	   Log.d("filepath", filepath);
	   
	   String[] filePathColumn = { MediaStore.Images.Media.DATA };
	   Log.d("filePathColumn", filePathColumn.length + "");
	   
	   Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(selectedImage,
	     filePathColumn, null, null, null);
	   cursor.moveToFirst();

	   int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	   Log.d("columnIndex", columnIndex + "");
	   String picturePath = cursor.getString(columnIndex);
	   Log.d("picturePath", picturePath + "");
	   cursor.close();

	   decodeFile(selectedImage.getPath());

	  }
	 }
	 
	 /**
	  * The method decodes the image file to avoid out of memory issues. Sets the
	  * selected image in to the ImageView.
	  * 
	  * @param filePath
	  */
	 public void decodeFile(String filePath) {
		
	  // Decode image size
	  BitmapFactory.Options o = new BitmapFactory.Options();
	  o.inJustDecodeBounds = true;
	  BitmapFactory.decodeFile(filePath, o);

	  // The new size we want to scale to
	  final int REQUIRED_SIZE = 1024;

	  // Find the correct scale value. It should be the power of 2.
	  int width_tmp = o.outWidth, height_tmp = o.outHeight;
	  int scale = 1;
	  while (true) {
	   if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
	    break;
	   width_tmp /= 2;
	   height_tmp /= 2;
	   scale *= 2;
	  }

	  // Decode with inSampleSize
	  BitmapFactory.Options o2 = new BitmapFactory.Options();
	  o2.inSampleSize = scale;
	  bitmap = BitmapFactory.decodeFile(filePath, o2);

	  image.setImageBitmap(bitmap);
	 }

	 /**
	  * The class connects with server and uploads the photo
	  * 
	  * 
	  */
	 class ImageUploadTask extends AsyncTask<Void, Void, String> {
	
	  // private ProgressDialog dialog;
	  private ProgressDialog dialog = new ProgressDialog(getActivity());

	  @Override
	  protected void onPreExecute() {
	   dialog.setMessage("Uploading...");
	   dialog.show();
	  }

	  @Override
	  protected String doInBackground(Void... params) {
	  
		        try
		        {
		            URL url = new URL("http://myserver/myapp/upload-image");
		            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		            conn.setRequestMethod("POST");

		            conn.setDoInput(true);
		            conn.setDoOutput(true);

		            conn.setRequestProperty("Connection", "Keep-Alive");
		            conn.setRequestProperty("Cache-Control", "no-cache");

		            conn.setReadTimeout(35000);
		            conn.setConnectTimeout(35000);

		            // directly let .compress write binary image data
		            // to the output-stream
		            OutputStream os = conn.getOutputStream();
		            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		            os.flush();
		            os.close();

		            System.out.println("Response Code: " + conn.getResponseCode());

		            InputStream in = new BufferedInputStream(conn.getInputStream());
		            Log.d("sdfs", "sfsd");
		            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
		            String line = "";
		            StringBuilder stringBuilder = new StringBuilder();
		            while ((line = responseStreamReader.readLine()) != null)
		                stringBuilder.append(line).append("\n");
		            responseStreamReader.close();

		            String response = stringBuilder.toString();
		            System.out.println(response);

		            conn.disconnect();
		            
		            return response;
		        }
		        catch(MalformedURLException e) {
		            e.printStackTrace();
		        }
		        catch(IOException e) {
		            e.printStackTrace();
		        }
	   return null;
	  }

	  @Override
	  protected void onPostExecute(String result) {
	   dialog.dismiss();
	   Toast.makeText(getActivity(), "file uploaded",
	     Toast.LENGTH_LONG).show();
	  }

	 }
	 
	
	 @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, 
       Bundle savedInstanceState) {

       // If activity recreated (such as from screen rotate), restore
       // the previous article selection set by onSaveInstanceState().
       // This is primarily necessary when in the two-pane layout.
       if (savedInstanceState != null) {
           //mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
       }
       
       View pageview = inflater.inflate(R.layout.profile_upload_page, container, false);
       

       // Inflate the layout for this fragment
       return pageview;
   }

   @Override
   public void onStart() {
       super.onStart();

       // During startup, check if there are arguments passed to the fragment.
       // onStart is a good place to do this because the layout has already been
       // applied to the fragment at this point so we can safely call the method
       // below that sets the article text.
       Bundle args = getArguments();
       /*if (args != null) {
           // Set article based on argument passed in
           updateArticleView(args.getInt(ARG_POSITION));
       } else if (mCurrentPosition != -1) {
           // Set article based on saved instance state defined during onCreateView
           updateArticleView(mCurrentPosition);
       }*/
   }
}
