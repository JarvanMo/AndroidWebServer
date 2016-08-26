package com.mikhaellopez.androidwebserver;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Mikhael LOPEZ on 14/12/2015.
 * update by mo
 */
public class AndroidWebServer extends NanoHTTPD {

    public AndroidWebServer(int port) {
        super(port);
    }

    public AndroidWebServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {

        String m3u8Txt = "#EXTM3U\n" +
                "#EXT-X-VERSION:3\n" +
                "#EXT-X-TARGETDURATION:7\n" +
                "#EXT-X-MEDIA-SEQUENCE:2\n" +
                "#EXTINF:6.066667,\n" +
                "output2.ts\n" +
                "#EXTINF:5.933333,\n" +
                "output3.ts\n" +
                "#EXTINF:5.166667,\n" +
                "output4.ts\n" +
                "#EXTINF:5.100000,\n" +
                "output5.ts\n" +
                "#EXTINF:3.700000,\n" +
                "output6.ts\n" +
                "#EXT-X-ENDLIST";

        String url = session.getUri();
        String fileUrl = "";
        String videoType = "";
        if(url.endsWith(".m3u8")){
//            fileUrl = "/storage/emulated/0/xyz/output.m3u8";
            videoType = "application/x-mpegurl";
        }else if(url.endsWith(".ts")) {
            fileUrl = "/storage/emulated/0/xyz/"+url.substring(url.lastIndexOf("/"));
            videoType = "video/mp2t";
        }


        Response response = null;

        try {
            final File f = new File(fileUrl);
            InputStream inputStream = null;
            if(url.endsWith(".m3u8")){
                byte[] bytes = m3u8Txt.getBytes("UTF-8");
                inputStream = new ByteArrayInputStream(bytes);
            }else if(url.endsWith(".ts")){
                inputStream = new FileInputStream(f);
            }
//                InputStream inputStream = getResources().openRawResource(R.raw.encrypted);
//                inputStream = new InputStreamEncrypted(inputStream);


            int totalLength = inputStream.available();

            String requestRange = session.getHeaders().get("range");
            if (requestRange == null) {
                //http 200"video/mp4"
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK,videoType , inputStream, totalLength);
            } else {
                //http 206

                //region get RangeStart from head
                Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(requestRange);
                matcher.find();
                long start = 0;
                try { start = Long.parseLong(matcher.group(1)); } catch (Exception e) { e.printStackTrace(); }
                //endregion

                inputStream.skip(start);

                long restLength = totalLength - start;
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, videoType, inputStream, restLength);

                String contentRange = String.format("bytes %d-%d/%d", start, totalLength, totalLength);
                response.addHeader("Content-Range", contentRange);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


    public Response response404(IHTTPSession session,String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><body>");
        builder.append("Sorry, Can't Found "+url + " !");
        builder.append("</body></html>\n");
        return  newFixedLengthResponse(builder.toString());
    }

    protected String getQuotaStr(String text) {
        return "\"" + text + "\"";
    }
}
