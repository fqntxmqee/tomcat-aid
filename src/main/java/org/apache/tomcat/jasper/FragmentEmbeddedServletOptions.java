
package org.apache.tomcat.jasper;

import java.io.File;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.jasper.Options;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.TldLocationsCache;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * @author <a href="mailto:guoqing.huang@foxmail.com">黄国庆</a>
 * @since 2012-5-8 上午9:38:40
 * @version
 */
public class FragmentEmbeddedServletOptions implements Options {

	private Log						log											= LogFactory.getLog(FragmentEmbeddedServletOptions.class);

	private Properties			settings										= new Properties();

	private boolean				development									= true;

	public boolean					fork											= true;

	private boolean				keepGenerated								= true;

	private boolean				trimSpaces									= false;

	private boolean				isPoolingEnabled							= true;

	private boolean				mappedFile									= true;

	private boolean				classDebugInfo								= true;

	private int						checkInterval								= 0;

	private boolean				isSmapSuppressed							= false;

	private boolean				isSmapDumped								= false;

	private boolean				genStringAsCharArray						= false;

	private boolean				errorOnUseBeanInvalidClassAttribute	= true;

	private File					scratchDir;

	private String					ieClassId									= "clsid:8AD9C840-044E-11D1-B3E9-00805F499D93";

	private String					classpath									= null;

	private String					compiler										= null;

	private String					compilerTargetVM							= "1.5";

	private String					compilerSourceVM							= "1.5";

	private String					compilerClassName							= null;

	private TldLocationsCache	tldLocationsCache							= null;

	private JspConfig				jspConfig									= null;

	private TagPluginManager	tagPluginManager							= null;

	private String					javaEncoding								= "UTF8";

	private int						modificationTestInterval				= 4;

	private boolean				recompileOnFail							= false;

	private boolean				xpoweredBy;

	private boolean				displaySourceFragment					= true;

	public String getProperty(String name) {
		return this.settings.getProperty(name);
	}

	public void setProperty(String name, String value) {
		if ((name != null) && (value != null)) {
			this.settings.setProperty(name, value);
		}
	}

	@Override
	public boolean getKeepGenerated() {
		return this.keepGenerated;
	}

	@Override
	public boolean getTrimSpaces() {
		return this.trimSpaces;
	}

	@Override
	public boolean isPoolingEnabled() {
		return this.isPoolingEnabled;
	}

	@Override
	public boolean getMappedFile() {
		return this.mappedFile;
	}

	@Override
	@Deprecated
	public boolean getSendErrorToClient() {
		return true;
	}

	@Override
	public boolean getClassDebugInfo() {
		return this.classDebugInfo;
	}

	@Override
	public int getCheckInterval() {
		return this.checkInterval;
	}

	@Override
	public int getModificationTestInterval() {
		return this.modificationTestInterval;
	}

	@Override
	public boolean getRecompileOnFail() {
		return this.recompileOnFail;
	}

	@Override
	public boolean getDevelopment() {
		return this.development;
	}

	@Override
	public boolean isSmapSuppressed() {
		return this.isSmapSuppressed;
	}

	@Override
	public boolean isSmapDumped() {
		return this.isSmapDumped;
	}

	@Override
	public boolean genStringAsCharArray() {
		return this.genStringAsCharArray;
	}

	@Override
	public String getIeClassId() {
		return this.ieClassId;
	}

	@Override
	public File getScratchDir() {
		return this.scratchDir;
	}

	@Override
	public String getClassPath() {
		return this.classpath;
	}

	@Override
	public boolean isXpoweredBy() {
		return this.xpoweredBy;
	}

	@Override
	public String getCompiler() {
		return this.compiler;
	}

	@Override
	public String getCompilerTargetVM() {
		return this.compilerTargetVM;
	}

	@Override
	public String getCompilerSourceVM() {
		return this.compilerSourceVM;
	}

	@Override
	public String getCompilerClassName() {
		return this.compilerClassName;
	}

	@Override
	public boolean getErrorOnUseBeanInvalidClassAttribute() {
		return this.errorOnUseBeanInvalidClassAttribute;
	}

	public void setErrorOnUseBeanInvalidClassAttribute(boolean b) {
		this.errorOnUseBeanInvalidClassAttribute = b;
	}

	@Override
	public TldLocationsCache getTldLocationsCache() {
		return this.tldLocationsCache;
	}

	public void setTldLocationsCache(TldLocationsCache tldC) {
		this.tldLocationsCache = tldC;
	}

