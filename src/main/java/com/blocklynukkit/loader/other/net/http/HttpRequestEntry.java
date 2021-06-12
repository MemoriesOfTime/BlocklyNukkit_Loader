package com.blocklynukkit.loader.other.net.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class HttpRequestEntry{
    String requestUrl;
    String requestRawUrl;
    String path; // starts with /xxx
    String header;
    String method;
    String parameter;
    String protocol;//http version
    String fromAddress;

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestRawUrl() {
        return requestRawUrl;
    }

    public void setRequestRawUrl(String requestRawUrl) {
        this.requestRawUrl = requestRawUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    HttpExchange exchange;
    public HttpRequestEntry(HttpExchange exchange){
        this.exchange = exchange;
    }

    public void addResponseHeaderEntry(String name,String value){
        exchange.getResponseHeaders().add(name, value);
    }

    public void addResponseHeader(String header){
        for(String line:header.split("\n")){
            String[] tmp = header.split(": *",2);
            exchange.getResponseHeaders().add(tmp[0],tmp[1]);
        }
    }

    public void addDefaultResponseHeader(){
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset=utf-8");
    }

    public void addDefaultResponseHeader(String charSet){
        exchange.getResponseHeaders().add("Content-Type", "text/html;charset="+charSet);
    }

    public boolean response(int statusCode,String content){
        return response(statusCode, content, "utf-8");
    }
    public boolean response(int statusCode,String content,String charSet){
        try {
            return response(statusCode, content.getBytes(charSet));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean response(int statusCode,byte[] content){
        try{
            exchange.sendResponseHeaders(statusCode, content.length);
            OutputStream out = exchange.getResponseBody();
            out.write(content);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
