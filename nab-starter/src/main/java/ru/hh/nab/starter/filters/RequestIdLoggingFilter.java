package ru.hh.nab.starter.filters;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.hh.nab.common.mdc.MDC;
import ru.hh.nab.starter.server.RequestHeaders;

//public final class RequestIdLoggingFilter extends OncePerRequestFilter {
public final class RequestIdLoggingFilter implements jakarta.servlet.Filter {

//  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//                                  HttpServletResponse response,
//                                  FilterChain filterChain) throws ServletException, IOException {
//
//    String requestId = request.getHeader(RequestHeaders.REQUEST_ID);
//    try {
//      if (requestId == null) {
//        requestId = RequestHeaders.EMPTY_REQUEST_ID;
//      } else {
//        response.addHeader(RequestHeaders.REQUEST_ID, requestId);
//      }
//      MDC.setRequestId(requestId);
//
//      filterChain.doFilter(request, response);
//
//    } finally {
//      MDC.clearRequestId();
//    }
//  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, jakarta.servlet.FilterChain chain) throws IOException, jakarta.servlet.ServletException {
    //todo
//    String requestId = request.getHeader(RequestHeaders.REQUEST_ID);
//    try {
//      if (requestId == null) {
//        requestId = RequestHeaders.EMPTY_REQUEST_ID;
//      } else {
//        response.addHeader(RequestHeaders.REQUEST_ID, requestId);
//      }
//      MDC.setRequestId(requestId);
//
//      filterChain.doFilter(request, response);
//
//    } finally {
//      MDC.clearRequestId();
//    }
  }
}
