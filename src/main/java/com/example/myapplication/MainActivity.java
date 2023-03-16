package com.example.myapplication;

import static android.util.Base64.encodeToString;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    Button BSelectImage;
    Button reqOCR;
    ImageView IVPreviewImage;
    String encodedImage="";
    String ocrMessage = "";
    String summaryMessage;
    Spinner spinner;
    TextView sizeText;

    TextView allText,dicText,summary;
    LinearLayout layout1;
    LinearLayout layout2;
    LinearLayout layout3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        summary = findViewById(R.id.summary);
        allText = findViewById(R.id.allText);
        dicText = findViewById(R.id.dicText);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        layout3.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        //개발자 정보 버튼 클릭시 액티비티 전환
        Button changeDisplay = (Button) findViewById(R.id.changeDisplay);
        changeDisplay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), NoticeActivity.class);
                startActivity(intent);
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override

            public void run() {
                try {

                    String message = getReqMessage(encodedImage);
                    URL url = new URL("https://4t6hn40hnn.apigw.ntruss.com/custom/v1/20374/bb28d3f6a04ceacadd82be60559a4397240eedd21a2402b988cc9aff41375916/general");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json;UTF-8");
                    connection.setRequestProperty("X-OCR-SECRET", "UVBnRGdRVGZva1h4aU14eWpGanNyTGtBSG5pSGFmS2k=");
                    System.out.println("connection:"+connection);
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(message);
                    wr.flush();
                    wr.close();

                    int responseCode = connection.getResponseCode();
                    System.out.println(responseCode);
                    if (responseCode == 200) {
                        System.out.println(connection.getResponseMessage());

                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        connection.getInputStream()));
                        String decodedString;
                        while ((decodedString = in.readLine()) != null) {
                            ocrMessage = decodedString;
                        }
                        in.close();
                        connection.disconnect();
                    } else {  // 에러 발생
                        ocrMessage = connection.getResponseMessage();
                    }
                } catch (Exception e) {
                    System.out.println("------" + e);
                }

                System.out.println(">>>>>>>>>>" + ocrMessage);
                JSONObject ocrArray;
                JSONArray imageArray;
                JSONObject fieldsObject;
                JSONArray fieldsArray = null;
                try {
                    ocrArray = new JSONObject(ocrMessage);
                    imageArray = ocrArray.getJSONArray("images");
                    fieldsObject = imageArray.getJSONObject(0);
                    fieldsArray = fieldsObject.getJSONArray("fields");

                } catch (JSONException e) {
                    System.out.println("변환오류");
                }

                String values = "";
                for (int i = 0; i < fieldsArray.length(); i++) {
                    try {
                        JSONObject obj = fieldsArray.getJSONObject(i);
                        String value = obj.getString("inferText")+" ";
                        values += value;

                    } catch (JSONException e) {
                        System.out.println(values);
                    }
                }
                final String valueText = values;
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        allText.setText(valueText);
                    }
                });

                System.out.println(values);
                summary_run(values);



            }
        });



        reqOCR =  findViewById(R.id.reqOCR);

        reqOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread.getState() == Thread.State.NEW) {
                    thread.start();

                }
            }

        });




        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);

        // handle the Choose Image button to trigger
        // the image chooser function
        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageChooser();
            }
        });

        spinner = findViewById(R.id.sizePicker);
        String[] items = new String[100];
        for(int i=0;i<items.length;i++) {
            items[i]= String.valueOf(i+1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(19);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                setAllTextSize(Integer.parseInt(items[position]));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                sizeText.setText("");
            }

        });
    }

    public void summary_run(String text) {
        try {
            System.out.println("summary 시작");
            String messages = getReqMessage2(text);
            URL summary_url = new URL("https://naveropenapi.apigw.ntruss.com/text-summary/v1/summarize");
            HttpURLConnection con = (HttpURLConnection) summary_url.openConnection();

                con.setConnectTimeout(10000);
                con.setRequestMethod("POST");
                con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "q02nxvaanw");
                con.setRequestProperty("X-NCP-APIGW-API-KEY", "eHhxlG3uajb9CQwPWJE0E2BCXkxTo8c5jq67JwIc");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(con.getOutputStream());
                os.write(messages.getBytes("UTF-8"));
                os.flush();
                os.close();


                int responseCode = con.getResponseCode();
                System.out.println("responseCode: " + responseCode);
                System.out.println("Message: " + con.getResponseMessage());


                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    con.getInputStream()));
                    String decodedString;
                    while ((decodedString = in.readLine()) != null) {
                        summaryMessage = decodedString;
                    }
                    in.close();
                    con.disconnect();

                } else {  // 에러 발생
                    summaryMessage = con.getResponseMessage();
                    con.disconnect();
                }
            } catch(Exception e){
                System.out.println("------" + e);
            }


        System.out.println(">>>>>>>>>>" + summaryMessage);
        JSONObject summaryArray;


        try {
            summaryArray = new JSONObject(summaryMessage);
            String values2 = summaryArray.getString("summary");
            final String valueText = values2;
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    summary.setText(valueText);
                }
            });
            dicCroller(valueText);
        } catch (JSONException e) {
            System.out.println("변환오류");
        }

    }

    public static String getReqMessage(String objectStorageURL) {

        String requestBody = "";

        try {

            long timestamp = new Date().getTime();

            JSONObject json = new JSONObject();
            json.put("version", "V1");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", Long.toString(timestamp));

            JSONObject image = new JSONObject();
            image.put("format", "jpeg");
            image.put("data", objectStorageURL);

            image.put("name", "test_ocr");
            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            requestBody = json.toString();
            System.out.println(">>>>>>>>>>" + requestBody);

        } catch (Exception e){
            System.out.println("## Exception : " + e);
        }

        return requestBody;

    }

    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        String temp = encodeToString(bytes, Base64.DEFAULT );
        return temp;
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                            IVPreviewImage.setImageBitmap(selectedImageBitmap);
                            encodedImage=BitmapToString(selectedImageBitmap);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });



    public static String getReqMessage2(String text) {

        String requestBody = "";

        try {

            JSONObject option = new JSONObject();
            option.put("language","ko");

            JSONObject document = new JSONObject();
            document.put("title", "요약");
            document.put("content", text);

            JSONObject request = new JSONObject();
            request.put("document",document);
            request.put("option",option);

            requestBody = request.toString();
            System.out.println(">>>>>>>>>>" + requestBody);

        } catch (Exception e){
            System.out.println("## Exception : " + e);
        }

        return requestBody;

    }



    public void dicCroller(String param) throws JSONException {

        //크롤링 할 구문
        String prompt = param;
        String APIkey = "08f2f324-48d3-46d3-b7b7-aedf39af0cf9";
        String values="";
        try {
            JSONObject body = new JSONObject();
            body.put("document", prompt);
            String messages = body.toString();

            String apiURL = "https://api.matgim.ai/54edkvw2hn/api-keyword"; // json 결과
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("x-auth-token", APIkey);
            con.setRequestProperty("Content-Type", "application/json");
            DataOutputStream os = new DataOutputStream(con.getOutputStream());
            os.write(messages.getBytes("UTF-8"));
            os.flush();
            os.close();



            int responseCode = con.getResponseCode();
            System.out.println(responseCode);
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }
            br.close();
            con.disconnect();
            String dicHtml = response.toString();
            System.out.println(dicHtml);

            ArrayList<String> items = new ArrayList<String>();
            //json파싱
            JSONObject jsonVar = new JSONObject(dicHtml);
            JSONArray sentences = (JSONArray)jsonVar.get("sentences");
            for (int j = 0; j < sentences.length();j++){
                JSONObject sentence = sentences.getJSONObject(j);
                JSONArray keywords = sentence.getJSONArray("keywords");
                for (int i = 0; i < keywords.length(); i++) {

                    JSONObject temp = keywords.getJSONObject(i);

                    String movieNm = temp.getString("word");

                    items.add(movieNm);
                }
            }
            for (int i=0;i<items.size();i++){
                values+=dictionaryApi(items.get(i))+"\n";
            }

            final String result = values;



            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    dicText.setText(result);
                }

            });

            //testText.setText(response.toString());
            //System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void expand1(View view){
        int v = (summary.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(layout1, new AutoTransition());
        summary.setVisibility(v);
    }
    public void expand2(View view){
        int v = (allText.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(layout2, new AutoTransition());
        allText.setVisibility(v);
    }
    public void expand3(View view){
        int v = (dicText.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
        TransitionManager.beginDelayedTransition(layout3, new AutoTransition());
        dicText.setVisibility(v);
    }

    public void setAllTextSize(int num){
        summary.setTextSize(num);
        allText.setTextSize(num);
        dicText.setTextSize(num);

    }

    public String dictionaryApi(String text) {
        String APIkey = "EDCF58CEC05E3D87A15E21895B628A53";
        StringBuffer buffer = new StringBuffer();

        try {

            String apiURL = "https://krdict.korean.go.kr/api/search?key=" + APIkey + "&q=" + text+"&req_type=json&num=10"; // json 결과

            URL url = new URL(apiURL);
            InputStream is= url.openStream(); // url 위치로 인풋스트림 연결
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            // inputstream 으로부터 xml 입력받기
            xpp.setInput( new InputStreamReader(is, "UTF-8") );
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String tag;



            xpp.next();

            int eventType= xpp.getEventType();



            while( eventType != XmlPullParser.END_DOCUMENT ){

                switch( eventType ) {

                    case XmlPullParser.START_DOCUMENT:


                        break;


                    case XmlPullParser.START_TAG:

                        tag = xpp.getName(); // 태그 이름 얻어오기


                        if (tag.equals("")) ;

                        else if (tag.equals("word")) {

                            buffer.append("단어 : ");

                            xpp.next();

                            // addr 요소의 TEXT 읽어와서 문자열버퍼에 추가

                            buffer.append(xpp.getText());

                            buffer.append("\n"); // 줄바꿈 문자 추가

                        } else if (tag.equals("definition")) {

                            buffer.append("의미 : ");

                            xpp.next();

                            buffer.append(xpp.getText());

                            buffer.append("\n");

                        }
                    case XmlPullParser.TEXT:

                        break;



                    case XmlPullParser.END_TAG:

                        tag= xpp.getName(); // 태그 이름 얻어오기

                        if(tag.equals("item")) buffer.append("\n"); // 첫번째 검색결과종료 후 줄바꿈

                        break;

                }

                eventType= xpp.next();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return buffer.toString(); // 파싱 다 종료 후 StringBuffer 문자열 객체 반환


    }
}

