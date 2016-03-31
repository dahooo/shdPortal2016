package com.shd.servlets;

import java.util.Properties;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ibm.portal.um.PumaHome;

public abstract class BaseServlet extends HttpServlet{

	@Autowired
	@Qualifier("imConfiguration")
	protected Properties imConfiguration;
	
	protected PumaHome pumaHome;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private WebApplicationContext springContext;
    /*
     * for spring autowired
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
        final AutowireCapableBeanFactory beanFactory = springContext.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(this);
        
        try {
	        Context context = new InitialContext();
	        Name pumaJndiName = new CompositeName(PumaHome.JNDI_NAME);
	        pumaHome = (PumaHome) context.lookup(pumaJndiName);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
}
