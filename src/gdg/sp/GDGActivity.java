package gdg.sp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

public class GDGActivity extends Activity {

	public static Context context = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gdg);
	}

	public void readQRCode(View view){
		
		GDGActivity.context = view.getContext();
		
		Toast.makeText(view.getContext(), "Lendo QRCode", Toast.LENGTH_LONG).show();
		
		try {

		    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

		    startActivityForResult(intent, 0);

		} catch (Exception e) {

		    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
		    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
		    startActivity(marketIntent);

		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {           
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) {

	        if (resultCode == RESULT_OK) {
	        	
	        	Pattern emailPattern = Patterns.EMAIL_ADDRESS; 
	        	Account[] accounts = AccountManager.get(context).getAccounts();
	        	
	        	String email = ""; 
	        	Account account = accounts[0];
        	    if (emailPattern.matcher(account.name).matches()) {
        	        email = account.name;
        	    }
	        	
        	    String link = data.getStringExtra("SCAN_RESULT");
	            String yourUrl = "http://www.movinon.com.br/gdg.php?email="+email+"&link="+link;
        		new RequestTask().execute(yourUrl);    
	            
	            
	        }
	        
	    }
	}
	
	
}

class RequestTask extends AsyncTask<String, String, String>{

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
        } catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(GDGActivity.context, result, Toast.LENGTH_LONG).show();
    }
    
}