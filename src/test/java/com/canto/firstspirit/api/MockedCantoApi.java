package com.canto.firstspirit.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockedCantoApi {


  static CantoApi getMockedApi(Map<HttpUrl, String> mockData) throws IOException {

    CantoApi mockedApi = mock(CantoApi.class);

    for (HttpUrl httpUrl : mockData.keySet()) {

      Request request = new Request.Builder().url(httpUrl)
          .build();

      Response mockResponse = new Response.Builder().code(200) // Set your desired response code
          .message("OK") // Set a response message
          .request(request) // Set the request associated with this response
          .body(ResponseBody.create(mockData.get(httpUrl), MediaType.get("application/json"))) // Set the response body
          .build();

      when(mockedApi.executeGetRequest(httpUrl)).thenReturn(mockResponse);

    }

    Request request = new Request.Builder().build();

    Response mockResponse = new Response.Builder().code(200) // Set your desired response code
        .message("OK") // Set a response message
        .request(request) // Set the request associated with this response
        .body(ResponseBody.create("no_data", MediaType.get("application/json"))) // Set the response body
        .build();

    when(mockedApi.executeGetRequest(any(HttpUrl.class))).thenReturn(mockResponse);

    return mockedApi;
  }


}
