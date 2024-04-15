package com.eg.test.model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestWrapper extends HttpServletRequestWrapper {
	
	private String reqBody;

	public String getReqBody() {
		return reqBody;
	}

	public void setReqBody(String reqBody) {
		this.reqBody = reqBody;
	}

	public RequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		StringBuilder stringBuilder = new StringBuilder();
		ServletInputStream servletInputStream = request.getInputStream();
		try(BufferedReader bfrdReader = new BufferedReader(new InputStreamReader((InputStream)servletInputStream))) {
			if(servletInputStream != null) {
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while((bytesRead = bfrdReader.read(charBuffer)) > 0){
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			}else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		}
		this.reqBody = stringBuilder.toString();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException{
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.reqBody.getBytes());
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
			
		};
	}
	
	@Override
	public BufferedReader getReader() throws IOException{
		return new BufferedReader (new InputStreamReader((InputStream)getInputStream()));
	}

}
