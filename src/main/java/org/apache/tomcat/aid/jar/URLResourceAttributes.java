package org.apache.tomcat.aid.jar;

import java.net.URL;
import java.util.Date;

import org.apache.naming.resources.ResourceAttributes;


/**
 * JarFileResourceAttributes
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-24 下午8:19:42
 * @version
 */
public class URLResourceAttributes extends ResourceAttributes {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7850634454417851600L;

	private URL						url					= null;

	public URLResourceAttributes(URL url) {
		this.url = url;
	}

	protected boolean	accessed			= false;

	protected String	canonicalPath	= null;

	@Override
	public boolean isCollection() {
		return false;
	}

	/**
	 * Get creation time.
	 * 
	 * @return creation time value
	 */
	@Override
	public long getCreation() {
		if (creation != -1L) {
			return creation;
		}
		creation = getLastModified();
		return creation;
	}

	/**
	 * Get creation date.
	 * 
	 * @return Creation date value
	 */
	@Override
	public Date getCreationDate() {
		if (creation == -1L) {
			creation = getCreation();
		}
		return super.getCreationDate();
	}

	/**
	 * Get last modified time.
	 * 
	 * @return lastModified time value
	 */
	@Override
	public long getLastModified() {
		if (lastModified != -1L) {
			return lastModified;
		}
		lastModified = 0L;
		return lastModified;
	}

	/**
	 * Get lastModified date.
	 * 
	 * @return LastModified date value
	 */
	@Override
	public Date getLastModifiedDate() {
		if (lastModified == -1L) {
			lastModified = getLastModified();
		}
		return super.getLastModifiedDate();
	}

	/**
	 * Get name.
	 * 
	 * @return Name value
	 */
	@Override
	public String getName() {
		if (name == null) {
			name = url.getPath();
		}
		return name;
	}
}
