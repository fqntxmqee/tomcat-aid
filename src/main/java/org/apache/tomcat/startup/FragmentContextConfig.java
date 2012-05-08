
package org.apache.tomcat.startup;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.ContextConfig;
import org.apache.jasper.compiler.TldLocationsCache;
import org.apache.tomcat.jasper.FragmentEmbeddedServletOptions;
import org.apache.tomcat.jasper.FragmentTldLocationsCache;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * 支持jar中的web.xml加载！
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-5-4 下午1:25:24
 * @version
 */
public class FragmentContextConfig extends ContextConfig {

	private String	defaultJarWebXmlName	= "META-INF/webframe/web.xml";

	@Override
	protected void applicationWebConfig() {
		scanJarWebXml();
		initTldLocationsCache();
		super.applicationWebConfig();
	}

	protected void scanJarWebXml() {
		ClassLoader classLoader = context.getLoader().getClassLoader();
		try {
			Enumeration<URL> jarWebXmls = classLoader.getResources(defaultJarWebXmlName);
			while (jarWebXmls.hasMoreElements()) {
				URL url = jarWebXmls.nextElement();
				URLConnection urlConnection = url.openConnection();
				urlConnection.setUseCaches(false);
				if (urlConnection instanceof JarURLConnection) {
					if (log.isDebugEnabled()) {
						log.debug("加载jar包中" + defaultJarWebXmlName + "文件！");
					}
					processJarWebConfig(urlConnection.getInputStream(),
						new InputSource(url.toExternalForm()));
				}
			}
		} catch (IOException e) {
			// ignore
		}
	}

	protected void processJarWebConfig(InputStream stream, InputSource source) {
		synchronized (webDigester) {
			try {
				source.setByteStream(stream);
				webDigester.push(this.context);
				webDigester.setErrorHandler(new FragmentContextErrorHandler());
				webDigester.parse(source);
				if (this.parseException != null) {
					this.ok = false;
				}
			} catch (Exception e) {
				errorPrint("contextConfig.applicationParse", source.getSystemId(),
					e);
				this.ok = false;
			} finally {
				webDigester.reset();
				this.parseException = null;
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (IOException e) {
					errorPrint("contextConfig.applicationClose", null, e);
				}
			}
		}
		webRuleSet.recycle();
	}

	protected void errorPrint(String key, Object arg, Throwable e) {
		if (arg == null) {
			log.error(sm.getString(key), e);
		} else {
			log.error(sm.getString(key, arg), e);
		}
	}

	protected void initTldLocationsCache() {
		ClassLoader classLoader = context.getLoader().getClassLoader();
		try {
			Enumeration<URL> jarWebXmls = classLoader.getResources(defaultJarWebXmlName);
			ServletContext ctxt = context.getServletContext();
			TldLocationsCache locationsCache = new FragmentTldLocationsCache(ctxt, jarWebXmls);
			Container container = context.findChild("jsp");
			if (container != null && container instanceof Wrapper) {
				Wrapper wrapper = (Wrapper) container;
				wrapper.addInitParameter("engineOptionsClass",
					FragmentEmbeddedServletOptions.class.getName());
				ctxt.setAttribute(TldLocationsCache.class.getName(), locationsCache);
			}
		} catch (IOException e) {
			// ignore
		}
	}

	protected class FragmentContextErrorHandler implements ErrorHandler {

		protected FragmentContextErrorHandler() {
		}

		@Override
		public void error(SAXParseException exception) {
			FragmentContextConfig.this.parseException = exception;
		}

		@Override
		public void fatalError(SAXParseException exception) {
			FragmentContextConfig.this.parseException = exception;
		}

		@Override
		public void warning(SAXParseException exception) {
			FragmentContextConfig.this.parseException = exception;
		}
	}
}
