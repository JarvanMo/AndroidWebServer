package com.mikhaellopez.androidwebserver;

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

       final File f = new File("/storage/emulated/0/xyz/test.mp4");
//        String msg = "<html><body><h1>Hello server</h1>\n";
//        Map<String, String> parms = session.getParms();
//        if (parms.get("username") == null) {
//            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//        } else {
//            msg += "<p>Hello, " + parms.get("username") + "!</p>";
//        }
//        return newFixedLengthResponse( msg + "</body></html>\n" );
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//            Response response = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, getMimeTypeForFile(f.getAbsolutePath()), fis,f.length());
//            response.addHeader("Content-Length", "" + f.length());
//            response.addHeader("Accept-Ranges", "bytes");
//            response.addHeader("Content-Range", "bytes " + 0 + "-" +
//                    f.length() + "/" + f.length());
//            return response ;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//           return response404(session,"");
//        }
//        StringBuilder builder = new StringBuilder();
//        builder.append("<!DOCTYPE html><html><body>");
//        builder.append("<video ");
//        builder.append("width="+600+" ");
//        builder.append("height="+600+" ");
//        builder.append("controls>");
//        builder.append("<source src="+getQuotaStr("/storage/emulated/0/xyz/test.mp4")+" ");
//        builder.append("type="+getQuotaStr("video/mp4")+">");
//        builder.append("Your browser doestn't support HTML5");
//        builder.append("</video>");
//        builder.append("</body></html>\n");
//        return newFixedLengthResponse(builder.toString());

        Response response = null;

        try {
            InputStream inputStream = new FileInputStream(f);
            int totalLength = inputStream.available();
            String requestRange = session.getHeaders().get("range");

            if (requestRange == null) {
                //http 200
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "video/mp4", inputStream, totalLength);
            } else {
                //http 206

                //region get RangeStart from head
                Matcher matcher = Pattern.compile("bytes=(\\d+)-(\\d*)").matcher(requestRange);
                matcher.find();
                long start = 0;
                try {
                    start = Long.parseLong(matcher.group(1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //endregion

                inputStream.skip(start);

                long restLength = totalLength - start;
                response = NanoHTTPD.newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, "video/mp4", inputStream, restLength);

                String contentRange = String.format("bytes %d-%d/%d", start, totalLength, totalLength);
                response.addHeader("Content-Range", contentRange);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  response;
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
