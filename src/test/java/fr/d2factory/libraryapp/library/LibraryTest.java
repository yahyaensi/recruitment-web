package fr.d2factory.libraryapp.library;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.library.exception.BookIsNotAvailableException;
import fr.d2factory.libraryapp.library.impl.LibraryImpl;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.ResidentMember;
import fr.d2factory.libraryapp.member.StudentMember;

public class LibraryTest {

	private Library library;

	private BookRepository bookRepository;

	private ClassLoader loader = ClassLoader.getSystemClassLoader();

	@Before
	public void setup() throws IOException, URISyntaxException {
		// instantiate the library and the repository
		bookRepository = new BookRepository();
		// load test books from books.json
		String json = Files.lines(Paths.get(loader.getResource("books.json").toURI())).parallel()
				.collect(Collectors.joining());
		List<Book> bookList = Arrays.asList(new Gson().fromJson(json, Book[].class));
		bookRepository.addBooks(bookList);

		library = new LibraryImpl(bookRepository);
	}

	@Test
	public void member_can_borrow_a_book_if_book_is_available() {
		Long isbn = 968787565445l;
		LocalDate borrowingDate = LocalDate.now();
		Member member = new StudentMember(new BigDecimal("10"));
		Book book = library.borrowBook(isbn, member, borrowingDate);
		assertNotNull("The book exists in the library and it's borrowed", book);
		assertEquals("The returned book has the demanded isbn", 968787565445l, book.getIsbn().getIsbnCode());
		Book unavailableBook = bookRepository.findBook(isbn);
		assertNull("The book was removed from available book list", unavailableBook);
		LocalDate bookBorrowingDate = bookRepository.findBorrowedBookDate(book);
		assertEquals("The book was moved to barrowed book list", borrowingDate, bookBorrowingDate);
	}

	@Test(expected = BookIsNotAvailableException.class)
	public void borrowed_book_is_no_longer_available() {
		Long isbn = 968787565445l;
		Member member = new StudentMember(new BigDecimal("10"));
		library.borrowBook(isbn, member, LocalDate.now());
		library.borrowBook(isbn, member, LocalDate.now());
	}

	@Test
	public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
		Long isbn = 968787565445l;
		int borrowingDays = 60;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member residentMember = new ResidentMember(initialWallet);
		Book borrowedBook = library.borrowBook(isbn, residentMember, borrowingDate);
		library.returnBook(borrowedBook, residentMember);
		assertEquals("Resident was taxed 10 cents for each day he kept the book",
				new BigDecimal("4.0"),
				residentMember.getWallet());
	}
	
	@Test
	public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
		Long isbn = 968787565445l;
		int borrowingDays = 70;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member residentMember = new ResidentMember(initialWallet);
		Book borrowedBook = library.borrowBook(isbn, residentMember, borrowingDate);
		library.returnBook(borrowedBook, residentMember);
		assertEquals("Resident was taxed 10 cents for each day he kept the book",
				new BigDecimal("2.0"),
				residentMember.getWallet());
	}

	@Test
	public void students_pay_10_cents_the_first_30days() {
		fail("Implement me");
	}

	@Test
	public void students_in_1st_year_are_not_taxed_for_the_first_15days() {
		fail("Implement me");
	}

	@Test
	public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
		fail("Implement me");
	}

	@Test
	public void members_cannot_borrow_book_if_they_have_late_books() {
		fail("Implement me");
	}
}
