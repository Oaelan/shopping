package com.example.demo.filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entity.UsersEntity;
import com.example.demo.provider.JwtProvider;
import com.example.demo.repository.UsersRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;


@Component
@RequiredArgsConstructor
public class JwtAuthoenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UsersRepository usersRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
			String token = parseBearerToken(request);
			
			if(token == null) {
				filterChain.doFilter(request, response);
				return;
			}
		
			String email = jwtProvider.validate(token);
			if(email == null) {
				filterChain.doFilter(request, response);
				return;
			}
			
			UsersEntity usersEntity = usersRepository.findByEmail(email); // 메서드 이름 수정
			String role = usersEntity.getRole(); //ROLE_USER, ROLE_ADMIN;
			
			List<GrantedAuthority> authorities = new ArrayList<>();

			authorities.add(new SimpleGrantedAuthority(role));
			
			SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
			AbstractAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(email,null,authorities);
			
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			securityContext.setAuthentication(authenticationToken);
			SecurityContextHolder.setContext(securityContext);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		filterChain.doFilter(request, response);
		
	}
	
	private String parseBearerToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		
		boolean hasAuthorization = StringUtils.hasText(authorization);
		
		if(!hasAuthorization) return null;
		
		boolean isBearer = authorization.startsWith("Bearer");
		
		if(!isBearer) return null;
		
		String token = authorization.substring(7);
		return token;
	}
	

	
}
