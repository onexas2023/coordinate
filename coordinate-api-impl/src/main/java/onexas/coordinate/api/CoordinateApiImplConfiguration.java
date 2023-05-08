package onexas.coordinate.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import onexas.coordinate.common.app.Env;

/**
 * 
 * @author Dennis Chen
 *
 */
@Configuration(Env.NS_BEAN + "CoordinateApiImplConfiguration")
@Profile({ Env.PROFILE_API_NODE })
@EnableWebSocket
public class CoordinateApiImplConfiguration implements WebSocketConfigurer {
	
	@Value("#{${websocket.maxTextMessageBufferSize:1024*1024}}")
	Integer maxTextMessageBufferSize;
	
	@Value("#{${websocket.maxBinaryMessageBufferSize:1024*1024}}")
	Integer maxmaxBinaryMessageBufferSize;
	
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		//default nothing
	}
	
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(maxTextMessageBufferSize);
        container.setMaxBinaryMessageBufferSize(maxmaxBinaryMessageBufferSize);
        return container;
    }
}