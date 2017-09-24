package nz.ac.auckland.concert.service.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that provides methods for working with a database. This class
 * interacts with the database using plain SQL, and doesn't use any ORM
 * functionality.
 */
public class DatabaseUtility {

	public static final String DATABASE_DRIVER_NAME = "org.h2.Driver";
	public static final String DATABASE_URL = "jdbc:h2:~/test;mv_store=false";
	public static final String DATABASE_USERNAME = "sa";
	public static final String DATABASE_PASSWORD = "sa";

	private static Logger _logger = LoggerFactory
			.getLogger(DatabaseUtility.class);

	// JDBC connection to the database.
	private static Connection _jdbcConnection = null;

	/**
	 * Creates a connection to the database.
	 *
	 * @throws ClassNotFoundException if the named database driver class can't
	 *                                be located.
	 * @throws SQLException           if there is an error with connecting to the
	 *                                database.
	 */
	public static void openDatabase() throws ClassNotFoundException,
			SQLException {

		// Load H2 database driver class.
		Class.forName("org.h2.Driver");

		// Open a connection to the database.
		_jdbcConnection = DriverManager.getConnection(
				DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
	}

	/**
	 * Closes the database connection.
	 *
	 * @throws SQLException if an error is encountered closing the connection.
	 */
	public static void closeDatabase() throws SQLException {
		if (_jdbcConnection != null) {
			_jdbcConnection.close();
		}
		_jdbcConnection = null;
	}

	/**
	 * Deletes rows from existing tables and optionally drops tables from the
	 * database.
	 *
	 * @param dropTables set to true to drop existing tables, false to only clear
	 *                   content from the tables.
	 * @throws IllegalStateException if the database connection hasn't been
	 *                               opened.
	 * @throws SQLException          if there's an error clearing database.
	 */
	public static void clearDatabase(boolean dropTables) throws IllegalStateException, SQLException {
		Statement s = _jdbcConnection.createStatement();
		s.execute("SET REFERENTIAL_INTEGRITY FALSE");

		Set<String> tables = new HashSet<String>();
		ResultSet rs = s.executeQuery("select table_name "
				+ "from INFORMATION_SCHEMA.tables "
				+ "where table_type='TABLE' and table_schema='PUBLIC'");

		while (rs.next()) {
			tables.add(rs.getString(1));
		}
		rs.close();
		for (String table : tables) {
			_logger.debug("Deleting content from " + table);
			s.executeUpdate("DELETE FROM " + table);
			if (dropTables) {
				s.executeUpdate("DROP TABLE " + table);
			}
		}

		s.execute("SET REFERENTIAL_INTEGRITY TRUE");
		s.close();
	}

	/**
	 * Executes a SQL statement.
	 *
	 * @param sqlStatement the SQL statement to execute.
	 * @throws IllegalStateException if the database connection hasn't been
	 *                               opened.
	 * @throws SQLException          if there's an error executing the SQL statement.
	 */
	public static void executeStatement(String sqlStatement) throws IllegalStateException, SQLException {
		if (_jdbcConnection == null) {
			throw new IllegalStateException();
		}

		Statement stmt = _jdbcConnection.createStatement();
		stmt.execute(sqlStatement);
	}

	/**
	 * Executes a SQL query and returns a ResultSet storing the result of the
	 * query.
	 *
	 * @param sqlQuery the SQL query to execute.
	 * @throws IllegalStateException if the database connection hasn't been
	 *                               opened.
	 * @throws SQLException          if there's an error executing the SQL query.
	 */
	public static ResultSet executeQuery(String sqlQuery) throws IllegalStateException, SQLException {
		if (_jdbcConnection == null) {
			throw new IllegalStateException();
		}

		Statement stmt = _jdbcConnection.createStatement();
		ResultSet resultSet = stmt.executeQuery(sqlQuery);

		return resultSet;
	}

	/**
	 * Logs the contents of the database using the configured logger. This
	 * method outputs the contents of tables that contain at least one row
	 * (there is no output for empty tables).
	 *
	 * @param tableNames an optional Set of table names. If this parameter is
	 *                   supplied, only the non-empty tables named in the Set are output.
	 * @throws IllegalStateException if the database connection hasn't been
	 *                               opened.
	 * @throws SQLException          if there's an error reading from the database.
	 */
	public static void dumpDatabase(Set<String> tableNames) throws IllegalStateException, SQLException {
		if (_jdbcConnection == null) {
			throw new IllegalStateException();
		}

		// Run a query to obtain table names.
		Statement s = _jdbcConnection.createStatement();
		ResultSet rs = s.executeQuery("select table_name "
				+ "from INFORMATION_SCHEMA.tables "
				+ "where table_type='TABLE' and table_schema='PUBLIC'");

		// Iterate over each table.
		while (rs.next()) {
			String tableName = rs.getString(1);

			if (tableNames != null && (!tableNames.contains(tableName))) {
				// Skip this table.
				continue;
			}

			// Run a query to extract all rows and column values for the table.
			// Allow the ResultSet's cursor to be returned to the beginning of
			// the result set, so the results can be parsed twice, first to
			// get data length data for formatting purposes, and, second to
			// read the table content.
			Statement query = _jdbcConnection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet tableRows = query.executeQuery("select * from "
					+ tableName);


			// A ResultSetMetaData object stores table metadata.
			ResultSetMetaData metadata = tableRows.getMetaData();

			// Use the ResultSetMetaData object to find out how many columns
			// are in the table and what their names (labels) are.
			// Record the length of each column label in the columnWidths array
			// (note the first element of the array is not used).
			int columnWidths[] = new int[metadata.getColumnCount() + 1];
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				columnWidths[i] = metadata.getColumnLabel(i).length();
			}

			// Find, for each column, the longest value for any row. This data
			// is required for formatting purposes.
			int rowCount = 0;
			while (tableRows.next()) {
				rowCount++;
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					int columnDataLength = 4; // 4 characters to store "null".

					try {
						columnDataLength = tableRows.getString(i).length();
					} catch (NullPointerException e) {
						// No action necessary - use the default 4 characters.
					}
					if (columnDataLength > columnWidths[i]) {
						columnWidths[i] = columnDataLength;
					}
				}
			}
			if (rowCount == 0) {
				// Table is empty - don't process it further.
				continue;
			}

			// Reset cursor for the second parse.
			tableRows.beforeFirst();

			// Output table header.
			_logger.debug("==");
			_logger.debug("Table: " + tableName);

			// Output column names (labels) with any padding to format the
			// output.
			StringBuffer columnLabels = new StringBuffer();
			for (int i = 1; i <= metadata.getColumnCount(); i++) {
				String columnLabel = metadata.getColumnLabel(i);
				columnLabels.append(columnLabel);

				int padding = columnWidths[i] - columnLabel.length() + 1;
				for (int j = 0; j < padding; j++) {
					columnLabels.append(" ");
				}
			}
			_logger.debug(columnLabels.toString());

			// Output table row data, adding padding as necessary.
			while (tableRows.next()) {
				StringBuffer columnValues = new StringBuffer();
				for (int i = 1; i <= metadata.getColumnCount(); i++) {
					String columnValue = tableRows.getString(i);
					if (columnValue == null) {
						columnValue = "null";
					}
					columnValues.append(columnValue);

					int padding = columnWidths[i] - columnValue.length() + 1;
					for (int j = 0; j < padding; j++) {
						columnValues.append(" ");
					}
				}
				_logger.debug(columnValues.toString());
			}
			_logger.debug("==\n");

			query.close();
		}
		rs.close();
	}
}
