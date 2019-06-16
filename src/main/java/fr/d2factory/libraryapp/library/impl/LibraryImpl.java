package fr.d2factory.libraryapp.library.impl;

import java.time.Duration;
import java.time.LocalDate;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.library.Library;
import fr.d2factory.libraryapp.library.exception.BookIsNotAvailableException;
import fr.d2factory.libraryapp.library.exception.HasLateBooksException;
import fr.d2factory.libraryapp.member.Member;

public class LibraryImpl implements Library {
	
	private BookRepository bookRepository = null;
	
	public LibraryImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
		Book book = bookRepository.findBook(isbnCode);
		if (book == null) {
			throw new BookIsNotAvailableException();
		}
		bookRepository.saveBookBorrow(book, borrowedAt);
		return book;
	}

	@Override
	public void returnBook(Book book, Member member) {
		LocalDate borrowingDate = bookRepository.findBorrowedBookDate(book);
		member.payBook((int)Duration.between(borrowingDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays());
		bookRepository.removeBookBorrow(book);
	}

}
