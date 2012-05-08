
package org.apache.tomcat.jasper;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.jasper.JasperException;
import org.apache.jasper.compiler.TldLocationsCache;
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-5-7 下午4:38:24
 * @version
 */
public class FragmentTldLocationsCache extends TldLocationsCache {

	private Log									log			= LogFactory.getLog(FragmentTldLocationsCache.class);

	private boolean							initialized	= false;

	private Hashtable<String, String[]>	mappings		= null;

	private ServletContext					ctxt			= null;

	private Enumeration<URL>				jarWebXmls	= null;

	public FragmentTldLocationsCache(ServletContext ctxt,
				Enumeration<URL> jarWebXmls) {
		this(ctxt, jarWebXmls, true);
	}

	protected FragmentTldLocationsCache(ServletContext ctxt,
				Enumeration<URL> jarWebXmls,
				boolean redeployMode) {
		super(ctxt, redeployMode);
		this.mappings = new Hashtable<String, String[]>();
		this.ctxt = ctxt;
		this.jarWebXmls = jarWebXmls;
	}

	@Override
	public String[] getLocation(String uri) throws JasperException {
		if (!initialized) {
			processJarWebXml();
			initialized = true;
		}
		String[] locations = getJarWebXmlTagConfig(uri);
		if (locations != null) {
			return locations;
		}
		return super.getLocation(uri);
	}

	protected String[] getJarWebXmlTagConfig(String uri) {
		return this.mappings.get(uri);
	}

	protected void processJarWebXml() {
		while (jarWebXmls.hasMoreElements()) {
			InputStream stream = null;
			try {
				URL url = jarWebXmls.nextElement();
				URLConnection urlConnection = url.openConnection();
				urlConnection.setUseCaches(false);
				if (urlConnection instanceof JarURLConnection) {
					stream = urlConnection.getInputStream();
					InputSource is = new InputSource(stream);
					is.setSystemId(url.toExternalForm());
					processJarWebXml(is);
				}
			} catch (Exception e) {
				log.warn(e.getMessage());
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (Throwable t) {
						// ignore
					}
				}
			}
		}
	}

	protected void processJarWebXml(InputSource jarWebXml)
				throws JasperException, MalformedURLException {
		TreeNode webtld = new ParserUtils().parseXMLDocument(
			"/META-INF/webframe/web.xml", jarWebXml);
		TreeNode jspConfig = webtld.findChild("jsp-config");
		if (jspConfig != null) {
			webtld = jspConfig;
		}
		Iterator<?> taglibs = webtld.findChildren("taglib");
		while (taglibs.hasNext()) {
			TreeNode taglib = (TreeNode) taglibs.next();
			String tagUri = null;
			String tagLoc = null;
			TreeNode child = taglib.findChild("taglib-uri");
			if (child != null) {
				tagUri = child.getBody();
			}
			child = taglib.findChild("taglib-location");
			if (child != null) {
				tagLoc = child.getBody();
			}
			if (tagLoc == null) {
				continue;
			}
			if (uriType(tagLoc) == 2) {
				tagLoc = "/WEB-INF/" + tagLoc;
			}
			String tagLoc2 = null;
			if (tagLoc.endsWith(".jar")) {
				tagLoc = this.ctxt.getResource(tagLoc).toString();
				tagLoc2 = "META-INF/taglib.tld";
			}
			addTld(tagUri, new String[]{
						tagLoc, tagLoc2});
		}
	}

	protected void addTld(String uri, String[] tlds) {
		if (uri == null || tlds == null) {
			return;
		}
		mappings.put(uri, tlds);
	}
}
