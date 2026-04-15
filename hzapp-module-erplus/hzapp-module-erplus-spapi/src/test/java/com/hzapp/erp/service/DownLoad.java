package com.hzapp.erp.service;

// DownloadExample.java
// This example is for use with the Selling Partner API for Reports, Version: 2021-06-30
// and the Selling Partner API for Feeds, Version: 2021-06-30


import okhttp3.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

/**
 * Example that downloads a document.
 */
public class DownLoad {

    public static void main(String args[]) {
        String url = "https://tortuga-prod-na.s3-external-1.amazonaws.com/320a906a-beee-4ad3-98cf-84c8efadb2a3.amzn1.tortuga.4.na.T1VHD6PLNURXY9?response-content-encoding=identity&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251210T041620Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=AKIA5U6MO6RAJGX6QOWO%2F20251210%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=9d46894db628242b120ef80f682c324843ba77730b0ec36b6df52ae2f410ef97";
        String compressionAlgorithm = "GZIP";

        DownLoad obj = new DownLoad();
        try {
            obj.download(url, compressionAlgorithm);
        } catch (IOException e) {
            //Handle exception here.
        } catch (IllegalArgumentException e) {
            //Handle exception here.
        }
    }

    /**
     * Download and optionally decompress the document retrieved from the given url.
     *
     * @param url                  the url pointing to a document
     * @param compressionAlgorithm the compressionAlgorithm used for the document
     * @throws IOException              when there is an error reading the response
     * @throws IllegalArgumentException when the charset is missing
     */
    public void download(String url, String compressionAlgorithm) throws IOException, IllegalArgumentException {
        OkHttpClient httpclient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = httpclient.newCall(request).execute();
        if (!response.isSuccessful()) {
            System.out.println(
                    String.format("Call to download content was unsuccessful with response code: %d and message: %s",
                            response.code(), response.message()));
            return;
        }

        try (ResponseBody responseBody = response.body()) {
            MediaType mediaType = MediaType.parse(response.header("Content-Type"));
            Charset charset = mediaType.charset();
            if (charset == null) {
                throw new IllegalArgumentException(String.format(
                        "Could not parse character set from '%s'", mediaType.toString()));
            }

            Closeable closeThis = null;
            try {
                InputStream inputStream = responseBody.byteStream();
                closeThis = inputStream;

                if ("GZIP".equals(compressionAlgorithm)) {
                    inputStream = new GZIPInputStream(inputStream);
                    closeThis = inputStream;
                }

                // This example assumes that the download content has a charset in the content-type header, e.g.
                // text/plain; charset=UTF-8
                if ("text".equals(mediaType.type()) && "plain".equals(mediaType.subtype())) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
                    closeThis = inputStreamReader;

                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    closeThis = reader;

                    String line;
                    do {
                        line = reader.readLine();
                        // Process line by line.
                    } while (line != null);
                } else {
                    System.out.println(IOUtils.toString(inputStream, charset));
                }
            } finally {
                if (closeThis != null) {
                    closeThis.close();
                }
            }
        }
    }
}