package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

	// We will consider that ISBN identifies at the same time the book and the copy
	// as mentioned in the exercise description
	private Map<ISBN, Book> availableBooks = new HashMap<>();
	private Map<Book, LocalDate> borrowedBooks = new HashMap<>();

	public void addBooks(List<Book> books) {
		if (books == null) {
			throw new IllegalArgumentException("books parameter is null");
		}
		Map<ISBN, Book> newBookMap = books.stream().collect(Collectors.toMap(Book::getIsbn, book -> book));
		availableBooks = Stream.concat(availableBooks.entrySet().stream(), newBookMap.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldBook, newBook) -> newBook));
	}

	public Book findBook(long isbnCode) {
		return availableBooks.get(new ISBN(isbnCode));
	}
	
	public void removeBookBorrow(Book book) {
		if (book == null) {
			throw new IllegalArgumentException("Book parameter is null");
		}
		availableBooks.putIfAbsent(book.getIsbn(), book);
		borrowedBooks.remove(book);
	}

	public void saveBookBorrow(Book book, LocalDate borrowedAt) {
		if (book == null || borrowedAt == null) {
			throw new IllegalArgumentException("Either book parameter or borowedAt parameter is null");
		}
		borrowedBooks.putIfAbsent(book, borrowedAt);
		availableBooks.remove(book.getIsbn());
	}

	public LocalDate findBorrowedBookDate(Book book) {
		return borrowedBooks.get(book);
	}
}
