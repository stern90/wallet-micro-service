package wallet.server;

import wallet.meta.Account;
import wallet.server.common.SecurityContextHolder;
import wallet.util.DataUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static wallet.server.WebResponse.LOGIN_FAIL;

public class LoginFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (!request.getRequestURI().contains("/login")) {
			String token = request.getParameter("token");
			Account account = DataUtil.isLogin(token);
			if (account == null) {
				WebResponse.writeReturn(servletResponse, WebResponse.error(LOGIN_FAIL));
				return;
			}
			SecurityContextHolder.setContext(account);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
	}
}
