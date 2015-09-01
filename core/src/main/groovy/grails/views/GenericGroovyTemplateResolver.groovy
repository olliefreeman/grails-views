package grails.views

import grails.util.BuildSettings
import groovy.text.Template
import groovy.transform.CompileStatic
import org.grails.io.support.GrailsResourceUtils

/**
 * A generic TemplateResolver for resolving Groovy templates that are compiled into classes
 *
 * @author Graeme Rocher
 */
@CompileStatic
class GenericGroovyTemplateResolver implements TemplateResolver {
    /**
     * The base directory to load templates in development mode
     */
    public static final char SLASH_CHAR = '/' as char
    public static final char DOT_CHAR = '.' as char
    public static final char UNDERSCORE_CHAR = '_' as char


    File baseDir = BuildSettings.BASE_DIR ? new File(BuildSettings.BASE_DIR, GrailsResourceUtils.VIEWS_DIR_PATH) : null

    /**
     * The base package to load templates as classes in production mode
     */
    String packageName = ""

    /**
     * The class loader to use for template loading in production mode
     */
    ClassLoader classLoader = Thread.currentThread().contextClassLoader

    @Override
    URL resolveTemplate(String path) {
        if(baseDir != null) {
            def f = new File(baseDir, path)
            if(f.exists()) {
                return f.toURI().toURL()
            }
        }
        return null
    }

    @Override
    Class<? extends Template> resolveTemplateClass(String path) {
        resolveTemplateClass(packageName, path)
    }

    @Override
    Class<? extends Template> resolveTemplateClass(String packageName, String path) {
        path = resolveTemplateName(packageName, path)
        try {
            def cls = classLoader.loadClass(path)
            return (Class<? extends Template>)cls
        } catch (Throwable e) {
        }
        return null
    }

    public static String resolveTemplateName(String scope, String path) {
        path = path.substring(1) // remove leading slash '/'
        path = path.replace(SLASH_CHAR, UNDERSCORE_CHAR)
        path = path.replace(DOT_CHAR, UNDERSCORE_CHAR)
        if(scope) {
            scope = scope.replaceAll(/[\W\s]/, String.valueOf(UNDERSCORE_CHAR))
            path = "${scope}_${path}"
        }
        return path
    }
}
