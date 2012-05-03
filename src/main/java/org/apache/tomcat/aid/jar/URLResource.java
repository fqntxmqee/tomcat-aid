package org.apache.tomcat.aid.jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.naming.resources.Resource;


/**
 * URLResource
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-24 下午8:53:33
 */
public class URLResource extends Resource {

	public URLResource(URL url) {
		this.url = url;
	}

	/**
	 * Associated url object.
	 */
	protected URL	url;


	/**
	 * Content accessor.
	 * 
	 * @return InputStream
	 */
	@Override
	public InputStream streamContent() throws IOException {
		if (binaryContent == null) {
			InputStream is = url.openStream();
			inputStream = is;
			return is;
		}
		return super.streamContent();
	}
}
