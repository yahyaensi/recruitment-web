package fr.d2factory.libraryapp.library.impl;

import java.time.LocalDate;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.library.Library;
import fr.d2factory.libraryapp.library.exception.HasLateBooksException;
import fr.d2factory.libraryapp.member.Member;

public class LibraryImpl implements Library {
	
	private BookRepository bookRepository = null;
	
	public LibraryImpl(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt) throws HasLateBooksException {
		return bookRepository.findBook(isbnCode);
	}

	@Override
	public void returnBook(Book book, Member member) {
		// TODO Auto-generated method stub
		
	}

}
