package com.example.helloworld_webservicewithksoap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	TextView tvMessage;
	final String METHOD_HELLO_WORLD = "HelloWorld";	
//	final String METHOD_CheckInLockPark = "checkin_lockpark";
	
	//服务器链接
	final String WEB_SERVICE_URL = "http://120.24.254.42:802/OperData.asmx";
    private static final String HelloWorld_SOAP_ACTION = "http://tempuri.org/HelloWorld";//不要忘记了
	final String Namespace = "http://tempuri.org";//命名空间
    
	Button button1;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initBtn();
		initTv();
		
	}
	
	private void  initTv(){
		
		tvMessage = (TextView) this.findViewById(R.id.tvMessage);
		
	}

	private void initBtn(){
		
		button1 = (Button)findViewById(R.id.helloworldbutton);
		button1.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
				Map<String, String> values = new HashMap<String, String>();
				values.put("msg", "这是Android手机发出的信息");
				Request(METHOD_HELLO_WORLD);
				
			}
			
		});
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}

	/**
	 * 调用WebService
	 * 
	 * @return WebService的返回值
	 * 
	 */
	public String CallWebService(String MethodName, Map<String, String> Params) {
		
		// 1、指定webservice的命名空间和调用的方法名
		SoapObject request = new SoapObject(Namespace, MethodName);
		
		// 2、设置调用方法的参数值，如果没有参数，可以省略，
		if (Params != null) {
			
			Iterator iter = Params.entrySet().iterator();
			
			while ( iter.hasNext() ) {
				
				Map.Entry entry = (Map.Entry) iter.next();
				request.addProperty( (String) entry.getKey(),
						(String) entry.getValue() );
				
			}
			
		}
		
		// 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);//注意是VER11，而不是VER12
		//envelope.bodyOut = request;
		// c#写的应用程序必须加上这句
		
		envelope.dotNet = true;
		HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);
		
		// 使用call方法调用WebService方法
		
		try {
			
			ht.call( HelloWorld_SOAP_ACTION, envelope); //注意：第一个参数应该是HelloWorld_SOAP_ACTION，不是空
			
		} catch (HttpResponseException e) {
			
			Log.e("----HttpResponseException异常---", e.getMessage());
			e.printStackTrace();
			
			
		} catch (IOException e) {
			
			Log.e("----IOException异常---", e.getMessage());
			e.printStackTrace();
			
		} catch (XmlPullParserException e) {
			
			Log.e("----XmlPullParserException异常---", e.getMessage());
			e.printStackTrace();
			
		}
		
		try {
			
			final SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
			
			if (result != null) {
				
				Log.d("----收到的回复----", result.toString());
				return result.toString();
				
			}
			

		} catch (SoapFault e) {
			
			Log.e("----发生错误---", e.getMessage());
			e.printStackTrace();
			
		}
		return null;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *            方法名+参数列表（哈希表形式）
	 */
	public void Request( Object... params) {
		
		new AsyncTask<Object, Object, String>() {

			@Override
			protected String doInBackground(Object... params) {
				
                System.out.println("Params的长度"+params.length);
                
				if (params != null && params.length == 2) {
					
					return CallWebService( (String) params[0],
							(Map<String, String>) params[1] );
					
				} else if (params != null && params.length == 1) {
					
					return CallWebService((String) params[0], null);
					
				} else {
					
					return null;
					
				}
				
			}

			protected void onPostExecute(String result) {
				
				if (result != null) {
					
					tvMessage.setText("服务器回复的信息 : " + result);
					
				}
				
			};

		}.execute(params);
	
	}
	
}
