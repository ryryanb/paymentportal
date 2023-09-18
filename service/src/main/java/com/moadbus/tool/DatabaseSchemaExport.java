package tool;

import java.io.File;
import java.util.EnumSet;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.TargetType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class DatabaseSchemaExport {
	/*
	 * For hibernate 5
	 */	
	public static void execute(String dialect, String output,String filename, String classPathPrefix, boolean create, boolean format) throws Exception {

        File outputDir = new File(output);
        outputDir.mkdirs();
        
	    MetadataSources metadata = new MetadataSources(
	            new StandardServiceRegistryBuilder()
	                    .applySetting("hibernate.dialect",dialect)
	                    .build());   
	    
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String []packages = classPathPrefix.split(",");   
        for(String p: packages ){
        
        	Resource[] resources = resourcePatternResolver.getResources("classpath*:" + p.replace(".", "/")
                + "/*.class");
	        for (Resource resource : resources) {
	            String className = resource.getFilename();
	            className = className.substring(0, className.length() - ".class".length());
	            metadata.addAnnotatedClass(Class.forName(p + "." + className));
	        }
	    }
   

        File out = new File(outputDir, filename);// "schema.sql");
       //SchemaExport schemaExport = new SchemaExport((MetadataImplementor) metadata.buildMetadata());
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile(out.getPath());
        schemaExport.setFormat(format);
        schemaExport.setDelimiter(";");       
        schemaExport.execute( EnumSet.of(TargetType.SCRIPT), SchemaExport.Action.CREATE, metadata.buildMetadata());
    }

	/**
	 * Entry point for <code>pom.xml</code>.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		execute(args[0],args[1], args[2],args[3],Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5]));
	}
}
