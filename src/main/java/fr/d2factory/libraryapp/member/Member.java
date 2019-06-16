package fr.d2factory.libraryapp.member;

import java.math.BigDecimal;
import java.time.LocalDate;

import fr.d2factory.libraryapp.library.Library;

/**
 * A member is a person who can borrow and return books to a {@link Library} A
 * member can be either a student or a resident
 */
public abstract class Member {
	/**
	 * An initial sum of money the member has
	 */
	protected BigDecimal wallet;

	protected Member(BigDecimal initialWallet) {
		this.wallet = initialWallet;
	}

	/**
	 * The member should pay their books when they are returned to the library
	 *
	 * @param numberOfDays
	 *            the number of days they kept the book
	 */
	public abstract void payBook(int numberOfDays);

	/**
	 * Returns true if the member is late, false otherwise.
	 *
	 * @param borrowingDate
	 *            a borrowing date of a book
	 */
	public abstract boolean isLate(LocalDate borrowingDate);

	public BigDecimal getWallet() {
		return wallet;
	}

	public void setWallet(BigDecimal wallet) {
		this.wallet = wallet;
	}
}
