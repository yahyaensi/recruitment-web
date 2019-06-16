package fr.d2factory.libraryapp.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import fr.d2factory.libraryapp.library.exception.HasLateBooksException;
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
		Member member = new ResidentMember(new BigDecimal("10"));
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
		Member member = new ResidentMember(new BigDecimal("10"));
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
		assertEquals("Resident payed 20 cents for each dat he kept the book after initial 60 days",
				new BigDecimal("2.0"),
				residentMember.getWallet());
	}

	@Test
	public void students_not_in_1st_year_pay_10_cents_the_first_30days() {
		Long isbn = 968787565445l;
		int borrowingDays = 30;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member studentMember = new StudentMember(false, initialWallet);
		Book borrowedBook = library.borrowBook(isbn, studentMember, borrowingDate);
		library.returnBook(borrowedBook, studentMember);
		assertEquals("Student not in 1st year payed 10 cents the first 30 days",
				new BigDecimal("7.0"),
				studentMember.getWallet());
	}
	
	@Test
	public void students_not_in_1st_year_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
		Long isbn = 968787565445l;
		int borrowingDays = 40;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member studentMember = new StudentMember(false, initialWallet);
		Book borrowedBook = library.borrowBook(isbn, studentMember, borrowingDate);
		library.returnBook(borrowedBook, studentMember);
		assertEquals("Student not in 1st year payed 10 cents the first 30 days",
				new BigDecimal("5.5"),
				studentMember.getWallet());
	}

	@Test
	public void students_in_1st_year_are_not_taxed_for_the_first_15days() {
		Long isbn = 968787565445l;
		int borrowingDays = 20;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member studentMember = new StudentMember(true, initialWallet);
		Book borrowedBook = library.borrowBook(isbn, studentMember, borrowingDate);
		library.returnBook(borrowedBook, studentMember);
		assertEquals("Student in 1st year is not taxed for the first 15 days",
				new BigDecimal("9.5"),
				studentMember.getWallet());
	}

	@Test
	public void students_in_1st_year_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
		Long isbn = 968787565445l;
		int borrowingDays = 40;
		LocalDate borrowingDate = LocalDate.now().minusDays(borrowingDays);
		BigDecimal initialWallet = new BigDecimal("10");
		Member studentMember = new StudentMember(true, initialWallet);
		Book borrowedBook = library.borrowBook(isbn, studentMember, borrowingDate);
		library.returnBook(borrowedBook, studentMember);
		assertEquals("Student in 1st year payed 15 cents for each day he kept the book after the initial 30 days",
				new BigDecimal("7.0"),
				studentMember.getWallet());
	}
	
	@Test
	public void returned_book_can_be_borrowed_again() {
		Long isbn = 968787565445l;
		LocalDate borrowingDate = LocalDate.now();
		Member member = new ResidentMember(new BigDecimal("10"));
		Book book = library.borrowBook(isbn, member, borrowingDate.minusDays(10));
		library.returnBook(book, member);
		Book bookBorrowedAgain = library.borrowBook(isbn, member, borrowingDate);
		assertNotNull("The book exists in the library and it's borrowed", bookBorrowedAgain);
		assertEquals("The returned book has the demanded isbn", 968787565445l, bookBorrowedAgain.getIsbn().getIsbnCode());
	}

	@Test(expected = HasLateBooksException.class)
	public void residents_cannot_borrow_book_if_they_have_late_books() {
		LocalDate nowDate = LocalDate.now();
		Member member = new ResidentMember(new BigDecimal("10"));
		library.borrowBook(968787565445l, member, nowDate.minusDays(61));
		library.borrowBook(3326456467846l, member, nowDate);
	}
}
