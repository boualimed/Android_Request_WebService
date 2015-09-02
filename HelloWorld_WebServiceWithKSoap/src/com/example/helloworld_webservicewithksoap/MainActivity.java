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
	
	//����������
	final String WEB_SERVICE_URL = "http://120.24.254.42:802/OperData.asmx";
    private static final String HelloWorld_SOAP_ACTION = "http://tempuri.org/HelloWorld";//��Ҫ������
	final String Namespace = "http://tempuri.org";//�����ռ�
    
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
				values.put("msg", "����Android�ֻ���������Ϣ");
				Request(METHOD_HELLO_WORLD);
				
			}
			
		});
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}

	/**
	 * ����WebService
	 * 
	 * @return WebService�ķ���ֵ
	 * 
	 */
	public String CallWebService(String MethodName, Map<String, String> Params) {
		
		// 1��ָ��webservice�������ռ�͵��õķ�����
		SoapObject request = new SoapObject(Namespace, MethodName);
		
		// 2�����õ��÷����Ĳ���ֵ�����û�в���������ʡ�ԣ�
		if (Params != null) {
			
			Iterator iter = Params.entrySet().iterator();
			
			while ( iter.hasNext() ) {
				
				Map.Entry entry = (Map.Entry) iter.next();
				request.addProperty( (String) entry.getKey(),
						(String) entry.getValue() );
				
			}
			
		}
		
		// 3�����ɵ���Webservice������SOAP������Ϣ������Ϣ��SoapSerializationEnvelope��������
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);//ע����VER11��������VER12
		//envelope.bodyOut = request;
		// c#д��Ӧ�ó������������
		
		envelope.dotNet = true;
		HttpTransportSE ht = new HttpTransportSE(WEB_SERVICE_URL);
		
		// ʹ��call��������WebService����
		
		try {
			
			ht.call( HelloWorld_SOAP_ACTION, envelope); //ע�⣺��һ������Ӧ����HelloWorld_SOAP_ACTION�����ǿ�
			
		} catch (HttpResponseException e) {
			
			Log.e("----HttpResponseException�쳣---", e.getMessage());
			e.printStackTrace();
			
			
		} catch (IOException e) {
			
			Log.e("----IOException�쳣---", e.getMessage());
			e.printStackTrace();
			
		} catch (XmlPullParserException e) {
			
			Log.e("----XmlPullParserException�쳣---", e.getMessage());
			e.printStackTrace();
			
		}
		
		try {
			
			final SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
			
			if (result != null) {
				
				Log.d("----�յ��Ļظ�----", result.toString());
				return result.toString();
				
			}
			

		} catch (SoapFault e) {
			
			Log.e("----��������---", e.getMessage());
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
	 * ִ���첽����
	 * 
	 * @param params
	 *            ������+�����б���ϣ����ʽ��
	 */
	public void Request( Object... params) {
		
		new AsyncTask<Object, Object, String>() {

			@Override
			protected String doInBackground(Object... params) {
				
                System.out.println("Params�ĳ���"+params.length);
                
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
					
					tvMessage.setText("�������ظ�����Ϣ : " + result);
					
				}
				
			};

		}.execute(params);
	
	}
	
}
