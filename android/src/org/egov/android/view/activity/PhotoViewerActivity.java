/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.egov.android.AndroidLibrary;
import org.egov.android.R;
import org.egov.android.api.SSLTrustManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PhotoViewerActivity extends FragmentActivity {
	private static final String TAG = PhotoViewerActivity.class.getName();

    private ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    private ViewPager viewPager;
    private static String complaintId="";
    private static String path = "";
    public static ArrayList<String> imageName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        path = getIntent().getExtras().getString("path");
        complaintId=getIntent().getExtras().getString("complaintId");
        _getComplaintImages();
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
        viewPager.setCurrentItem(getIntent().getExtras().getInt("imageId"));
    }

    private void _getComplaintImages() {
        File folder = new File(path);
        if (!folder.exists()) {
            return;
        }
        imageName = new ArrayList<String>();
        File[] listOfFiles = folder.listFiles();
        
        Arrays.sort(listOfFiles, new Comparator<File>()
        {
			@Override
			public int compare(File arg0, File arg1) {
				// TODO Auto-generated method stub
				return arg0.getName().compareTo(arg1.getName());
			}
	    });
        
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (!listOfFiles[i].getName().contains("photo_temp_user") && !listOfFiles[i].getName().startsWith("photo_")) {
                  imageName.add(path + File.separator + listOfFiles[i].getName());
                }
            }
        }
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return imageName.size();
        }

        @Override
        public Fragment getItem(int position) {
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.photo_swipe_fragment, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            ProgressBar imgProgress=(ProgressBar) swipeView.findViewById(R.id.imgprogressbar);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            if(new File(path + File.separator + "photo_" + (position+1) +".jpg").exists())
        	{
            	imageView.setImageBitmap(_getBitmapImage(path + File.separator + "photo_"+ (position+1) +".jpg"));
            	imgProgress.setVisibility(View.GONE);
        	}
            else
            {
                imageView.setImageBitmap(_getBitmapImage(imageName.get(position)));
                new ImageDownloaderTask(imageView, imgProgress).execute(getComplaintImageURL(complaintId, (position+1)), "GET", path+ File.separator + "photo_"+ (position+1) +".jpg");
            }
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }

        private Bitmap _getBitmapImage(String path) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            return BitmapFactory.decodeFile(path, options);
        }
        
        /**
         * This function used to get thumb image url with parameters 
         */
        private String getComplaintImageURL(String complaintId,int fileNo)
        {
        	String thumbImgURL=null;
        	List<NameValuePair> params = new LinkedList<NameValuePair>();
        	
        	try {

        		String accessToken = AndroidLibrary.getInstance().getSession()
                        .getString("access_token", "");
                	
                thumbImgURL = AndroidLibrary.getInstance().getConfig().getString("api.baseUrl")
                            + "/api/v1.0/complaint/" + complaintId
                            + "/downloadSupportDocument";
                if(!thumbImgURL.endsWith("?")){
                		thumbImgURL += "?";
                }
                
                params.add(new BasicNameValuePair("fileNo", String.valueOf(fileNo)));
                params.add(new BasicNameValuePair("type", "complaint"));
                params.add(new BasicNameValuePair("access_token", accessToken));
                String paramString = URLEncodedUtils.format(params, "utf-8");
                thumbImgURL += paramString;
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        	
        	return thumbImgURL;
        }

        
        class ImageDownloaderTask extends AsyncTask<String, Void, String> {
            private final WeakReference<ImageView> imageViewReference;
            private ProgressBar imgdownloadprogress;

            public ImageDownloaderTask(ImageView imageView, ProgressBar imgdownloadprogress) {
                imageViewReference = new WeakReference<ImageView>(imageView);
                this.imgdownloadprogress=imgdownloadprogress;
            }

            @Override
            protected String doInBackground(String... params) {
                downloadBitmap(params[0], params[1], params[2]);
                return params[2];
            }

            @Override
            protected void onPostExecute(String downloadedImagePath) {
                if (isCancelled()) {
                	downloadedImagePath = null;
                }

                if (imageViewReference != null) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(_getBitmapImage(downloadedImagePath));
                    }
                }
                imgdownloadprogress.setVisibility(View.GONE);
            }
            
            private Bitmap downloadBitmap(String url, String requestMethod, String filePath) {
            	HttpURLConnection con = null;
                try {
                	/* Protocal Switch Condition Whether sending https request or http request */
        			if (url.startsWith("https://")) {
        			   new SSLTrustManager();
        			   con = (HttpsURLConnection) new URL(url).openConnection();
        			}
        			else
        			{
        			   con = (HttpURLConnection) new URL(url).openConnection();
        			}
                    con.setRequestMethod(requestMethod);
                    con.setUseCaches(false);
                    con.setDoInput(true);

                    InputStream inputStream = con.getInputStream();
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        saveImage(bitmap, filePath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w(TAG, "Error downloading image from " + url);
                } finally {
                    con.disconnect();
                }
                return null;
            }
            
            
            private void saveImage(Bitmap image, String filePath) {
                File pictureFile = new File(filePath);
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }  
            }
        }
        
    }
    
    
    
}
