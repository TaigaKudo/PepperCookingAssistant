package io.github.TaigaKudo.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private final JwtService jwtService;
	
	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
			) throws ServletException, IOException{
		String authorizationHeader = request.getHeader("Authorization");
		
		System.out.println("Authorization = " + authorizationHeader);
		
		if(authorizationHeader == null
				|| !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request,  response);
			return;
		}
		
		// "Bearer xxxxxxxx"の頭7文字を削除
		String token = authorizationHeader.substring(7);
		
		try {
			Long userId = jwtService.validateAndExtractUserId(token);
			
			System.out.println("JWT OK userId = " + userId);
			
			UsernamePasswordAuthenticationToken authentication = 
				new UsernamePasswordAuthenticationToken(
						userId,					// ログインユーザー情報
						null,					// 認証情報（JWTで検証済みなのでnull）
						Collections.emptyList()	// 権限情報
						);
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		filterChain.doFilter(request,  response);
	}
}
