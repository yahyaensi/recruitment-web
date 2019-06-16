package fr.d2factory.libraryapp.library;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import fr.d2factory.libraryapp.library.impl.LibraryImpl;
import fr.d2factory.libraryapp.member.Member;
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
		Member member = new StudentMember();
		Book book = library.borrowBook(968787565445l, member, LocalDate.now());
		assertNotNull("The book exists in the library", book);
		assertEquals("The returned book has the demanded isbn", 968787565445l, book.getIsbn().getIsbnCode());
	}

	@Test
	public void borrowed_book_is_no_longer_available() {
		fail("Implement me");
	}

	@Test
	public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
		fail("Implement me");
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
	public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
		fail("Implement me");
	}

	@Test
	public void members_cannot_borrow_book_if_they_have_late_books() {
		fail("Implement me");
	}
}
