package com.oyedelhi.oyedelhi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import static android.R.attr.name;
import static com.oyedelhi.oyedelhi.R.id.etUsername_Register;
import static com.oyedelhi.oyedelhi.R.id.etage_Register;
import static com.oyedelhi.oyedelhi.R.id.etcity;
import static com.oyedelhi.oyedelhi.R.id.etemail_Register;

public class MainActivity extends AppCompatActivity {


    ImageView image ;
    ActionBar actionBar;
    Toolbar toolbar;

    TextView toolbar_title;
    private Bitmap bitmap;
    private String UPLOAD_URL = "";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "filename";
    private String KEY_UID = "uid";
    private String KEY_FORMAT = "format";

    public static String json_string;
    private int PICK_IMAGE_REQUEST = 1;


    // Global declaration of items so that they can be used in any function
    EditText etage_Register, etUsername_Register , etemail_Register , etcity;
    Button btnRegisterNow ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        toolbar = (Toolbar) findViewById(R.id.edit_details_toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        toolbar_title.setText("Resume");

        image=(ImageView)findViewById(R.id.imageButton);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        // maping to views
        etage_Register = (EditText) findViewById(R.id.etage_Register);
        etUsername_Register = (EditText) findViewById(R.id.etUsername_Register);
        etemail_Register = (EditText) findViewById(R.id.etemail_Register);
        etcity = (EditText) findViewById(R.id.etcity);
        btnRegisterNow = (Button) findViewById(R.id.btnRegisterNow);

        btnRegisterNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                execute();

            }
        });


    }

    private void execute() {

        // String name = editext.getText()

        String name = etUsername_Register.getText().toString();
        String age= etage_Register.getText().toString();
        String username = etemail_Register.getText().toString();
        String password = etcity.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                        MainActivity.this.startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Register Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RegisterRequest registerRequest = new RegisterRequest(name, age, username, password, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        registerRequest.setRetryPolicy(new DefaultRetryPolicy(0 , DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(registerRequest);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                image.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = new ProgressDialog(getApplicationContext(), R.style.TransparentProgressDialog);
        //progDailog.setMessage("Loading...");
        loading.show();
        loading.setIndeterminate(false);
        loading.setProgressStyle(R.style.TransparentProgressDialog);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View vd = inflater.inflate(R.layout.custom_progress_dialog, null);
        loading.setContentView(vd);
        loading.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String

                String image=null;
                try{
                    image= getStringImage(bitmap);
                }catch (OutOfMemoryError ome) {
                    Toast.makeText(getApplicationContext(), "Cannot Upload more than 200KB", Toast.LENGTH_SHORT).show();
                }

                //Getting Image Name

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_UID, "2");
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, "profilepic");
                params.put(KEY_FORMAT, "jpeg");

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext().getApplicationContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                e.printStackTrace();
            }
            return mIcon11;
        }




    public boolean checkJsonNull(JSONObject jsonObject1,String hasProperty)
    {
        return  jsonObject1.has(hasProperty);

    }

}
