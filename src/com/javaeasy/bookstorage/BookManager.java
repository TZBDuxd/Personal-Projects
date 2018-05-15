package com.javaeasy.bookstorage;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.javaeasy.bookframe.bookManagerException;


public class BookManager {
	Connection conn;
	PreparedStatement insertState;
	PreparedStatement deleteState;
	PreparedStatement updateState;
	PreparedStatement queryAllState;
	/**
	 * ���ݿ����
	 */
	private static final String BOOK_TABLE_NAME = "mybooks";
	/**
	 * ����mybooks���SQL���
	 */
	private static final String CREATE_TABLE_SQL = " create table " /* if not exists*/ + BOOK_TABLE_NAME
			+ " (bookName varchar(200) primary key, writer varchar(200), "
			+ " publisher varchar(200), bookType varchar(200), bookRemark varchar(200))";
		

	/**
	 * ���췽�����������ݿ�
	 * 
	 * @param datasourceName
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	public BookManager(String datasourceName) throws ClassNotFoundException, SQLException, bookManagerException {
		String user = "root";
		String password = "root";
		String url = "jdbc:mysql://localhost:3306/" + datasourceName
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT&allowPublicKeyRetrieval=true";
		String driver = "com.mysql.cj.jdbc.Driver";
		Class.forName(driver);
		conn = DriverManager.getConnection(url, user, password);
		if (conn == null) 
		{
			throw new bookManagerException("���ݿ�����ʧ�ܣ�");
		}
		conn.setAutoCommit(true);
		createBookTable();
		initStatement();
	}

	/**
	 * ������
	 * 
	 * @throws SQLException
	 */
	private void createBookTable() throws SQLException {
		// TODO Auto-generated method stub
		DatabaseMetaData metaData = conn.getMetaData();
		ResultSet rs = metaData.getTables(null, null, null, new String[] { "TABLE" });
		boolean hasTable = false;
		while (rs.next()) {
			String tableName = rs.getString(3);
			if (BOOK_TABLE_NAME.equals(tableName)) {
				hasTable = true;
			}
		}
		if (hasTable) {
			return;
		}
		Statement state = conn.createStatement();
		state.executeUpdate(CREATE_TABLE_SQL);
		state.close();
	}

	/**
	 * ��ʼ����
	 * 
	 * @throws SQLException
	 */
	private void initStatement() throws SQLException {
		// TODO Auto-generated method stub
		insertState = conn.prepareStatement("insert into " + BOOK_TABLE_NAME + " values(?,?,?,?,?) ");
		deleteState = conn.prepareStatement("delete from " + BOOK_TABLE_NAME + " where bookName=?" );
		updateState = conn.prepareStatement("update " + BOOK_TABLE_NAME + " set bookName=?, writer=?, publisher=?, "
				+ " bookType=?, bookRemark=? where bookName=?");
		queryAllState = conn.prepareStatement("select * from " + BOOK_TABLE_NAME);
	}

	/**
	 * ���ͼ�鷽��
	 * 
	 * @param book
	 * @throws SQLException
	 */
	public void addBook(Book book) throws SQLException {
		insertState.setString(1, book.bookName);
		insertState.setString(2, book.writer);
		insertState.setString(3, book.publisher);
		insertState.setString(4, book.bookType);
		insertState.setString(5, book.bookRemark);
		insertState.executeUpdate();
	}

	/**
	 * ɾ��ͼ��
	 * 
	 * @param book
	 * @throws SQLException
	 */
	public void deleteBook(Book book) throws SQLException {
		deleteState.setString(1, book.bookName);
		deleteState.executeUpdate();
	}

	/**
	 * ����ͼ��
	 * 
	 * @param book
	 * @throws SQLException
	 */
	public void updateBook(Book book) throws SQLException {
		updateState.setString(1, book.bookName);
		updateState.setString(2, book.writer);
		updateState.setString(3, book.publisher);
		updateState.setString(4, book.bookType);
		updateState.setString(5, book.bookRemark);
		updateState.setString(6, book.oldBookName);
		updateState.executeUpdate();
	}

	/**
	 * ��ѯͼ��
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Book> queryAllBook() throws SQLException {
		ResultSet rs = queryAllState.executeQuery();
		List<Book> books = new ArrayList<Book>();
		while (rs.next()) {
			Book book = new Book();
			String name = rs.getString(1);
			book.bookName = name;
			book.oldBookName = name;
			book.writer = rs.getString(2);
			book.publisher = rs.getString(3);
			book.bookType = rs.getString(4);
			book.bookRemark = rs.getString(5);
			book.newlyAdded = false;
			books.add(book);
		}
		return books;

	}

}
