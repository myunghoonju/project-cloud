package com.netflix.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ZuulLog extends ZuulFilter {

    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    public int filterOrder() {
        return FilterConstants.DEBUG_FILTER_ORDER;
    }

    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        ObjectMapper objectMapper = new ObjectMapper();
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        log.error("--> {}", requestURI);
      if (requestURI.equals("/client-two/test2")) {
        return clientTwo(ctx, objectMapper);
      }

      return clientOne(ctx, objectMapper);
    }

  private static Object clientOne(RequestContext ctx, ObjectMapper objectMapper) {
    InputStream input = (InputStream) ctx.get("requestEntity");
    try {
      if (input == null) {
        input = ctx.getRequest().getInputStream();
      }
      ObjectNode jsonNode = objectMapper.readValue(input, ObjectNode.class);
      if (jsonNode.path("input") != null) {
          jsonNode.put("input","modified");
      }

      ctx.put("requestEntity", new ServletInputStreamWrapper(objectMapper.writeValueAsBytes(jsonNode)));

      return null;
    } catch (IOException e) {
      return null;
    }
  }

  private static Object clientTwo(RequestContext ctx, ObjectMapper objectMapper) {
    InputStream input = (InputStream) ctx.get("requestEntity");
    try {
      if (input == null) {
        input = ctx.getRequest().getInputStream();
      }
      ArrayNode arrayNode = objectMapper.readValue(input, ArrayNode.class);
      ArrayNode newList = objectMapper.createArrayNode();
      ObjectNode newNode3= objectMapper.createObjectNode();
      if (!arrayNode.isEmpty()) {
        ObjectNode newNode = objectMapper.createObjectNode();
        ObjectNode newNode2 = objectMapper.createObjectNode();

        arrayNode.remove(0);
        newNode.put("input", "modified-list");
        newNode2.put("input", "modified-list2");
        newList.add(newNode);
        newList.add(newNode2);
        newNode3.set("test", newList);
      }

      ctx.put("requestEntity",new ByteArrayInputStream((objectMapper.writeValueAsString(newList).getBytes(StandardCharsets.UTF_8))));


      return null;
    } catch (IOException e) {
      return null;
    }
  }
}