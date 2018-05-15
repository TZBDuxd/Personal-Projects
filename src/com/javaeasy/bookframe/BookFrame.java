package com.javaeasy.bookframe;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.javaeasy.bookstorage.Book;
import com.javaeasy.bookstorage.BookManager;

public class BookFrame extends JFrame {
	private BookManager bookManager;
	private JTable booksTable;
	private BookTableModel booksModel;
	private JButton delBtn;
	private JButton refreshBtn;

	public BookFrame() {
		String value = (String) JOptionPane.showInputDialog(this, "����������Դ������", "����������Դ�����֣�",
				JOptionPane.QUESTION_MESSAGE, null, null, "sqltest");
		try {
			bookManager = new BookManager(value);
		} catch (ClassNotFoundException e) {
			JOptionPane.showInputDialog(this, "�޷�����JDBC�����������˳���", "�޷�����JDBC����", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		} catch (SQLException e) {
			JOptionPane.showInputDialog(this, "�޷�������ر������˳���", "�޷����ش�����ر�", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		} catch (bookManagerException e) {
			JOptionPane.showInputDialog(this, "�޷��������ݿ����ӣ������˳���", "�޷��������ݿ�����", JOptionPane.ERROR_MESSAGE);
		}
		try {
			init();
		} catch (SQLException e) {
			// TODO: handle exception
			JOptionPane.showInputDialog(this, "�����ʼ������" + e.getMessage(), "�����ʼ������", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void init() throws SQLException {
		// TODO Auto-generated method stub
		this.setTitle("�鼮������");
		this.setSize(800, 300);
		this.locateFrameCenter();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		booksModel = new BookTableModel();
		booksModel.updataBooks(bookManager.queryAllBook());
		booksTable = new JTable(booksModel);
		booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		booksTable.getTableHeader().setReorderingAllowed(false);// �����û��Ƿ�����϶���ͷ���������������
		delBtn =new JButton("ɾ��");
		refreshBtn =new JButton("ˢ��");
		delBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				booksModel.removeBook();
			}
		});
		refreshBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				booksModel.reloadBooksModel();
			}
		});
		JScrollPane scroll = new JScrollPane(booksTable);
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout(7, 7));
		container.add(scroll, BorderLayout.CENTER);
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout());
		btnPanel.add(refreshBtn);
		btnPanel.add(delBtn);
		container.add(btnPanel, BorderLayout.SOUTH);

	}

	public void locateFrameCenter() {
		int frameWidth = this.getWidth();
		int frameHeight = this.getHeight();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		int screenWidth =screen.width;
		int screenHeight =screen.height;
		this.setLocation((screenWidth-frameWidth)/2, (screenHeight-frameHeight)/2);
	}

	private static final String[] columnNames = new String[] { "����", "����", "������", "����", "��ע" };

	class BookTableModel extends AbstractTableModel {
		private List<Book> books;

		public void updataBooks(List<Book> books) {
			this.books = books;
			Book book = new Book();
			book.newlyAdded = true;
			this.books.add(book);
			this.fireTableDataChanged();// ���±��
		}

		public void removeBook() {
			int row = booksTable.getSelectedRow();
			if (row < 0 && row >= books.size()) {
				JOptionPane.showMessageDialog(BookFrame.this, "��ѡ��Ҫɾ������", "��ѡ��Ҫɾ�����鼮", JOptionPane.ERROR_MESSAGE);
				return;
			}
			Book book = books.get(row);
			if (book.newlyAdded) {
				return;
			}
			try {
				bookManager.deleteBook(book);
			} catch (SQLException e) {
				// TODO: handle exception
				JOptionPane.showInputDialog(BookFrame.this, "ɾ���鼮����" + e.getMessage(), "ɾ���鼮����",
						JOptionPane.ERROR_MESSAGE);
			}
			reloadBooksModel();
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}
		
		public int getColumnCount(){
			return 5;
		}
		public int getRowCount() {
			return books.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (books.size() <= rowIndex) {
				return "";
			}
			Book book = books.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return book.bookName;
			case 1:
				return book.writer;
			case 2:
				return book.publisher;
			case 3:
				return book.bookType;
			case 4:
				return book.bookRemark;
			default:
				return "";
			}
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (books.size() <= rowIndex) {
				return;
			}
			Book book = books.get(rowIndex);
			switch (columnIndex) {
			case 0:
				book.bookName = (String) aValue;
				break;
			case 1:
				book.writer = (String) aValue;
				break;
			case 2:
				book.publisher = (String) aValue;
				break;
			case 3:
				book.bookType = (String) aValue;
			case 4:
				book.bookRemark = (String) aValue;
			default:
				return;
			}
			if (book.newlyAdded) {
				try {
					bookManager.addBook(book);
				} catch (SQLException e) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(BookFrame.this, "����鼮����" + e.getMessage(), "����鼮����",
							JOptionPane.ERROR_MESSAGE);
				}
			} else {
				try {
					bookManager.updateBook(book);
				} catch (SQLException e) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(BookFrame.this, "�����鼮����" + e.getMessage(), "�����鼮����",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			reloadBooksModel();
		}

		public void reloadBooksModel() {
			try {
				this.updataBooks(bookManager.queryAllBook());
			} catch (SQLException e) {
				// TODO: handle exception
				JOptionPane.showMessageDialog(BookFrame.this, "��ȡ�鼮����" + e.getMessage(), "��ȡ�鼮����",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void showFrame() {
		// TODO Auto-generated method stub
		
	}

}
