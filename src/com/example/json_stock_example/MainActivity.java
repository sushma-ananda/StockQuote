package com.example.json_stock_example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	String stock_symbol ="";
	EditText editTextStockSymbol;
	Button getQuotes;
	TextView stockSymbol, stockName, stockPrice;
	static String name, price, symbol;
	int buttonClicked=0;
	Thread t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getQuotes = (Button) findViewById(R.id.get_Quotes);
		editTextStockSymbol = (EditText) findViewById(R.id.editText_StockSymbol);
		
		final Handler showContent = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				//Load stock quotes
				String[] strarr = (String[]) msg.obj;
				stockName = (TextView) findViewById(R.id.stock_Name);
				stockSymbol = (TextView) findViewById(R.id.stock_symbol);
				stockPrice = (TextView) findViewById(R.id.stock_Price);
				stockSymbol.setText("Stock symbol: "+strarr[1]);
				stockPrice.setText("Stock Price is: "+strarr[2]);
				stockName.setText("Stock Name: "+strarr[0]);
				Log.e(INPUT_SERVICE, msg.obj.toString());
				return false;
			}
		});
		

		getQuotes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String stockSym = editTextStockSymbol.getText().toString();
				buttonClicked++;
				if(isNetworkActive())
				{
					
						t = new Thread() {
							@Override
							public void run()
							{
								while(true)
								{
									String[] strarr;
									if(!symbol.equalsIgnoreCase(editTextStockSymbol.getText().toString()))
									{
										
										symbol = "";
										name="";
										price="";
										
									}
									strarr = stockJSon(stockSym);	
									Message msg = Message.obtain();
									msg.obj = strarr;
									
									showContent.sendMessage(msg);
								}
							}
						};
						//new MyAsyncTask().execute();
						
						t.start();
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
						/*
						if(t != null)
						{   //check if thread is new or already running
							if(t.isAlive() && t.getState() == Thread.State.NEW)
							{
								Log.i("Thread State", "Thread alive and running");
								if(symbol.equalsIgnoreCase(editTextStockSymbol.getText().toString()))
								{
									//t.setName(symbol);
									t.start();
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								else
								{
									symbol = editTextStockSymbol.getText().toString();
									//t.setName(symbol);
									t.start();
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}*/
							/*else
							{
								symbol = editTextStockSymbol.getText().toString();
								
								t.run();
								try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							}
*/
			//}
					
					}
			}
		});
			
	}
	
	public String[] stockJSon(String stocksym)
	{
		InputStream inputStream = null;
		String result = "";
		
		String url_selected = "http://finance.yahoo.com/webservice/v1/symbols/"+stocksym+"/quote?format=json";
		ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
		//InputStream inputStream = null;
		try
		{
			HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());				
			HttpGet httpGet = new HttpGet(url_selected);
			httpGet.setHeader("Content-type", "application/json");				
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			
			inputStream = httpEntity.getContent();
			Log.v("test", httpEntity.getContent().toString());
		}catch (UnsupportedEncodingException e1)
		{
			Log.e("UnsupportedEncodingException",e1.toString());
			e1.printStackTrace();
		}
		catch(ClientProtocolException e2)
		{
			Log.e("ClientProtocolException",e2.toString());
			e2.printStackTrace();
		}
		catch(IllegalStateException e3)
		{
			Log.e("IllegalStateException",e3.toString());
			e3.printStackTrace();
		}
		catch(IOException e4)
		{
			Log.e("IOException",e4.toString());
			e4.printStackTrace();
		}
		
		try
		{
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder builderString = new StringBuilder();
			String line = null;
			while((line=reader.readLine()) != null)
			{
				builderString.append(line + "\n");
			}
			inputStream.close();
			result = builderString.toString();
			
		}catch(Exception e){
			Log.e("StringBuilding and BufferedReader", "Error converting" + e.toString());
		}
		try
		{	
			JSONObject jsonObject = new JSONObject(result);
			JSONObject listObject = jsonObject.getJSONObject("list");
			JSONArray resourcesObject = listObject.getJSONArray("resources");
			JSONObject resObject = resourcesObject.getJSONObject(0);
			JSONObject resource = resObject.getJSONObject("resource");
			JSONObject fieldsObject = resource.getJSONObject("fields");
			name = fieldsObject.getString("name");
			symbol = fieldsObject.getString("symbol");
			price = fieldsObject.getString("price");
			//volume = classObject.getString("volume");
		}catch(JSONException e)
		{
			e.printStackTrace();
		}
		String[] strarr ={ name, symbol, price};
		return strarr;
	}

private class MyAsyncTask extends AsyncTask<String, String, String> {
		
		InputStream inputStream = null;
		String result = "";
		

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url_selected = "http://finance.yahoo.com/webservice/v1/symbols/"+editTextStockSymbol.getText().toString()+"/quote?format=json";
			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			InputStream inputStream = null;
			try
			{
				HttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());				
				HttpGet httpGet = new HttpGet(url_selected);
				httpGet.setHeader("Content-type", "application/json");				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				
				inputStream = httpEntity.getContent();
				Log.v("test", httpEntity.getContent().toString());
			}catch (UnsupportedEncodingException e1)
			{
				Log.e("UnsupportedEncodingException",e1.toString());
				e1.printStackTrace();
			}
			catch(ClientProtocolException e2)
			{
				Log.e("ClientProtocolException",e2.toString());
				e2.printStackTrace();
			}
			catch(IllegalStateException e3)
			{
				Log.e("IllegalStateException",e3.toString());
				e3.printStackTrace();
			}
			catch(IOException e4)
			{
				Log.e("IOException",e4.toString());
				e4.printStackTrace();
			}
			
			try
			{
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder builderString = new StringBuilder();
				String line = null;
				while((line=reader.readLine()) != null)
				{
					builderString.append(line + "\n");
				}
				inputStream.close();
				result = builderString.toString();
				
			}catch(Exception e){
				Log.e("StringBuilding and BufferedReader", "Error converting" + e.toString());
			}
			try
			{	
				JSONObject jsonObject = new JSONObject(result);
				JSONObject listObject = jsonObject.getJSONObject("list");
				JSONArray resourcesObject = listObject.getJSONArray("resources");
				JSONObject resObject = resourcesObject.getJSONObject(0);
				JSONObject resource = resObject.getJSONObject("resource");
				JSONObject fieldsObject = resource.getJSONObject("fields");
				name = fieldsObject.getString("name");
				symbol = fieldsObject.getString("symbol");
				price = fieldsObject.getString("price");
				//volume = classObject.getString("volume");
			}catch(JSONException e)
			{
				e.printStackTrace();
			}
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			stockName = (TextView) findViewById(R.id.stock_Name);
			stockSymbol = (TextView) findViewById(R.id.stock_symbol);
			stockPrice = (TextView) findViewById(R.id.stock_Price);
			stockSymbol.setText("Stock symbol: "+symbol);
			stockPrice.setText("Stock Price is: "+price);
			stockName.setText("Stock Name: "+name);
				
			super.onPostExecute(result);
		}

    }	
		

//Check Network connectivity
	public boolean isNetworkActive()
	{
		boolean result = false;
		ConnectivityManager conn = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(conn != null)
		{
			NetworkInfo nwinfo = conn.getActiveNetworkInfo();
			if(nwinfo != null && nwinfo.isConnected())
			{
				result = true;
			}
			else
			{
				result = false;
			}
		}
		return result;
	}
}
