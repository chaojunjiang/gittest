package run;

import javax.sql.DataSource;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * GeneratorDemo
 */
public class GeneratorDemo {
	
	public static DataSource getDataSource() {
		Prop p = PropKit.use("config.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}
	
	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "com.demo.common.model.base";
		// base model 文件保存路径
		//String baseModelOutputDir = PathKit.getPath(new GeneratorDemo());
		//System.out.println(GeneratorDemo.class.getClassLoader().getResource("").getPath());
		String baseModelOutputDir = PathKit.getWebRootPath() + "/Generator/src/com/demo/common/model/base";
		//System.out.println(PathKit.getWebRootPath()); //获取项目空间路径：D:\eclipsespace
		//System.out.println(PathKit.getRootClassPath());//获取class文件的bin路径：D:\eclipsespace\Generator\bin
		//System.out.println(PathKit.getPackagePath(new GeneratorDemo()));//获取该类的包路径：com/demo/common/model
		//System.out.println(PathKit.getPath(new GeneratorDemo()));//D:\eclipsespace\Generator\bin\com\demo\common\model
		//System.out.println(PathKit.getPath(GeneratorDemo.class));//D:\eclipsespace\Generator\bin\com\demo\common\model
		
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "com.demo.common.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";
		
		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
		// 设置数据库方言
		gernerator.setDialect(new MysqlDialect());
		// 添加不需要生成的表名
		String[] unIncludeTable = {"",""};
		gernerator.addExcludedTable(unIncludeTable);
		// 设置是否在 Model 中生成 dao 对象:public static final Sqoopy dao = new Sqoopy();
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
		gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
	}
}
