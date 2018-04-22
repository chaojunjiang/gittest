import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.Dialect;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.c3p0.C3p0Plugin;


public class Test {

	public static void main(String[] args) {
		try {
			new Test().run();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void run() throws SQLException {
		DataSource dataSource = getDataSource();
		Connection conn = dataSource.getConnection();
		DatabaseMetaData dbMeta = conn.getMetaData();
		Dialect dialect = new MysqlDialect();
		String schemaPattern = dialect instanceof OracleDialect ? dbMeta.getUserName() : null;
		ResultSet tablesNameResultSet = dbMeta.getTables(conn.getCatalog(), schemaPattern, null, new String[]{"TABLE", "VIEW"});
		while (tablesNameResultSet.next()) {
			//遍历获取当前数据库的表名称
			String tableName = tablesNameResultSet.getString("TABLE_NAME");
			//System.out.println(tableName);
			//根据表名称查看表的信息
			String sql = dialect.forTableBuilderDoBuild(tableName);
			Statement stm = conn.createStatement();
			ResultSet tableInfoResultSet = stm.executeQuery(sql);
			ResultSetMetaData rsmd = tableInfoResultSet.getMetaData();
			int numberColumns = rsmd.getColumnCount();
//			System.out.println("表"+tableName+"中的字段个数："+numberOfColumns);
			HashMap<String, Integer> columnInfos = new HashMap<String, Integer>();//保存每个表的字段和字段类型数据
			for (int i = 1; i <= numberColumns; i++) {
				String columnName = rsmd.getColumnName(i);
				int columnType = rsmd.getColumnType(i);
				columnInfos.put(columnName, columnType);
			}
			System.out.println(tableName+"表信息："+columnInfos);
			/**
			 * int对应的数据库中的类型
				 public static final int BLOB 2004 
				 public static final int BOOLEAN 16 
				 public static final int CHAR 1 
				 public static final int CLOB 2005 
				 public static final int DATE 91 
				 public static final int DOUBLE 8 
				 public static final int FLOAT 6 
				 public static final int INTEGER 4 
				 public static final int VARCHAR 12 
			 */
		}
	}
	
	public static DataSource getDataSource() {
		Prop p = PropKit.use("config.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}

}
