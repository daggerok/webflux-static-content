package daggerok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class JavaApplication {

  @Component
  public class CustomWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {

      final ServerHttpRequest req = exchange.getRequest();

      return req.getURI().getPath().equals("/")
          ? chain.filter(exchange.mutate().request(req.mutate()
                                                      .path("/index.html")
                                                      .build())
                                 .build())
          : chain.filter(exchange);
    }
  }

  @Bean
  public RouterFunction routerFunction() {

    return route(GET("/api/**"),
                 request -> ok().body(Flux.just("one", "two"), String.class))
        .andOther(resources("/**", new ClassPathResource("/static")))
        ;
  }

  public static void main(String[] args) {
    SpringApplication.run(JavaApplication.class, args);
  }
}