	@Override
	public String getJavaEncoding() {
		return this.javaEncoding;
	}

	@Override
	public boolean getFork() {
		return this.fork;
	}

	@Override
	public JspConfig getJspConfig() {
		return this.jspConfig;
	}

	@Override
	public TagPluginManager getTagPluginManager() {
		return this.tagPluginManager;
	}

	@Override
	public boolean isCaching() {
		return false;
	}

	@Override
	public Map<?, ?> getCache() {
		return null;
	}

	@Override
	public boolean getDisplaySourceFragment() {
		return this.displaySourceFragment;
	}

	public FragmentEmbeddedServletOptions(ServletConfig config,
				ServletContext context) {
		Enumeration<?> enumeration = config.getInitParameterNames();
		while (enumeration.hasMoreElements()) {
			String k = (String) enumeration.nextElement();
			String v = config.getInitParameter(k);
			setProperty(k, v);
		}
		String validating = config.getInitParameter("validating");
		if ("false".equals(validating)) {
			org.apache.jasper.xmlparser.ParserUtils.validating = false;
		}
		String keepgen = config.getInitParameter("keepgenerated");
		if (keepgen != null) {
			if (keepgen.equalsIgnoreCase("true")) {
				this.keepGenerated = true;
			} else if (keepgen.equalsIgnoreCase("false")) {
				this.keepGenerated = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.keepgen"));
			}
		}
		String trimsp = config.getInitParameter("trimSpaces");
		if (trimsp != null) {
			if (trimsp.equalsIgnoreCase("true")) {
				this.trimSpaces = true;
			} else if (trimsp.equalsIgnoreCase("false")) {
				this.trimSpaces = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.trimspaces"));
			}
		}
		this.isPoolingEnabled = true;
		String poolingEnabledParam = config.getInitParameter("enablePooling");
		if ((poolingEnabledParam != null)
					&& (!poolingEnabledParam.equalsIgnoreCase("true"))) {
			if (poolingEnabledParam.equalsIgnoreCase("false")) {
				this.isPoolingEnabled = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.enablePooling"));
			}
		}
		String mapFile = config.getInitParameter("mappedfile");
		if (mapFile != null) {
			if (mapFile.equalsIgnoreCase("true")) {
				this.mappedFile = true;
			} else if (mapFile.equalsIgnoreCase("false")) {
				this.mappedFile = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.mappedFile"));
			}
		}
		String debugInfo = config.getInitParameter("classdebuginfo");
		if (debugInfo != null) {
			if (debugInfo.equalsIgnoreCase("true")) {
				this.classDebugInfo = true;
			} else if (debugInfo.equalsIgnoreCase("false")) {
				this.classDebugInfo = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.classDebugInfo"));
			}
		}
		String checkInterval = config.getInitParameter("checkInterval");
		if (checkInterval != null) {
			try {
				this.checkInterval = Integer.parseInt(checkInterval);
			} catch (NumberFormatException ex) {
				if (this.log.isWarnEnabled()) {
					this.log.warn(Localizer.getMessage("jsp.warning.checkInterval"));
				}
			}
		}
		String modificationTestInterval = config.getInitParameter("modificationTestInterval");
		if (modificationTestInterval != null) {
			try {
				this.modificationTestInterval = Integer.parseInt(modificationTestInterval);
			} catch (NumberFormatException ex) {
				if (this.log.isWarnEnabled()) {
					this.log.warn(Localizer.getMessage("jsp.warning.modificationTestInterval"));
				}
			}
		}
		String recompileOnFail = config.getInitParameter("recompileOnFail");
		if (recompileOnFail != null) {
			if (recompileOnFail.equalsIgnoreCase("true")) {
				this.recompileOnFail = true;
			} else if (recompileOnFail.equalsIgnoreCase("false")) {
				this.recompileOnFail = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.recompileOnFail"));
			}
		}
		String development = config.getInitParameter("development");
		if (development != null) {
			if (development.equalsIgnoreCase("true")) {
				this.development = true;
			} else if (development.equalsIgnoreCase("false")) {
				this.development = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.development"));
			}
		}
		String suppressSmap = config.getInitParameter("suppressSmap");
		if (suppressSmap != null) {
			if (suppressSmap.equalsIgnoreCase("true")) {
				this.isSmapSuppressed = true;
			} else if (suppressSmap.equalsIgnoreCase("false")) {
				this.isSmapSuppressed = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.suppressSmap"));
			}
		}
		String dumpSmap = config.getInitParameter("dumpSmap");
		if (dumpSmap != null) {
			if (dumpSmap.equalsIgnoreCase("true")) {
				this.isSmapDumped = true;
			} else if (dumpSmap.equalsIgnoreCase("false")) {
				this.isSmapDumped = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.dumpSmap"));
			}
		}
		String genCharArray = config.getInitParameter("genStrAsCharArray");
		if (genCharArray != null) {
			if (genCharArray.equalsIgnoreCase("true")) {
				this.genStringAsCharArray = true;
			} else if (genCharArray.equalsIgnoreCase("false")) {
				this.genStringAsCharArray = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.genchararray"));
			}
		}
		String errBeanClass = config.getInitParameter("errorOnUseBeanInvalidClassAttribute");
		if (errBeanClass != null) {
			if (errBeanClass.equalsIgnoreCase("true")) {
				this.errorOnUseBeanInvalidClassAttribute = true;
			} else if (errBeanClass.equalsIgnoreCase("false")) {
				this.errorOnUseBeanInvalidClassAttribute = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.errBean"));
			}
		}
		String ieClassId = config.getInitParameter("ieClassId");
		if (ieClassId != null) {
			this.ieClassId = ieClassId;
		}
		String classpath = config.getInitParameter("classpath");
		if (classpath != null) {
			this.classpath = classpath;
		}
		String dir = config.getInitParameter("scratchdir");
		if (dir != null) {
			this.scratchDir = new File(dir);
		} else {
			this.scratchDir = ((File) context.getAttribute("javax.servlet.context.tempdir"));
			if (this.scratchDir == null) {
				dir = System.getProperty("java.io.tmpdir");
				if (dir != null) {
					this.scratchDir = new File(dir);
				}
			}
		}
		if (this.scratchDir == null) {
			this.log.fatal(Localizer.getMessage("jsp.error.no.scratch.dir"));
			return;
		}
		if ((!this.scratchDir.exists())
					|| (!this.scratchDir.canRead())
					|| (!this.scratchDir.canWrite())
					|| (!this.scratchDir.isDirectory())) {
			this.log.fatal(Localizer.getMessage("jsp.error.bad.scratch.dir",
				this.scratchDir.getAbsolutePath()));
		}
		this.compiler = config.getInitParameter("compiler");
		String compilerTargetVM = config.getInitParameter("compilerTargetVM");
		if (compilerTargetVM != null) {
			this.compilerTargetVM = compilerTargetVM;
		}
		String compilerSourceVM = config.getInitParameter("compilerSourceVM");
		if (compilerSourceVM != null) {
			this.compilerSourceVM = compilerSourceVM;
		}
		String javaEncoding = config.getInitParameter("javaEncoding");
		if (javaEncoding != null) {
			this.javaEncoding = javaEncoding;
		}
		String compilerClassName = config.getInitParameter("compilerClassName");
		if (compilerClassName != null) {
			this.compilerClassName = compilerClassName;
		}
		String fork = config.getInitParameter("fork");
		if (fork != null) {
			if (fork.equalsIgnoreCase("true")) {
				this.fork = true;
			} else if (fork.equalsIgnoreCase("false")) {
				this.fork = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.fork"));
			}
		}
		String xpoweredBy = config.getInitParameter("xpoweredBy");
		if (xpoweredBy != null) {
			if (xpoweredBy.equalsIgnoreCase("true")) {
				this.xpoweredBy = true;
			} else if (xpoweredBy.equalsIgnoreCase("false")) {
				this.xpoweredBy = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.xpoweredBy"));
			}
		}
		String displaySourceFragment = config.getInitParameter("displaySourceFragment");
		if (displaySourceFragment != null) {
			if (displaySourceFragment.equalsIgnoreCase("true")) {
				this.displaySourceFragment = true;
			} else if (displaySourceFragment.equalsIgnoreCase("false")) {
				this.displaySourceFragment = false;
			} else if (this.log.isWarnEnabled()) {
				this.log.warn(Localizer.getMessage("jsp.warning.displaySourceFragment"));
			}
		}
		this.tldLocationsCache = (TldLocationsCache) context.getAttribute(TldLocationsCache.class.getName());
		if (this.tldLocationsCache == null) {
			this.tldLocationsCache = new TldLocationsCache(context);
		}
		this.jspConfig = new JspConfig(context);
		this.tagPluginManager = new TagPluginManager(context);
	}
}
