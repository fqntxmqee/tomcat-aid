package org.apache.naming.resources.jar;

import javax.naming.directory.DirContext;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.aid.jar.JarFileDirContext;


/**
 * SupportLoadJarFileContextListener
 * 
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-4-24 下午4:24:23
 * @version
 */
public class SupportLoadJarFileContextListener implements LifecycleListener {

	private String		scanJarFileSuffix		= null;

	private String		pattern					= null;

	private boolean	useResources		= true;

	public synchronized void setScanJarFileSuffix(String scanJarFileSuffix) {
		this.scanJarFileSuffix = scanJarFileSuffix;
	}

	public synchronized void setAddPattern(String pattern) {
		this.pattern = pattern;
	}

	public synchronized void setUseResources(boolean useResources) {
		this.useResources = useResources;
	}

	/* (non-Javadoc)
	 * @see org.apache.catalina.LifecycleListener#lifecycleEvent(org.apache.catalina.LifecycleEvent)
	 */
	@Override
	public void lifecycleEvent(LifecycleEvent event) {
		if (Lifecycle.BEFORE_START_EVENT == event.getType()) {
			Lifecycle context = event.getLifecycle();
			if (context instanceof StandardContext) {
				StandardContext standardContext = (StandardContext) context;
				DirContext resources = new JarFileDirContext(standardContext, scanJarFileSuffix, pattern, useResources);
				standardContext.setResources(resources);
			}
		}
	}
}
