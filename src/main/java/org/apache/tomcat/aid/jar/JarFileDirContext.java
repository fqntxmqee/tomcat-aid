
package org.apache.tomcat.aid.jar;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;

import org.apache.catalina.Loader;
import org.apache.naming.NamingEntry;
import org.apache.naming.resources.FileDirContext;
import org.apache.tomcat.util.file.Matcher;

/**
 * JarFileDirContext
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-24 下午1:46:09
 * @version
 */
public class JarFileDirContext extends FileDirContext {

	// 默认从jar包中搜索的资源文件后缀
	private final static String	defaultPatterns	= "/css/*|/images/*|/jsp/*|/js/*";

	private final static String	RESOURCES	= "resources/";

	// 切割字符串scanJarFileSuffix的分割符
	private String						delimiters			= "|,;";

	// 需要从jar包中加载资源的pattern集合
	private List<String>				patterns				= null;

	private Loader						loader				= null;

	private ClassLoader				classLoader			= null;

	private boolean					useResources		= true;

	public JarFileDirContext() {
		String[] arr = defaultPatterns.split("\\|");
		patterns = new ArrayList<String>(Arrays.asList(arr));
	}

	public JarFileDirContext(Loader loader,
				String scanJarFileSuffix,
				String pattern,
				boolean useResources) {
		this();
		this.setLoader(loader);
		this.setScanJarFileSuffix(scanJarFileSuffix);
		this.setAddPattern(pattern);
		this.setUseResources(useResources);
	}

	public synchronized void setLoader(Loader loader) {
		this.loader = loader;
	}

	/**
	 * 覆盖默认的pattern
	 * 
	 * @param scanJarFileSuffix
	 * @author 黄国庆 2012-4-24 下午9:16:47
	 */
	public synchronized void setScanJarFileSuffix(String scanJarFileSuffix) {
		if (scanJarFileSuffix == null || "".equals(scanJarFileSuffix)) {
			return;
		}
		this.patterns = tokenizeToStringArray(scanJarFileSuffix, delimiters,
			true, true);
	}

	/**
	 * 追加默认的pattern
	 * 
	 * @param pattern
	 * @author 黄国庆 2012-4-24 下午9:16:29
	 */
	public synchronized void setAddPattern(String pattern) {
		if (pattern == null) {
			return;
		}
		this.patterns.addAll(tokenizeToStringArray(pattern, delimiters, true,
			true));
	}

	public synchronized void setUseResources(boolean useResources) {
		this.useResources = useResources;
	}

	@Override
	protected Attributes doGetAttributes(String name, String[] attrIds)
				throws NamingException {
		Attributes attributes = super.doGetAttributes(name, attrIds);
		if (attributes == null) {
			URL url = getURLResource(name);
			if (url != null) {
				return new URLResourceAttributes(url);
			}
		}
		return attributes;
	}

	@Override
	protected List<NamingEntry> doListBindings(String name)
				throws NamingException {
		List<NamingEntry> list = super.doListBindings(name);
		if (list == null) {
			return list((URL) null);
		}
		return list;
	}

	@Override
	protected Object doLookup(String name) {
		Object result = super.doLookup(name);
		if (result == null) {
			URL url = getURLResource(name);
			if (url != null) {
				return new URLResource(url);
			}
		}
		return result;
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		try {
			super.rename(oldName, newName);
		} catch (NamingException ne) {
			if (getURLResource(oldName) != null) {
				throw new OperationNotSupportedException();
			}
		}
	}

	@Override
	public void unbind(String name) throws NamingException {
		try {
			super.unbind(name);
		} catch (NamingException ne) {
			if (getURLResource(name) != null) {
				throw new OperationNotSupportedException();
			}
		}
	}

	protected List<NamingEntry> list(URL url) {
		return new ArrayList<NamingEntry>();
	}

	/**
	 * 从jar包中获取URL资源文件
	 * 
	 * @param res
	 * @return
	 * @author 黄国庆 2012-4-24 下午9:17:21
	 */
	protected URL getURLResource(String res) {
		try {
			if (!scanJarFilechecked(res)) {
				return null;
			}
			String _res = res;
			if (_res.startsWith("/")) {
				_res = res.substring(1);
			}
			if (classLoader == null) {
				if (loader != null) {
					classLoader = loader.getClassLoader();
				}
				if (classLoader == null) {
					return null;
				}
			}
			if (useResources) {
				_res = RESOURCES + _res;
			}
			Enumeration<URL> reses = classLoader.getResources(_res);
			if (reses.hasMoreElements()) {
				return reses.nextElement();
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	protected boolean scanJarFilechecked(String name) {
		for (String pattern : patterns) {
			if (pattern == null || "".equals(pattern)) {
				continue;
			}
			if (Matcher.matchPath(pattern, name, false)) {
				return true;
			}
		}
		return false;
	}

	protected List<String> tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return tokens;
	}
}
