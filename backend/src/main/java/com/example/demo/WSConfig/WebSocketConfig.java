package com.example.demo.WSConfig;

import com.example.demo.Controllers.MessagingController;
import com.example.demo.Globals.GlobalLogger;
import com.example.demo.Security.JwtTokenProvider;
import com.example.demo.Services.Customs.CustomUserDetailsService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private static final Logger logger = GlobalLogger.getLogger(MessagingController.class);

    @Autowired
    public WebSocketConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                logger.debug("Received STOMP frame: {}", accessor);
                return message;
            }
        });
    }
    @Bean
    public ChannelInterceptor authChannelInterceptor(){
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(
                    @NotNull Message<?> message,
                    @NotNull MessageChannel channel){
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                //Checking if it is a CONNECT frame
                if(StompCommand.CONNECT.equals(accessor.getCommand())){
                    logger.debug("Handling CONNECT frame");
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    logger.debug("Authorization header: {}", authHeader);

                    if(authHeader != null && authHeader.startsWith("Bearer ")){
                        String token = authHeader.substring(7);
                        if(jwtTokenProvider.isTokenValid(token)){
                            String username = jwtTokenProvider.getUsernameFromToken(token);
                            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                            accessor.setUser(authentication);
                        }
                    }
                }
                return message;
            }
        };
    }

}
