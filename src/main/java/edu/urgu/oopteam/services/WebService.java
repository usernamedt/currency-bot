package edu.urgu.oopteam.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebService {
    public static String getPageContent(String pageAddress, String codePage) throws Exception {
        StringBuilder sb = new StringBuilder();
        URL pageURL = new URL(pageAddress);
        URLConnection uc = pageURL.openConnection();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        uc.getInputStream(), codePage))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }
}
