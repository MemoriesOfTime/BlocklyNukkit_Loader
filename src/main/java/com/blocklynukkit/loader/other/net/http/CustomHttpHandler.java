package com.blocklynukkit.loader.other.net.http;

import com.blocklynukkit.loader.Loader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CustomHttpHandler implements HttpHandler {
    public String callback;
    public CustomHttpHandler(String callback){
        this.callback = callback;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpRequestEntry requestEntry = new HttpRequestEntry(exchange);
        requestEntry.setFromAddress(exchange.getRemoteAddress().getAddress().getHostAddress()+":"+exchange.getRemoteAddress().getPort());
        requestEntry.setRequestUrl(exchange.getRequestURI().toString());
        requestEntry.setPath(exchange.getRequestURI().getQuery());
        StringBuilder stringBuilder = new StringBuilder();
        exchange.getRequestHeaders().forEach((k,v)->{
            stringBuilder.append(k).append(": ");
            for(short i=0;i<v.size();i++){
                stringBuilder.append(v.get(i));
                if (i != v.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("\n");
        });
        requestEntry.setHeader(stringBuilder.toString());
        requestEntry.setMethod(exchange.getRequestMethod());
        if (exchange.getRequestMethod().equals("GET")) {
            requestEntry.setParameter(exchange.getRequestURI().getQuery());
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
            StringBuilder requestBodyContent = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                requestBodyContent.append(line).append('\n');
            }
            requestEntry.setParameter(requestBodyContent.toString());
        }
        requestEntry.setProtocol(exchange.getProtocol());
        Loader.plugin.call(callback,requestEntry);
    }
}
