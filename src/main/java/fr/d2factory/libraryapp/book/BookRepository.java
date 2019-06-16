package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Triplet;

import fr.d2factory.libraryapp.member.Member;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {

	// We will consider that ISBN identifies at the same time
	// the book and the copy as mentioned in the exercise description
	private Map<ISBN, Book> availableBooks = new HashMap<>();
	private Map<ISBN, Triplet<Member, Book, LocalDate>> borrowedBooks = new HashMap<>();

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
			throw new IllegalArgumentException("book parameter is null");
		}
		availableBooks.putIfAbsent(book.getIsbn(), book);
		borrowedBooks.remove(book.getIsbn());
	}

	public void saveBookBorrow(Member member, Book book, LocalDate borrowedAt) {
		if (member == null || book == null || borrowedAt == null) {
			throw new IllegalArgumentException("member parameter, book parameter or borowedAt parameter is null");
		}
		borrowedBooks.putIfAbsent(book.getIsbn(), new Triplet<>(member, book, borrowedAt));
		availableBooks.remove(book.getIsbn());
	}

	public LocalDate findBorrowedBookDate(Book book) {
		Triplet<Member, Book, LocalDate> triplet = borrowedBooks.get(book.getIsbn());
		if (triplet != null) {
			return triplet.getValue2();
		}
		return null;
	}

	public boolean isMemberLate(Member member) {
		if (member == null) {
			throw new IllegalArgumentException("member parameter is null");
		}
		return borrowedBooks.entrySet().stream()
				.anyMatch(e -> member.equals(e.getValue().getValue0()) && member.isLate(e.getValue().getValue2()));
	}
}
