package com.redp.geotrack;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetToken {

    public String getToken() throws IOException {
        String url = "https://git.redp.icu/pjpmosteiro/ext/-/raw/main/tokenapp";
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        //response status
        int status = con.getResponseCode();
        System.out.println(status);
        //response body
        if (status == 200) {

            String line;

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder sb = new StringBuilder();

            while ((line = in.readLine()) != null) {

                sb.append(line);
            }
            return sb.toString();
        } else {
            System.out.println("error GET info");
        }

        return url;
    }}