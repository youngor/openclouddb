package org.opencloudb.sqlexecute;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;

/**
 * base sql test cases,some data should in database first
 * 
 * @author wuzhih
 * 
 */
public class BaseSQLExeTest {

	private static Connection getCon(String url, String user, String passwd)
			throws SQLException {
		Connection theCon = DriverManager.getConnection(url, user, passwd);
		return theCon;
	}

	private static void testMultiNodeNormalSQL(Connection theCon)
			throws SQLException {
		theCon.setAutoCommit(true);
		System.out.println("testMultiNodeNormalSQL begin");
		String[] sqls = {
				"select * from travelrecord where id=1",
				"select * from travelrecord limit 100",
				"select sum(fee) total_fee, days,count(id),max(fee),min(fee) from  travelrecord  group by days  order by days desc limit 99 ",
				"update travelrecord set user=user where id =1",
				"delete from travelrecord where id =1 ",
				"insert into travelrecord (id,user,traveldate,fee,days) values(1,'wang','2014-01-05',510.5,3)"};
		Statement stmt = theCon.createStatement();
		for (String sql : sqls) {
			stmt.execute(sql);
		}
		theCon.setAutoCommit(false);
		for (String sql : sqls) {
			stmt.execute(sql);
		}
		theCon.commit();
		System.out.println("testMultiNodeNormalSQL end");
	}

	private static void testSingleNodeNormalSQL(Connection theCon)
			throws SQLException {
		theCon.setAutoCommit(true);
		System.out.println("testSingleNodeNormalSQL begin");
		String[] sqls = {
				"select * from company limit 100",
				"select name,count(id),max(id),min(id) from company group by name order by name desc limit 100 ",
				"update company set name=name where id =1",
				"delete from company where id =-1 ",
				"delete from company where id =1 ",
				"insert into company(id,name) values(1,'hp')" };
		Statement stmt = theCon.createStatement();
		for (String sql : sqls) {
			System.out.println("execute "+sql);
			stmt.execute(sql);
		}
		theCon.setAutoCommit(false);
		for (String sql : sqls) {
			stmt.execute(sql);
		}
		theCon.commit();
		System.out.println("testSingleNodeNormalSQL end");
	}

	private static void testBadSQL(Connection theCon) throws SQLException {
		System.out.println("testBadSQL begin");
		theCon.setAutoCommit(true);
		String[] sqls = {
				"select a ,id,name from company limit 1",
				"update company set name=name where id =-1",
				"insert into company(id,name) values(1,'hp')",
				"insert into company(id,name,badname) values(1,'hp')",
				"insert into travelrecord (id,user,traveldate,fee,days) values(1,’wang’,’2014-01-05’,510.5,3)",
				"insert into travelrecord (id,user,traveldate,fee,days,badcolumn) values(1,’wang’,’2014-01-05’,510.5,3)",
				"select sum(fee) total_fee, days,count(id),max(fee),min(fee) from  travelrecord  group by count(id)  order by count(id) desc limit 99 " };
		for (String sql : sqls) {
			try {
				theCon.createStatement().executeQuery(sql);
			} catch (Exception e) {
				// e.printStackTrace();
				Assert.assertEquals(true, e != null);
			}
		}

		System.out.println("testBadSQL passed");
	}

	private static void testTransaction(Connection theCon) throws SQLException {
		System.out.println("testTransaction begin");
		theCon.setAutoCommit(false);
		String sql = "select id,name from company limit 1";
		String oldName = null;
		String upSQL = null;
		ResultSet rs = theCon.createStatement().executeQuery(sql);
		if (rs.next()) {
			long id = rs.getLong(1);
			oldName = rs.getString(2);
			upSQL = "update company set name='updatedname' where id=" + id;
			// System.out.println(sql);
		}
		int count = theCon.createStatement().executeUpdate(upSQL);
		Assert.assertEquals(true, count > 0);
		theCon.rollback();
		rs = theCon.createStatement().executeQuery(sql);
		String newName = null;
		if (rs.next()) {
			newName = rs.getString(2);
		}
		Assert.assertEquals(true, oldName.equals(newName));
		System.out.println("testTransaction passed");
	}

	private static void closeCon(Connection theCon) {
		try {
			theCon.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		if (args.length < 3) {
			System.out
					.println("input param,format: [jdbcurl] [user] [password]   ");
			return;
		}
		String url = args[0];
		String user = args[1];
		String password = args[2];
		Connection theCon = null;
		
		theCon = null;
		try {
			theCon = getCon(url, user, password);
			testBadSQL(theCon);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeCon(theCon);
		}
		theCon = null;
		try {
			theCon = getCon(url, user, password);
			testSingleNodeNormalSQL(theCon);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeCon(theCon);
		}
		try {
			theCon = getCon(url, user, password);
			testTransaction(theCon);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeCon(theCon);
		}
		theCon = null;
		try {
			theCon = getCon(url, user, password);
			testMultiNodeNormalSQL(theCon);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeCon(theCon);
		}
	}

}
